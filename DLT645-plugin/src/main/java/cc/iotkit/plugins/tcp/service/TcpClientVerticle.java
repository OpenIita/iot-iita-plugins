package cc.iotkit.plugins.tcp.service;

import cc.iotkit.common.utils.JsonUtils;
import cc.iotkit.plugin.core.thing.IThingService;
import cc.iotkit.plugin.core.thing.actions.DeviceState;
import cc.iotkit.plugin.core.thing.actions.up.DeviceStateChange;
import cc.iotkit.plugin.core.thing.actions.up.PropertyReport;
import cc.iotkit.plugins.tcp.analysis.DLT645Analysis;
import cc.iotkit.plugins.tcp.analysis.DLT645Converter;
import cc.iotkit.plugins.tcp.analysis.DLT645FunCode;
import cc.iotkit.plugins.tcp.analysis.DLT645V2007Data;
import cc.iotkit.plugins.tcp.conf.TcpClientConfig;
import cc.iotkit.plugins.tcp.constants.DLT645Constant;
import cc.iotkit.plugins.tcp.utils.ByteUtils;
import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.IdUtil;
import com.gitee.starblues.bootstrap.annotation.AutowiredType;
import com.gitee.starblues.core.PluginInfo;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetClientOptions;
import io.vertx.core.net.NetSocket;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Author：tfd
 * @Date：2023/12/13 17:00
 */
@Slf4j
@Service
public class TcpClientVerticle extends AbstractVerticle {
    @Getter
    @Setter
    private TcpClientConfig config;

    private NetClient netClient;

    private NetSocket socket;

    @Autowired
    @AutowiredType(AutowiredType.Type.MAIN_PLUGIN)
    private IThingService thingService;

    @Autowired
    private PluginInfo pluginInfo;

    @Override
    public void start() {
        log.info("init start");
        initClient();
    }

    private void initClient() {
        NetClientOptions options = new NetClientOptions();
        options.setReconnectAttempts(Integer.MAX_VALUE);
        options.setReconnectInterval(20000L);
        netClient = vertx.createNetClient(options);
        log.info("start1 connect->"+config.getHost()+":"+config.getPort());
        netClient.connect(config.getPort(), config.getHost(), result -> {
            System.out.println("connect result:"+JsonUtils.toJsonString(result));
            if (result.succeeded()) {
                log.info("connect dlt645 server success");
                socket = result.result();
                stateChange(DeviceState.ONLINE);
                DLT645Analysis.inst().getTemplateByDIn(DLT645Constant.PRO_VER_2007);
                socket.handler(data->{
                    String hexStr= ByteUtils.byteArrayToHexString(data.getBytes(),false);
                    log.info("Received message:{}", hexStr);
                    Map<String, Object> ret = DLT645Analysis.unPackCmd2Map(ByteUtils.hexStringToByteArray(hexStr));
                    //获取功能码
                    Object func = ret.get(DLT645Analysis.FUN);
                    DLT645FunCode funCode = DLT645FunCode.decodeEntity((byte) func);
                    if(funCode.isError()){
                        log.error("message erroe:{}", hexStr);
                        return;
                    }
//                    //获取设备地址
                    byte[] adrrTmp = (byte[]) ret.get(DLT645Analysis.ADR);
                    byte[] addr = new byte[6];
                    ByteUtils.byteInvertedOrder(adrrTmp,addr);
//                    //获取数据
                    byte[] dat = (byte[]) ret.get(DLT645Analysis.DAT);
                    DLT645V2007Data dataEntity = new DLT645V2007Data();
                    dataEntity.decodeValue(dat, DLT645Analysis.din2entity);
                    Map<String, Object> params = new HashMap<>();
                    params.put("p"+dataEntity.getKey(),dataEntity.getValue());//数据标识
                    thingService.post(pluginInfo.getPluginId(), PropertyReport.builder().params(params).build());
                }).closeHandler(res->{
                    log.info("dlt645 tcp connection closed!");
                    stateChange(DeviceState.OFFLINE);
                    }
                ).exceptionHandler(res->{
                    log.info("dlt645 tcp connection exce!");
                    stateChange(DeviceState.OFFLINE);
                });
            } else {
                log.info("connect dlt645 tcp error", result.cause());
            }
        });
    }

    @Scheduled(fixedRate = 60, timeUnit = TimeUnit.SECONDS)
    private void readDataTask() {
        log.info("readData:"+socket);
        if(socket!=null){
            String msg=DLT645Converter.packData("000023092701","读数据","00000000");
            sendMsg(msg);
        }
    }

    private void stateChange(DeviceState state){
        thingService.post(pluginInfo.getPluginId(), DeviceStateChange.builder()
                .id(IdUtil.simpleUUID())
                .productKey("BRD3x4fkKxkaxXFt")
                .deviceName("123456789123")
                .state(state)
                .time(System.currentTimeMillis())
                .build());
    }

    public void sendMsg(String msg) {
        log.info("send msg data:{}", msg);
        Buffer data=Buffer.buffer(HexUtil.decodeHex(msg));
        socket.write(data, r -> {
            if (r.succeeded()) {
                log.info("msg send success:{}", msg);
            } else {
                log.error("msg send failed", r.cause());
            }
        });
    }

}
