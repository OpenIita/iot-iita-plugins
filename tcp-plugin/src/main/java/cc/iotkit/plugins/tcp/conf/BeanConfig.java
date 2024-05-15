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

import cc.iotkit.plugin.core.IPluginConfig;
import cc.iotkit.plugin.core.IPluginScript;
import cc.iotkit.plugin.core.LocalPluginConfig;
import cc.iotkit.plugin.core.LocalPluginScript;
import cc.iotkit.plugin.core.thing.IThingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * @author sjg
 */
@Slf4j
@Component
public class BeanConfig {

    @Bean
    @ConditionalOnProperty(name = "plugin.runMode", havingValue = "dev")
    IThingService getThingService() {
        return new FakeThingService();
    }

    @Bean
    @ConditionalOnProperty(name = "plugin.runMode", havingValue = "dev")
    IPluginScript getPluginScript() {
        log.info("init LocalPluginScript");
        return new LocalPluginScript("script.js");
    }

    @Bean
    @ConditionalOnProperty(name = "plugin.runMode", havingValue = "dev")
    IPluginConfig getPluginConfig() {
        return new LocalPluginConfig();
    }
}
