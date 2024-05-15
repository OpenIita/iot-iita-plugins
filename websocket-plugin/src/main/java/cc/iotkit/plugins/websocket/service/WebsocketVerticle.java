/*
 *
 *  * | Licensed 未经许可不能去掉「OPENIITA」相关版权
 *  * +----------------------------------------------------------------------
 *  * | Author: xw2sy@163.com
 *  * +----------------------------------------------------------------------
 *
 *  Copyright [2024] [OPENIITA]
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * /
 */
package cc.iotkit.plugins.websocket.service;

import cc.iotkit.common.utils.JsonUtils;
import cc.iotkit.common.utils.StringUtils;
import cc.iotkit.plugin.core.thing.IThingService;
import cc.iotkit.plugin.core.thing.actions.ActionResult;
import cc.iotkit.plugin.core.thing.actions.DeviceState;
import cc.iotkit.plugin.core.thing.actions.up.DeviceRegister;
import cc.iotkit.plugin.core.thing.actions.up.DeviceStateChange;
import cc.iotkit.plugin.core.thing.actions.up.PropertyReport;
import cc.iotkit.plugin.core.thing.actions.up.SubDeviceRegister;
import cc.iotkit.plugins.websocket.analysis.DataAnalysis;
import cc.iotkit.plugins.websocket.beans.Event;
import cc.iotkit.plugins.websocket.conf.WebsocketConfig;
import cn.hutool.core.util.ObjectUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.gitee.starblues.bootstrap.annotation.AutowiredType;
import com.gitee.starblues.core.PluginInfo;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.net.PemKeyCertOptions;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author tfd
 */
@Slf4j
@Component
@Data
public class WebsocketVerticle extends AbstractVerticle {

    private HttpServer httpServer;
    private final Map<String, ServerWebSocket> wsClients = new ConcurrentHashMap<>();

    private static final Map<String, Boolean> CONNECT_POOL = new ConcurrentHashMap<>();
    private static final Map<String, Boolean> DEVICE_ONLINE = new ConcurrentHashMap<>();

    private Map<String, String> tokens=new HashMap<>();

    private WebsocketConfig config;

    @Autowired
    @AutowiredType(AutowiredType.Type.MAIN_PLUGIN)
    private IThingService thingService;

    @Autowired
    private PluginInfo pluginInfo;

    @Override
    public void start() {
        Executors.newSingleThreadScheduledExecutor().schedule(this::initWsServer, 3, TimeUnit.SECONDS);
    }

