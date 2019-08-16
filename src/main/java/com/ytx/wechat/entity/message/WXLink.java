package com.ytx.wechat.entity.message;

import java.io.Serializable;

/**
 * 微信链接消息
 */
public class WXLink extends WXMessage implements Serializable, Cloneable {
    /**
     * 链接标题
     */
    public String linkName;
    /**
     * 链接地址
     */
    public String linkUrl;

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

        WXLink wxLink = (WXLink) o;

        if (linkName != null ? !linkName.equals(wxLink.linkName) : wxLink.linkName != null) {
            return false;
        }
        return linkUrl != null ? linkUrl.equals(wxLink.linkUrl) : wxLink.linkUrl == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (linkName != null ? linkName.hashCode() : 0);
        result = 31 * result + (linkUrl != null ? linkUrl.hashCode() : 0);
        return result;
    }

    @Override
    public WXLink clone() {
        return (WXLink) super.clone();
    }
}
