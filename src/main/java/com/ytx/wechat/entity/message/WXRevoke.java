package com.ytx.wechat.entity.message;

import java.io.Serializable;

/**
 * 微信撤回消息
 */
public class WXRevoke extends WXMessage implements Serializable, Cloneable {
    /**
     * 撤回消息id
     */
    public long msgId;
    /**
     * 撤回消息提示文字
     */
    public String msgReplace;

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

        WXRevoke wxRevoke = (WXRevoke) o;

        if (msgId != wxRevoke.msgId) {
            return false;
        }
        return msgReplace != null ? msgReplace.equals(wxRevoke.msgReplace) : wxRevoke.msgReplace == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (int) (msgId ^ (msgId >>> 32));
        result = 31 * result + (msgReplace != null ? msgReplace.hashCode() : 0);
        return result;
    }

    @Override
    public WXRevoke clone() {
        return (WXRevoke) super.clone();
    }
}
