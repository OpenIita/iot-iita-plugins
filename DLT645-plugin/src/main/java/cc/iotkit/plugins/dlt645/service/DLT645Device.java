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

import cc.iotkit.plugin.core.thing.IDevice;
import cc.iotkit.plugin.core.thing.actions.ActionResult;
import cc.iotkit.plugin.core.thing.actions.down.DeviceConfig;
import cc.iotkit.plugin.core.thing.actions.down.PropertyGet;
import cc.iotkit.plugin.core.thing.actions.down.PropertySet;
import cc.iotkit.plugin.core.thing.actions.down.ServiceInvoke;
import cc.iotkit.plugins.dlt645.analysis.DLT645Converter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author：tfd
 * @Date：2023/12/14 16:22
 */
@Service
public class DLT645Device implements IDevice {

    @Autowired
    private TcpClientVerticle dlt645Verticle;

    @Override
    public ActionResult config(DeviceConfig action) {
        return ActionResult.builder().code(0).reason("").build();
    }

    @Override
    public ActionResult propertyGet(PropertyGet action) {
        for (String key:action.getKeys()){
            String msg=DLT645Converter.packData(action.getDeviceName(),"读数据",key.replaceFirst("p",""));
            dlt645Verticle.sendMsg(msg);
        }
        return ActionResult.builder().code(0).reason("success").build();
    }

    @Override
    public ActionResult propertySet(PropertySet action) {
        return null;
    }

    @Override
    public ActionResult serviceInvoke(ServiceInvoke action) {
        return null;
    }
}
