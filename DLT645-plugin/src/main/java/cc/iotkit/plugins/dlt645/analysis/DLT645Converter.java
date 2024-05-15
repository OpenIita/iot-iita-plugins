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

package cc.iotkit.plugins.dlt645.analysis;

import cc.iotkit.plugins.dlt645.constants.DLT645Constant;
import cc.iotkit.plugins.dlt645.utils.ByteUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;


/**
 * @Author：tfd
 * @Date：2023/12/13 17:54
 */
@Slf4j
@Data
public class DLT645Converter {

    public static String packData(String deviceAddress,String funCode,String dataIdentifier){
        // 对设备地址进行编码
        byte[] tmp = ByteUtils.hexStringToByteArray(deviceAddress);
        byte[] adrr = new byte[6];
        ByteUtils.byteInvertedOrder(tmp,adrr);

        // 根据对象名获取对象格式信息，这个格式信息，记录在CSV文件中
        DLT645Data dataEntity = DLT645Analysis.inst().getTemplateByDIn(DLT645Constant.PRO_VER_2007).get(dataIdentifier);
        if (dataEntity == null) {
            log.info("CSV模板文件中未定义对象:" + dataIdentifier + " ，你需要在模板中添加该对象信息");
        }
        byte byFun = Byte.decode(String.valueOf(DLT645FunCode.getCode(funCode,DLT645Constant.PRO_VER_2007)));

        // 使用DLT645协议框架编码
        byte[] pack = DLT645Analysis.packCmd(adrr,byFun,dataEntity.getDIn());

        // 将报文按要求的16进制格式的String对象返回
        return ByteUtils.byteArrayToHexString(pack,false);
    }
    @Data
    public static class ReportData{
        private String type;
        private String identifier;
        private Long occur;
        private Long time;
        private Object data;
    }
}
