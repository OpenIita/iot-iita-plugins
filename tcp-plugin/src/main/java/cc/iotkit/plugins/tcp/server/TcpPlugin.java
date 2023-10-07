package cc.iotkit.plugins.tcp.server;

import cc.iotkit.plugin.core.IPlugin;
import cc.iotkit.plugin.core.IPluginConfig;
import cc.iotkit.plugins.tcp.conf.TcpServerConfig;
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

/**
 * tcp插件
 *
 * @author sjg
 */
@Slf4j
@Service
public class TcpPlugin implements PluginCloseListener, IPlugin {

    @Autowired
    private PluginInfo pluginInfo;
    @Autowired
    private TcpServerVerticle tcpServerVerticle;

    @Autowired
    private TcpServerConfig tcpConfig;

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
            BeanUtil.copyProperties(config, tcpConfig, CopyOptions.create().ignoreNullValue());
            tcpServerVerticle.setConfig(tcpConfig);

            Future<String> future = vertx.deployVerticle(tcpServerVerticle);
            future.onSuccess((s -> {
                deployedId = s;
                log.info("tcp plugin started success");
            }));
            future.onFailure((e) -> {
                log.error("tcp plugin startup failed", e);
            });
        } catch (Throwable e) {
            log.error("tcp plugin startup error", e);
        }
    }

    @Override
    public void close(GenericApplicationContext applicationContext, PluginInfo pluginInfo, PluginCloseType closeType) {
        try {
            tcpServerVerticle.stop();
            Future<Void> future = vertx.undeploy(deployedId);
            future.onSuccess(unused -> log.info("tcp plugin stopped success"));
            if (closeType == PluginCloseType.UNINSTALL) {
                log.info("tcp plugin UNINSTALL：{}", pluginInfo.getPluginId());
            } else if (closeType == PluginCloseType.STOP) {
                log.info("tcp plugin STOP：{}", pluginInfo.getPluginId());
            } else if (closeType == PluginCloseType.UPGRADE_UNINSTALL) {
                log.info("tcp plugin UPGRADE_UNINSTALL：{}", pluginInfo.getPluginId());
            }
        } catch (Throwable e) {
            log.error("tcp plugin stop error", e);
        }
    }

    @Override
    public Map<String, Object> getLinkInfo(String pk, String dn) {
        return null;
    }
}