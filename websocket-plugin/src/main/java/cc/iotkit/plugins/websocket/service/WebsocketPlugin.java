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

package cc.iotkit.plugins.websocket.service;

import cc.iotkit.plugin.core.IPluginConfig;
import cc.iotkit.plugins.websocket.conf.WebsocketConfig;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.gitee.starblues.bootstrap.annotation.AutowiredType;
import com.gitee.starblues.bootstrap.realize.PluginCloseListener;
import com.gitee.starblues.core.PluginCloseType;
import com.gitee.starblues.core.PluginInfo;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author tfd
 */
@Slf4j
@Service
public class WebsocketPlugin implements PluginCloseListener {

    @Autowired
    private PluginInfo pluginInfo;
    @Autowired
    private WebsocketVerticle websocketVerticle;
    @Autowired
    private WebsocketConfig websocketConfig;

    @Autowired
    @AutowiredType(AutowiredType.Type.MAIN_PLUGIN)
    private IPluginConfig pluginConfig;

    private Vertx vertx;
    private String deployedId;

    @PostConstruct
    public void init() {
        vertx = Vertx.vertx();
        try {
            //获取插件最新配置替换当前配置
            Map<String, Object> config = pluginConfig.getConfig(pluginInfo.getPluginId());
            BeanUtil.copyProperties(config, websocketConfig, CopyOptions.create().ignoreNullValue());
            websocketVerticle.setConfig(websocketConfig);

            Future<String> future = vertx.deployVerticle(websocketVerticle);
            future.onSuccess((s -> {
                deployedId = s;
                log.info("websocket plugin started success");
            }));
            future.onFailure((e) -> {
                log.error("websocket plugin startup failed", e);
            });
        } catch (Throwable e) {
            log.error("websocket plugin startup error", e);
        }
    }

    @Override
    public void close(GenericApplicationContext applicationContext, PluginInfo pluginInfo, PluginCloseType closeType) {
        try {
            log.info("plugin close,type:{},pluginId:{}", closeType, pluginInfo.getPluginId());
            if (deployedId != null) {
                CountDownLatch wait = new CountDownLatch(1);
                Future<Void> future = vertx.undeploy(deployedId);
                future.onSuccess(unused -> {
                    log.info("websocket plugin stopped success");
                    wait.countDown();
                });
                future.onFailure(h -> {
                    log.info("websocket plugin stopped failed");
                    h.printStackTrace();
                    wait.countDown();
                });
                wait.await(5, TimeUnit.SECONDS);
            }
        } catch (Throwable e) {
            log.error("websocket plugin stop error", e);
        }
    }

}
