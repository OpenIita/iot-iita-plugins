package cc.iotkit.plugins.hydrovalve.service;

import cc.iotkit.model.device.DeviceInfo;
import cc.iotkit.model.product.Product;
import cc.iotkit.plugin.core.thing.IThingService;
import cc.iotkit.plugin.core.thing.actions.ActionResult;
import cc.iotkit.plugin.core.thing.actions.IDeviceAction;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author：tfd
 * @Date：2024/1/8 14:58
 */
@Slf4j
public class FakeThingService implements IThingService {

    @Override
    public ActionResult post(String pluginId, IDeviceAction action) {
        log.info("post action:{}", action);
        return ActionResult.builder().code(0).build();
    }

    @Override
    public Product getProduct(String pk) {
        return Product.builder()
                .productKey("cGCrkK7Ex4FESAwe")
                .productSecret("aaaaaaaa")
                .build();
    }

    @Override
    public DeviceInfo getDevice(String dn) {
        return DeviceInfo.builder()
                .productKey("cGCrkK7Ex4FESAwe")
                .deviceName(dn)
                .build();
    }

    @Override
    public Map<String, ?> getProperty(String dn) {
        return new HashMap<>(0);
    }
}
