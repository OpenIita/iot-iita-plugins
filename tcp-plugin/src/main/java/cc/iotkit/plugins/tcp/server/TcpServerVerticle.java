package cc.iotkit.plugins.tcp.server;


import cc.iotkit.plugin.core.IPluginScript;
import cc.iotkit.plugin.core.thing.IThingService;
import cc.iotkit.plugin.core.thing.actions.ActionResult;
import cc.iotkit.plugin.core.thing.actions.DeviceState;
import cc.iotkit.plugin.core.thing.actions.up.DeviceRegister;
import cc.iotkit.plugin.core.thing.actions.up.DeviceStateChange;
import cc.iotkit.plugin.core.thing.actions.up.PropertyReport;
import cc.iotkit.plugins.tcp.cilent.VertxTcpClient;
import cc.iotkit.plugins.tcp.conf.TcpServerConfig;
import cc.iotkit.plugins.tcp.parser.DataDecoder;
import cc.iotkit.plugins.tcp.parser.DataEncoder;
import cc.iotkit.plugins.tcp.parser.DataPackage;
import cc.iotkit.plugins.tcp.parser.DataReader;
import cc.iotkit.script.IScriptEngine;
import cn.hutool.core.util.IdUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.gitee.starblues.bootstrap.annotation.AutowiredType;
import com.gitee.starblues.core.PluginInfo;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetServer;
import io.vertx.core.net.NetServerOptions;
import io.vertx.core.net.NetSocket;
import io.vertx.core.parsetools.RecordParser;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author huangwenl
 * @date 2022-10-13
 */
@Slf4j
@Service
public class TcpServerVerticle extends AbstractVerticle {

    @Getter
    @Setter
    private TcpServerConfig config;

    private VertxTcpServer tcpServer;

    private final Map<String, VertxTcpClient> clientMap = new ConcurrentHashMap<>();

    private final Map<String, String> dnToPk = new HashMap<>();

    private final Map<String, Long> heartbeatDevice = new HashMap<>();

    private ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;

    private ScheduledThreadPoolExecutor offlineCheckExecutor;

    @Setter
    private long keepAliveTimeout = Duration.ofSeconds(30).toMillis();

    private Collection<NetServer> tcpServers;

    @Getter
    private IScriptEngine scriptEngine;

    @Autowired
    private PluginInfo pluginInfo;

    @Autowired
    @AutowiredType(AutowiredType.Type.MAIN_PLUGIN)
    private IPluginScript pluginScript;

    @Autowired
    @AutowiredType(AutowiredType.Type.MAIN_PLUGIN)
    private IThingService thingService;

    @Override
    public void start() {
        tcpServer = new VertxTcpServer();
        initConfig();
        initTcpServer();
    }

    @Override
    public void stop() {
        tcpServer.shutdown();
        scheduledThreadPoolExecutor.shutdown();
        offlineCheckExecutor.shutdown();
    }

    /**
     * 创建配置文件
     */
    public void initConfig() {
        //获取脚本引擎
        scriptEngine = pluginScript.getScriptEngine(pluginInfo.getPluginId());
    }


    /**
     * 初始TCP服务
     */
    private void initTcpServer() {
        int instance = Math.max(2, config.getInstance());
        List<NetServer> instances = new ArrayList<>(instance);
        for (int i = 0; i < instance; i++) {
            instances.add(vertx.createNetServer(
                    new NetServerOptions().setHost(config.getHost())
                            .setPort(config.getPort())
            ));
        }
        // 根据解析类型配置数据解析器
        tcpServer.setServer(instances);
        // 针对JVM做的多路复用优化
        // 多个server listen同一个端口，每个client连接的时候vertx会分配
        // 一个connection只能在一个server中处理
        for (NetServer netServer : instances) {
            netServer.listen(config.createSocketAddress(), result -> {
                if (result.succeeded()) {
                    log.info("tcp server startup on {}", result.result().actualPort());
                } else {
                    log.error("tcp server startup error", result.cause());
                }
            });
        }
    }

    public void sendMsg(String addr, Buffer msg) {
        VertxTcpClient tcpClient = clientMap.get(addr);
        if (tcpClient != null) {
            tcpClient.sendMessage(msg);
        }
    }

    @Scheduled(fixedRate = 40, timeUnit = TimeUnit.SECONDS)
    private void offlineCheckTask() {
        log.info("keepClientTask");
        Set<String> clients = new HashSet<>(clientMap.keySet());
        for (String key : clients) {
            VertxTcpClient client = clientMap.get(key);
            if (!client.isOnline()) {
                client.shutdown();
            }
        }

        heartbeatDevice.keySet().iterator().forEachRemaining(addr -> {
            Long time = heartbeatDevice.get(addr);
            //心跳超时，判定为离线
            if (System.currentTimeMillis() - time > keepAliveTimeout * 2) {
                heartbeatDevice.remove(addr);
                //离线上报
                thingService.post(pluginInfo.getPluginId(), DeviceStateChange.builder()
                        .id(IdUtil.simpleUUID())
                        .productKey(dnToPk.get(addr))
                        .deviceName(addr)
                        .state(DeviceState.OFFLINE)
                        .time(System.currentTimeMillis())
                        .build());
            }
        });
    }

