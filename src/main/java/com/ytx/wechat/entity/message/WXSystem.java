package com.ytx.wechat.entity.message;

import java.io.Serializable;

/**
 * 微信系统消息，新人入群，被踢出群，红包消息等
 */
public class WXSystem extends WXMessage implements Serializable, Cloneable {
    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public WXSystem clone() {
        return (WXSystem) super.clone();
    }
}
