package cc.iotkit.plugins.mqtt.service;

import cc.iotkit.model.device.DeviceInfo;
import cc.iotkit.model.product.Product;
import cc.iotkit.plugin.core.thing.IThingService;
import cc.iotkit.plugin.core.thing.actions.ActionResult;
import cc.iotkit.plugin.core.thing.actions.IDeviceAction;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 测试服务
 *
 * @author sjg
 */
@Slf4j
public class FakeThingService implements IThingService {

    /**
     * 添加测试产品
     */
    private static final Map<String, String> PRODUCTS = Map.of(
            "hbtgIA0SuVw9lxjB", "xdkKUymrEGSCYWswqCvSPyRSFvH5j7CU",
            "Rf4QSjbm65X45753", "xdkKUymrEGSCYWswqCvSPyRSFvH5j7CU",
            "cGCrkK7Ex4FESAwe", "xdkKUymrEGSCYWswqCvSPyRSFvH5j7CU"
    );

    /**
     * 添加测试设备
     */
    private static final Map<String, String> DEVICES = new HashMap<>();

    static {
        for (int i = 0; i < 10; i++) {
            DEVICES.put("TEST:GW:" + StringUtils.leftPad(i + "", 6, "0"), "hbtgIA0SuVw9lxjB");
            DEVICES.put("TEST_SW_" + StringUtils.leftPad(i + "", 6, "0"), "Rf4QSjbm65X45753");
            DEVICES.put("TEST_SC_" + StringUtils.leftPad(i + "", 6, "0"), "cGCrkK7Ex4FESAwe");
        }
    }

    @Override
    public ActionResult post(String pluginId, IDeviceAction action) {
        log.info("post action:{}", action);
        return ActionResult.builder().code(0).build();
    }

    @Override
    public Product getProduct(String pk) {
        return Product.builder()
                .productKey(pk)
                .productSecret(PRODUCTS.get(pk))
                .build();
    }

    @Override
    public DeviceInfo getDevice(String dn) {
        return DeviceInfo.builder()
                .productKey(DEVICES.get(dn))
                .deviceName(dn)
                .build();
    }

    @Override
    public Map<String, ?> getProperty(String dn) {
        return new HashMap<>(0);
    }
}
