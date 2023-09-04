package cc.iotkit.plugins.http.conf;

import cc.iotkit.plugin.core.thing.IThingService;
import cc.iotkit.plugins.http.service.FakeThingService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * @author sjg
 */
@Component
public class BeanConfig {

    @Bean
    @ConditionalOnProperty(name = "plugin.runMode", havingValue = "dev")
    IThingService getThingService() {
        return new FakeThingService();
    }

}
