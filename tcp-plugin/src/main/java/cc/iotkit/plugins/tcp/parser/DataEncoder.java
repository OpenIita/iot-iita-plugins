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

package cc.iotkit.plugins.tcp.parser;

import io.vertx.core.buffer.Buffer;
import lombok.extern.slf4j.Slf4j;

/**
 * 数据编码
 *
 * @author sjg
 */
@Slf4j
public class DataEncoder {

    public static Buffer encode(DataPackage data) {
        Buffer buffer = Buffer.buffer();
        //设备地址(6byte) + 功能码(2byte) +	消息序号(2byte) + 包体(不定长度)
        buffer.appendInt(6+2+2+data.getPayload().length);
        buffer.appendBytes(data.getAddr().getBytes());
        buffer.appendShort(data.getCode());
        buffer.appendShort(data.getMid());
        buffer.appendBytes(data.getPayload());
        return buffer;
    }
}
