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

import lombok.Getter;
import lombok.Setter;

/**
 * @Author：tfd
 * @Date：2024/1/9 15:44
 */
@Getter
@Setter
public class ModBusEntity {
    /**
     * 流水号
     */
    private int sn = 0;

    /**
     * 地址
     */
    private byte devAddr = 0x01;

    /**
     * 功能码
     */
    private byte func = 0x01;

    /**
     * 数据域
     */
    private byte[] data = new byte[0];

    /**
     * 出错信息
     */
    private int errCode = 0;

    /**
     * 出错信息
     */
    private String errMsg = "";
}
