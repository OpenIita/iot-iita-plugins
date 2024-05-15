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

package cc.iotkit.plugins.hydrovalve.analysis;

/**
 * @Author：tfd
 * @Date：2024/1/9 15:45
 */
public class ModBusConstants {
    public static final String MODE = "modbusMode";
    public static final String MODE_ASCII = "ASCII";
    public static final String MODE_RTU = "RTU";
    public static final String MODE_TCP = "TCP";

    /**
     * ModBus的报文结构
     */
    public static final String SN = "sn";
    public static final String ADDR = "devAddr";
    public static final String FUNC = "func";
    public static final String DATA = "data";

    /**
     * 寄存器地址和数量
     */
    public static final String REG_ADDR = "regAddr";
    public static final String REG_CNT = "regCnt";

    public static final String REG_HOLD_STATUS = "regHoldStatus";

    //读多个保持寄存器
    public static final byte FUN_CODE3 = 0x03;

    //写单个保持寄存器
    public static final byte FUN_CODE6 = 0x06;
}
