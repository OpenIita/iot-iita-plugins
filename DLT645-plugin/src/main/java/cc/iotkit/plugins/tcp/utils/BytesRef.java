package cc.iotkit.plugins.tcp.utils;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * @Author：tfd
 * @Date：2023/12/13 17:49
 */
@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class BytesRef {
    private byte[] value = new byte[] {};
}
