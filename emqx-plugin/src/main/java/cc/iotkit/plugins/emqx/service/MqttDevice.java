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

package cc.iotkit.plugins.emqx.service;

import cc.iotkit.common.enums.ErrCode;
import cc.iotkit.common.exception.BizException;
import cc.iotkit.plugin.core.thing.IDevice;
import cc.iotkit.plugin.core.thing.actions.ActionResult;
import cc.iotkit.plugin.core.thing.actions.down.DeviceConfig;
import cc.iotkit.plugin.core.thing.actions.down.PropertyGet;
import cc.iotkit.plugin.core.thing.actions.down.PropertySet;
import cc.iotkit.plugin.core.thing.actions.down.ServiceInvoke;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.vertx.core.json.JsonObject;
import io.vertx.mqtt.MqttClient;
import lombok.Setter;
import org.springframework.stereotype.Service;

/**
 * mqtt设备下行接口
 *
 * @author sjg
 */
@Service
public class MqttDevice implements IDevice {

    @Setter
    private MqttClient client;

    @Override
    public ActionResult config(DeviceConfig action) {
        return ActionResult.builder().code(0).reason("").build();
    }

    @Override
    public ActionResult propertyGet(PropertyGet action) {
        String topic = String.format("/sys/%s/%s/c/service/property/get", action.getProductKey(), action.getDeviceName());
        return send(
                topic,
                new JsonObject()
                        .put("id", action.getId())
                        .put("method", "thing.service.property.get")
                        .put("params", action.getKeys())
        );
    }

    @Override
    public ActionResult propertySet(PropertySet action) {
        String topic = String.format("/sys/%s/%s/c/service/property/set", action.getProductKey(), action.getDeviceName());
        return send(
                topic,
                new JsonObject()
                        .put("id", action.getId())
                        .put("method", "thing.service.property.set")
                        .put("params", action.getParams())
        );
    }

    @Override
    public ActionResult serviceInvoke(ServiceInvoke action) {
        String topic = String.format("/sys/%s/%s/c/service/%s", action.getProductKey(), action.getDeviceName(), action.getName());
        return send(
                topic,
                new JsonObject()
                        .put("id", action.getId())
                        .put("method", "thing.service." + action.getName())
                        .put("params", action.getParams())
        );
    }

    private ActionResult send(String topic, JsonObject payload) {
        try {
            client.publish(topic, payload.toBuffer(), MqttQoS.AT_LEAST_ONCE, false, false);
            return ActionResult.builder().code(0).reason("").build();
        } catch (BizException e) {
            return ActionResult.builder().code(e.getCode()).reason(e.getMessage()).build();
        } catch (Exception e) {
            return ActionResult.builder().code(ErrCode.UNKNOWN_EXCEPTION.getKey()).reason(e.getMessage()).build();
        }
    }

}
