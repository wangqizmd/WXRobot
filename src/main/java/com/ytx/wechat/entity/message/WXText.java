package com.ytx.wechat.entity.message;

import java.io.Serializable;

/**
 * 微信文字消息
 */
public class WXText extends WXMessage implements Serializable, Cloneable {
    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public WXText clone() {
        return (WXText) super.clone();
    }
}
