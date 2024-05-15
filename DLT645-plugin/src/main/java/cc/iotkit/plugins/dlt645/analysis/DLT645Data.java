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

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * @Author：tfd
 * @Date：2023/12/13 17:55
 */
@Slf4j
@Data
public abstract class DLT645Data {
    /**
     * 名称
     */
    private String name;
    /**
     * 格式
     */
    private DLT645DataFormat format = new DLT645DataFormat();
    /**
     * 长度
     */
    private int length;
    /**
     * 单位
     */
    private String unit;
    /**
     * 可读
     */
    private boolean read;
    /**
     * 可写
     */
    private boolean write;
    /**
     * 数值
     */
    private Object value = 0.0;

    /**
     * 数值
     */
    private Object value2nd;

    public abstract String getKey();

    public abstract byte[] getDIn();

    public abstract void setDIn(byte[] value);

    public abstract int getDInLen();

    public String toString() {
        if (this.value2nd == null) {
            return this.name + ":" + this.value + this.unit;
        }

        return this.name + ":" + this.value + this.unit + " " + this.value2nd;
    }

    public void decodeValue(byte[] data, Map<String, DLT645Data> dinMap) {

        // DI值
        this.setDIn(data);

        // 获取字典信息
        DLT645Data dict = dinMap.get(this.getKey());
        if (dict == null) {
            log.info("DIn info err,please configure：" + this.getKey());
        }

        this.format = dict.format;
        this.name = dict.name;
        this.read = dict.read;
        this.write = dict.write;
        this.length = dict.length;
        this.unit = dict.unit;


        // 基本值
        this.value = this.format.decodeValue(data, this.format.getFormat(), this.getDInLen(), this.format.getLength());

        // 组合值
        if (this.format.getFormat2nd() != null && !this.format.getFormat2nd().isEmpty()) {
            this.value2nd = this.format.decodeValue(data, this.format.getFormat2nd(), this.getDInLen() + this.format.getLength(), this.format.getLength2nd());
        }
    }
}
