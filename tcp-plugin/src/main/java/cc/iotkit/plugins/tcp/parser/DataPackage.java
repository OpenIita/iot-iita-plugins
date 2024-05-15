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

import cn.hutool.core.util.HexUtil;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.IOException;

/**
 * 数据包
 *
 * @author sjg
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DataPackage {

    public static final short CODE_REGISTER = 0x10;
    public static final short CODE_REGISTER_REPLY = 0x11;
    public static final short CODE_HEARTBEAT = 0x20;
    public static final short CODE_DATA_UP = 0x30;
    public static final short CODE_DATA_DOWN = 0x40;

    /**
     * 设备地址
     */
    private String addr;

    /**
     * 功能码
     */
    private short code;

    /**
     * 消息序号
     */
    private short mid;

    /**
     * 包体数据
     */
    @JsonSerialize(using = BufferSerializer.class)
    private byte[] payload;


    public static class BufferSerializer extends JsonSerializer<byte[]> {

        @Override
        public void serialize(byte[] value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
            jgen.writeString(HexUtil.encodeHexStr(value));
        }
    }
}