    private void initWsServer() {
        HttpServerOptions options = new HttpServerOptions()
                .setPort(config.getPort());
        if (config.isSsl()) {
            options = options.setSsl(true)
                    .setKeyCertOptions(new PemKeyCertOptions()
                            .setKeyPath(config.getSslKey())
                            .setCertPath(config.getSslCert()));
        }

        httpServer = vertx.createHttpServer(options).webSocketHandler(wsClient -> {
            log.info("webSocket client connect sessionId:{},path={}", wsClient.textHandlerID(), wsClient.path());
            String deviceKey = wsClient.path().replace("/","");
            String[] strArr=deviceKey.split("_");
            if(StringUtils.isBlank(deviceKey)||strArr.length!=2){
                log.warn("陌生连接，拒绝");
                wsClient.reject();
                return;
            }
            wsClient.writeTextMessage("connect succes! please auth!");
            wsClient.textMessageHandler(message -> {
                HashMap<String,Object> msg;
                try{
                    msg=JsonUtils.parseObject(message,HashMap.class);
                }catch (Exception e){
                    log.warn("数据格式异常");
                    wsClient.writeTextMessage("data err");
                    return;
                }
                log.info("webSocket receive message:{}",message);
                if(wsClients.containsKey(deviceKey)){
                    if("ping".equals(msg.get("type"))){//心跳
                        msg.put("type","pong");
                        wsClient.writeTextMessage(JsonUtils.toJsonString(msg));
                        return;
                    }else if("register".equals(msg.get("type"))){//注册
                        ActionResult result;
                        List<String> subDevices = null;
                        if(ObjectUtil.isNotNull(msg.get("subDevices"))){
                            subDevices=JsonUtils.parseObject(JsonUtils.toJsonString(msg.get("subDevices")),List.class);
                            List<DeviceRegister> subsRe =new ArrayList<>();
                            for(String sub:subDevices){
                                String subName=sub.split("_")[0];
                                String subKey=sub.split("_")[1];
                                subsRe.add(DeviceRegister.builder()
                                        .productKey(subKey)
                                        .deviceName(subName)
                                        .build());
                            }
                            //带子设备注册
                            result = thingService.post(
                                    pluginInfo.getPluginId(),
                                    SubDeviceRegister.builder()
                                            .productKey(strArr[1])
                                            .deviceName(strArr[0])
                                            .subs(subsRe)
                                            .build()
                            );
                        }else{
                            //设备注册
                            result = thingService.post(
                                    pluginInfo.getPluginId(),
                                    DeviceRegister.builder()
                                            .productKey(strArr[1])
                                            .deviceName(strArr[0])
                                            .build()
                            );
                        }
                        if(ObjectUtil.isNotNull(result)&&result.getCode()==0){
                            log.info("设备上线");
                            //父设备上线
                            thingService.post(
                                    pluginInfo.getPluginId(),
                                    DeviceStateChange.builder()
                                            .productKey(strArr[1])
                                            .deviceName(strArr[0])
                                            .state(DeviceState.ONLINE)
                                            .build()
                            );
                            //子设备上线
                            if(ObjectUtil.isNotNull(subDevices)){
                                log.info("子设备上线");
                                for(String sub:subDevices){
                                    String subName=sub.split("_")[0];
                                    String subKey=sub.split("_")[1];
                                    thingService.post(
                                            pluginInfo.getPluginId(),
                                            DeviceStateChange.builder()
                                                    .productKey(subKey)
                                                    .deviceName(subName)
                                                    .state(DeviceState.ONLINE)
                                                    .build()
                                    );

                                }
                            }
                            //注册成功
                            Map<String,Object> ret=new HashMap<>();
                            ret.put("id",msg.get("id"));
                            ret.put("type",msg.get("type"));
                            ret.put("result","succes");
                            wsClient.writeTextMessage(JsonUtils.toJsonString(ret));
                            return;
                        }else{
                            //注册失败
                            Map<String,Object> ret=new HashMap<>();
                            ret.put("id",msg.get("id"));
                            ret.put("type",msg.get("type"));
                            ret.put("result","fail");
                            wsClient.writeTextMessage(JsonUtils.toJsonString(ret));
                            return;
                        }
                    }else{//数据处理
                        if("event".equals(msg.get("type"))){
                            Event event= null;
                            try {
                                event = DataAnalysis.mapper.readValue(JsonUtils.toJsonString(msg.get("event")), new TypeReference<Event>() {});
                            } catch (JsonProcessingException e) {
                                e.printStackTrace();
                            }
                            String[] keys=event.getData().getEntityId().split("_");
                            if(DataAnalysis.EVENT_STATE_CHANGED.equals(event.getEventType())){
                                thingService.post(pluginInfo.getPluginId(),
                                        PropertyReport.builder().productKey(keys[1])
                                                .deviceName(keys[0])
                                                .params(DataAnalysis.stateChangedEvent(event.getData()
                                                        .getOldState(),event.getData().getNewState())).build());

                            }
                            //注册失败
                            Map<String,Object> ret=new HashMap<>();
                            ret.put("id",msg.get("id"));
                            ret.put("type",msg.get("type"));
                            ret.put("result","succes");
                            wsClient.writeTextMessage(JsonUtils.toJsonString(ret));
                            return;
                        }
                    }
                }else if(msg!=null&&"auth".equals(msg.get("type"))){
                    Set<String> tokenKey=tokens.keySet();
                    for(String key:tokenKey){
                        if(ObjectUtil.isNotNull(msg.get(key))&&tokens.get(key).equals(msg.get(key))){
                            //保存设备与连接关系
                            log.info("认证通过");
                            if(!wsClients.containsKey(deviceKey)){
                                wsClients.put(deviceKey, wsClient);
                            }
                            wsClient.writeTextMessage("auth succes");
                            return;
                        }
                    }
                    log.warn("认证失败，拒绝");
                    wsClient.writeTextMessage("auth fail");
                    return;
                }else{
                    log.warn("认证失败，拒绝");
                    wsClient.writeTextMessage("auth fail");
                    return;
                }

            });
            wsClient.closeHandler(c -> {
                log.warn("client connection closed,deviceKey:{}", deviceKey);
                if(wsClients.containsKey(deviceKey)){
                    wsClients.remove(deviceKey);
                    thingService.post(
                            pluginInfo.getPluginId(),
                            DeviceStateChange.builder()
                                    .productKey(strArr[1])
                                    .deviceName(strArr[0])
                                    .state(DeviceState.OFFLINE)
                                    .build()
                    );
                }
            });
            wsClient.exceptionHandler(ex -> {
                log.warn("webSocket client connection exception,deviceKey:{}", deviceKey);
                if(wsClients.containsKey(deviceKey)){
                    wsClients.remove(deviceKey);
                    thingService.post(
                            pluginInfo.getPluginId(),
                            DeviceStateChange.builder()
                                    .productKey(strArr[1])
                                    .deviceName(strArr[0])
                                    .state(DeviceState.OFFLINE)
                                    .build()
                    );
                }
            });
        }).listen(config.getPort(), server -> {
            if (server.succeeded()) {
                log.info("webSocket server is listening on port " + config.getPort());
                if(config.getTokenKey()!=null&&config.getAccessToken()!=null){
                        tokens.put(config.getTokenKey(),config.getAccessToken());
                }
            } else {
                log.error("webSocket server on starting the server", server.cause());
            }
        });
    }

    @Override
    public void stop() {
        for (String deviceKey : wsClients.keySet()) {
            thingService.post(
                    pluginInfo.getPluginId(),
                    DeviceStateChange.builder()
                            .productKey(deviceKey.split("_")[1])
                            .deviceName(deviceKey.split("_")[0])
                            .state(DeviceState.OFFLINE)
                            .build()
            );
        }
        tokens.clear();
        httpServer.close(voidAsyncResult -> log.info("close webocket server..."));
    }

    public void send(String deviceKey,String msg) {
        ServerWebSocket wsClient = wsClients.get(deviceKey);
        String msgStr = JsonUtils.toJsonString(msg);
        log.info("send msg payload:{}", msgStr);
        Future<Void> result = wsClient.writeTextMessage(msgStr);
        result.onFailure(e -> log.error("webSocket server send msg failed", e));
    }

}