    class VertxTcpServer {

        /**
         * 为每个NetServer添加connectHandler
         *
         * @param servers 创建的所有NetServer
         */
        public void setServer(Collection<NetServer> servers) {
            if (tcpServers != null && !tcpServers.isEmpty()) {
                shutdown();
            }
            tcpServers = servers;
            for (NetServer tcpServer : tcpServers) {
                tcpServer.connectHandler(this::acceptTcpConnection);
            }
        }

        /**
         * TCP连接处理逻辑
         *
         * @param socket socket
         */
        protected void acceptTcpConnection(NetSocket socket) {
            // 客户端连接处理
            String clientId = IdUtil.simpleUUID() + "_" + socket.remoteAddress();
            VertxTcpClient client = new VertxTcpClient(clientId);
            client.setKeepAliveTimeoutMs(keepAliveTimeout);
            try {
                // TCP异常和关闭处理
                socket.exceptionHandler(err -> log.error("tcp server client [{}] error", socket.remoteAddress(), err)).closeHandler(nil -> {
                    log.debug("tcp server client [{}] closed", socket.remoteAddress());
                    client.shutdown();
                });
                // 这个地方是在TCP服务初始化的时候设置的 parserSupplier
                client.setKeepAliveTimeoutMs(keepAliveTimeout);
                client.setSocket(socket);
                RecordParser parser = DataReader.getParser(buffer -> {
                    try {
                        DataPackage data = DataDecoder.decode(buffer);
                        String addr = data.getAddr();
                        int code = data.getCode();
                        if (code == 0x10) {
                            clientMap.put(addr, client);
                            //设备注册
                            String pk = dnToPk.put(addr, new String(data.getPayload()));
                            ActionResult rst = thingService.post(pluginInfo.getPluginId(), DeviceRegister.builder()
                                    .id(IdUtil.simpleUUID())
                                    .productKey(pk)
                                    .deviceName(addr)
                                    .time(System.currentTimeMillis())
                                    .build());
                            if (rst.getCode() == 0) {
                                //回复注册成功
                                sendMsg(addr, DataEncoder.encode(
                                        DataPackage.builder()
                                                .addr(addr)
                                                .code(DataPackage.CODE_REGISTER_REPLY)
                                                .mid(data.getMid())
                                                .payload(Buffer.buffer().appendInt(0).getBytes())
                                                .build()
                                ));
                            }
                            return;
                        }

                        if (code == 0x20) {
                            //心跳
                            if (!heartbeatDevice.containsKey(addr)) {
                                //第一次心跳，上线
                                thingService.post(pluginInfo.getPluginId(), DeviceStateChange.builder()
                                        .id(IdUtil.simpleUUID())
                                        .productKey(dnToPk.get(addr))
                                        .deviceName(addr)
                                        .state(DeviceState.ONLINE)
                                        .time(System.currentTimeMillis())
                                        .build());
                            }
                            heartbeatDevice.put(addr, System.currentTimeMillis());
                            return;
                        }

                        if (code == 0x30) {
                            //设备数据上报
                            //数据上报也作为心跳
                            heartbeatDevice.put(addr, System.currentTimeMillis());
                            //调用脚本解码
                            Map<String, Object> rst = scriptEngine.invokeMethod(new TypeReference<>() {
                            }, "decode", data);
                            if (rst == null) {
                                return;
                            }
                            //属性上报
                            thingService.post(pluginInfo.getPluginId(), PropertyReport.builder()
                                    .id(IdUtil.simpleUUID())
                                    .productKey(dnToPk.get(addr))
                                    .deviceName(addr)
                                    .params(rst)
                                    .time(System.currentTimeMillis())
                                    .build());
                        }

                        //未注册断开连接
                        if (!clientMap.containsKey(data.getAddr())) {
                            socket.close();
                        }

                    } catch (Exception e) {
                        log.error("handler error", e);
                    }
                });
                client.setParser(parser);
                log.debug("accept tcp client [{}] connection", socket.remoteAddress());
            } catch (Exception e) {
                log.error("create tcp server client error", e);
                client.shutdown();
            }
        }

        public void shutdown() {
            if (tcpServers == null) {
                return;
            }
            for (NetServer tcpServer : tcpServers) {
                try {
                    tcpServer.close();
                } catch (Exception e) {
                    log.warn("close tcp server error", e);
                }
            }
            tcpServers = null;
        }

    }
}