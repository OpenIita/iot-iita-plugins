package cc.iotkit.plugins.mqtt.service;

import cc.iotkit.common.utils.JsonUtils;
import cc.iotkit.plugin.core.IPlugin;
import cc.iotkit.plugin.core.IPluginConfig;
import cc.iotkit.plugins.mqtt.conf.MqttConfig;
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

/**
 * @author sjg
 */
@Slf4j
@Service
public class MqttPlugin implements PluginCloseListener, IPlugin {

    @Autowired
    private PluginInfo pluginInfo;
    @Autowired
    private MqttVerticle mqttVerticle;
    @Autowired
    private MqttConfig mqttConfig;

    @Autowired
    @AutowiredType(AutowiredType.Type.MAIN_PLUGIN)
    private IPluginConfig pluginConfig;

    private Vertx vertx;
    private CountDownLatch countDownLatch;
    private String deployedId;

    @PostConstruct
    public void init() {
        vertx = Vertx.vertx();
        try {
            //获取插件最新配置替换当前配置
            Map<String, Object> config = pluginConfig.getConfig(pluginInfo.getPluginId());
            BeanUtil.copyProperties(config,mqttConfig, CopyOptions.create().ignoreNullValue());
            mqttVerticle.setConfig(mqttConfig);

            countDownLatch = new CountDownLatch(1);
            Future<String> future = vertx.deployVerticle(mqttVerticle);
            future.onSuccess((s -> {
                deployedId = s;
                countDownLatch.countDown();
            }));
            future.onFailure((e) -> {
                countDownLatch.countDown();
                log.error("start mqtt plugin failed", e);
            });
            countDownLatch.await();
            future.succeeded();
        } catch (Throwable e) {
            log.error("start mqtt plugin error.", e);
        }
    }

    @Override
    public void close(GenericApplicationContext applicationContext, PluginInfo pluginInfo, PluginCloseType closeType) {
        try {
            mqttVerticle.stop();
            Future<Void> future = vertx.undeploy(deployedId);
            future.onSuccess(unused -> log.info("stop mqtt plugin success"));
            if (closeType == PluginCloseType.UNINSTALL) {
                log.info("插件被卸载了：{}", pluginInfo.getPluginId());
            } else if (closeType == PluginCloseType.STOP) {
                log.info("插件被关闭了：{}", pluginInfo.getPluginId());
            } else if (closeType == PluginCloseType.UPGRADE_UNINSTALL) {
                log.info("插件被升级卸载了：{}", pluginInfo.getPluginId());
            }
        } catch (Throwable e) {
            log.error("stop mqtt plugin error.", e);
        }
    }

    @Override
    public Map<String, Object> getLinkInfo(String pk, String dn) {
        return null;
    }
}
