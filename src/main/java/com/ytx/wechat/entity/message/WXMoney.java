package com.ytx.wechat.entity.message;

import java.io.Serializable;

/**
 * 微信转账消息
 */
public class WXMoney extends WXMessage implements Serializable, Cloneable {

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public WXMoney clone() {
        return (WXMoney) super.clone();
    }
}
