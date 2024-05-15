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

package cc.iotkit.plugins.dlt645.service;

import cc.iotkit.plugin.core.thing.IThingService;
import cc.iotkit.plugin.core.thing.actions.ActionResult;
import cc.iotkit.plugin.core.thing.actions.IDeviceAction;
import cc.iotkit.plugin.core.thing.model.ThingDevice;
import cc.iotkit.plugin.core.thing.model.ThingProduct;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author：tfd
 * @Date：2023/12/13 16:56
 */
@Slf4j
public class FakeThingService implements IThingService {

    @Override
    public ActionResult post(String pluginId, IDeviceAction action) {
        log.info("post action:{}", action);
        return ActionResult.builder().code(0).build();
    }

    @Override
    public ThingProduct getProduct(String pk) {
        return ThingProduct.builder()
                .productKey("PjmkANSTDt85bZPj")
                .productSecret("aaaaaaaa")
                .build();
    }

    @Override
    public ThingDevice getDevice(String dn) {
        return ThingDevice.builder()
                .productKey("PjmkANSTDt85bZPj")
                .deviceName(dn)
                .build();
    }

    @Override
    public Map<String, ?> getProperty(String dn) {
        return new HashMap<>(0);
    }
}