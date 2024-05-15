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

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author：tfd
 * @Date：2023/12/13 17:58
 */
@Slf4j
@Setter
@Getter
public class DLT645V1997Data extends DLT645Data {
    /**
     * DI1/DI0
     */
    private byte di0l = 0;
    private byte di0h = 0;
    private byte di1l = 0;
    private byte di1h = 0;

    @Override
    public String getKey() {
        String key = "";
        key += Integer.toString(this.di1h, 16);
        key += Integer.toString(this.di1l, 16);
        key += Integer.toString(this.di0h, 16);
        key += Integer.toString(this.di0l, 16);
        return key.toUpperCase();
    }

    @Override
    public byte[] getDIn() {
        byte[] value = new byte[2];
        value[0] = (byte) (this.di0l + (this.di0h << 4));
        value[1] = (byte) (this.di1l + (this.di1h << 4));
        return value;
    }

    @Override
    public void setDIn(byte[] value) {
        if (value.length < 2) {
            log.info("DATA LENGTH ERROR");
        }

        // DI值
        this.di1h = (byte) ((value[1] & 0xf0) >> 4);
        this.di1l = (byte) (value[1] & 0x0f);
        this.di0h = (byte) ((value[0] & 0xf0) >> 4);
        this.di0l = (byte) (value[0] & 0x0f);
    }

    /**
     * 1997版的DIn2字节
     *
     * @return
     */
    @Override
    public int getDInLen() {
        return 2;
    }
}
