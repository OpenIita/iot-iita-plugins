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

package cc.iotkit.plugins.mqtt.service;

import cc.iotkit.plugin.core.thing.IThingService;
import cc.iotkit.plugin.core.thing.actions.ActionResult;
import cc.iotkit.plugin.core.thing.actions.IDeviceAction;
import cc.iotkit.plugin.core.thing.model.ThingDevice;
import cc.iotkit.plugin.core.thing.model.ThingProduct;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 测试服务
 *
 * @author sjg
 */
@Slf4j
public class FakeThingService implements IThingService {

    /**
     * 添加测试产品
     */
    private static final Map<String, String> PRODUCTS = Map.of(
            "hbtgIA0SuVw9lxjB", "xdkKUymrEGSCYWswqCvSPyRSFvH5j7CU",
            "Rf4QSjbm65X45753", "xdkKUymrEGSCYWswqCvSPyRSFvH5j7CU",
            "cGCrkK7Ex4FESAwe", "xdkKUymrEGSCYWswqCvSPyRSFvH5j7CU"
    );

    /**
     * 添加测试设备
     */
    private static final Map<String, String> DEVICES = new HashMap<>();

    static {
        for (int i = 0; i < 10; i++) {
            DEVICES.put("TEST:GW:" + StringUtils.leftPad(i + "", 6, "0"), "hbtgIA0SuVw9lxjB");
            DEVICES.put("TEST_SW_" + StringUtils.leftPad(i + "", 6, "0"), "Rf4QSjbm65X45753");
            DEVICES.put("TEST_SC_" + StringUtils.leftPad(i + "", 6, "0"), "cGCrkK7Ex4FESAwe");
        }
    }

    @Override
    public ActionResult post(String pluginId, IDeviceAction action) {
        log.info("post action:{}", action);
        return ActionResult.builder().code(0).build();
    }

    @Override
    public ThingProduct getProduct(String pk) {
        return ThingProduct.builder()
                .productKey(pk)
                .productSecret(PRODUCTS.get(pk))
                .build();
    }

    @Override
    public ThingDevice getDevice(String dn) {
        return ThingDevice.builder()
                .productKey(DEVICES.get(dn))
                .deviceName(dn)
                .build();
    }

    @Override
    public Map<String, ?> getProperty(String dn) {
        return new HashMap<>(0);
    }
}
