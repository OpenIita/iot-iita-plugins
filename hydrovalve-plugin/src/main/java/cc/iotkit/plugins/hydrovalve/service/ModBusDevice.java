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

package cc.iotkit.plugins.hydrovalve.service;

import cc.iotkit.common.utils.StringUtils;
import cc.iotkit.plugin.core.thing.IDevice;
import cc.iotkit.plugin.core.thing.actions.ActionResult;
import cc.iotkit.plugin.core.thing.actions.down.DeviceConfig;
import cc.iotkit.plugin.core.thing.actions.down.PropertyGet;
import cc.iotkit.plugin.core.thing.actions.down.PropertySet;
import cc.iotkit.plugin.core.thing.actions.down.ServiceInvoke;
import cc.iotkit.plugins.hydrovalve.analysis.ModBusConstants;
import cc.iotkit.plugins.hydrovalve.analysis.ModBusEntity;
import cc.iotkit.plugins.hydrovalve.analysis.ModBusRtuAnalysis;
import cc.iotkit.plugins.hydrovalve.utils.ByteUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @Author：tfd
 * @Date：2024/1/10 11:06
 */
@Service
public class ModBusDevice implements IDevice {

    @Autowired
    private ModbusVerticle modbusVerticle;

    ModBusRtuAnalysis analysis=new ModBusRtuAnalysis();

    @Override
    public ActionResult config(DeviceConfig action) {
        return ActionResult.builder().code(0).reason("").build();
    }

    @Override
    public ActionResult propertyGet(PropertyGet action) {
        return null;
    }

    @Override
    public ActionResult propertySet(PropertySet action) {
        ModBusEntity read=new ModBusEntity();
        String devAddr=action.getDeviceName().split("_")[1];
        read.setFunc(ModBusConstants.FUN_CODE6);
        read.setDevAddr(Byte.parseByte(devAddr));
        Integer addr=0;
        for (Map.Entry<String, ?> entry : action.getParams().entrySet()) {
            int val = Integer.parseInt((String) entry.getValue());
            String a1= StringUtils.leftPad(addr.toHexString(addr),4,'0')+StringUtils.leftPad(addr.toHexString(val),4,'0');
            read.setData(ByteUtils.hexStrToBinaryStr(a1));
            byte[] msg = analysis.packCmd4Entity(read);
            modbusVerticle.sendMsg(msg);
        }
        return ActionResult.builder().code(0).reason("success").build();
    }

    @Override
    public ActionResult serviceInvoke(ServiceInvoke action) {
        return null;
    }
}
