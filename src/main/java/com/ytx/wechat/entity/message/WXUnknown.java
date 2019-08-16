package com.ytx.wechat.entity.message;

import java.io.Serializable;

/**
 * 微信未知类型消息
 */
public class WXUnknown extends WXMessage implements Serializable, Cloneable {
    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public WXUnknown clone() {
        return (WXUnknown) super.clone();
    }
}
