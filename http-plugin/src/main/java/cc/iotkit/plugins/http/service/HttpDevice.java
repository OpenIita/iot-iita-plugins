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

package cc.iotkit.plugins.http.service;

import cc.iotkit.plugin.core.thing.IDevice;
import cc.iotkit.plugin.core.thing.actions.ActionResult;
import cc.iotkit.plugin.core.thing.actions.down.DeviceConfig;
import cc.iotkit.plugin.core.thing.actions.down.PropertyGet;
import cc.iotkit.plugin.core.thing.actions.down.PropertySet;
import cc.iotkit.plugin.core.thing.actions.down.ServiceInvoke;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * http设备下行接口
 *
 * @author sjg
 */
@Service
public class HttpDevice implements IDevice {

    @Autowired
    private HttpVerticle httpVerticle;

    @Override
    public ActionResult config(DeviceConfig action) {
        return ActionResult.builder().code(0).reason("").build();
    }

    @Override
    public ActionResult propertyGet(PropertyGet action) {
        throw new UnsupportedOperationException("不支持该功能");
    }

    @Override
    public ActionResult propertySet(PropertySet action) {
        throw new UnsupportedOperationException("不支持该功能");
    }

    @Override
    public ActionResult serviceInvoke(ServiceInvoke action) {
        throw new UnsupportedOperationException("不支持该功能");
    }

}
