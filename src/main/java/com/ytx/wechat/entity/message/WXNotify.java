package com.ytx.wechat.entity.message;

import java.io.Serializable;

/**
 * 微信状态消息
 */
public class WXNotify extends WXMessage implements Serializable, Cloneable {
    public static final int NOTIFY_READED = 1;
    public static final int NOTIFY_ENTER_SESSION = 2;
    public static final int NOTIFY_INITED = 3;
    public static final int NOTIFY_SYNC_CONV = 4;
    public static final int NOTIFY_QUIT_SESSION = 5;

    /**
     * 状态码
     */
    public int notifyCode;
    /**
     * 关联联系人
     */
    public String notifyContact;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        WXNotify wxNotify = (WXNotify) o;

        if (notifyCode != wxNotify.notifyCode) {
            return false;
        }
        return notifyContact != null ? notifyContact.equals(wxNotify.notifyContact) : wxNotify.notifyContact == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + notifyCode;
        result = 31 * result + (notifyContact != null ? notifyContact.hashCode() : 0);
        return result;
    }

    @Override
    public WXNotify clone() {
        return (WXNotify) super.clone();
    }
}
