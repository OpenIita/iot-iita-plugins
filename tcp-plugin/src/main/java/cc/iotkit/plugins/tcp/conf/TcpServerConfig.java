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

package cc.iotkit.plugins.tcp.conf;

import io.vertx.core.net.SocketAddress;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author huangwenl
 * @date 2022-10-13
 */
@Data
@Component
@ConfigurationProperties(prefix = "tcp")
public class TcpServerConfig {

    private String host;

    private int port;

    /**
     * 服务实例数量(线程数)
     */
    private int instance = Runtime.getRuntime().availableProcessors();

    public SocketAddress createSocketAddress() {
        if (StringUtils.isEmpty(host)) {
            host = "localhost";
        }
        return SocketAddress.inetSocketAddress(port, host);
    }

}
