package com.ytx.wechat.entity.message;


import java.io.Serializable;

import com.ytx.wechat.entity.contact.WXGroup;
import com.ytx.wechat.entity.contact.WXContact;
import com.ytx.wechat.entity.contact.WXUser;
/**
 * 微信消息
 */
public abstract class WXMessage implements Serializable, Cloneable {
    /**
     * 消息的id
     */
    public long id;
    /**
     * 消息的本地id
     */
    public long idLocal;
    /**
     * 消息的时间戳
     */
    public long timestamp;
    /**
     * 消息来源的群，如果不是群消息，值为null
     */
    public WXGroup fromGroup;
    /**
     * 消息来源的用户，如果不来自特定用户，值为null
     */
    public WXUser fromUser;
    /**
     * 消息发送的联系人
     */
    public WXContact toContact;
    /**
     * 消息的内容
     */
    public String content;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        WXMessage wxMessage = (WXMessage) o;

        if (id != wxMessage.id) {
            return false;
        }
        if (idLocal != wxMessage.idLocal) {
            return false;
        }
        if (timestamp != wxMessage.timestamp) {
            return false;
        }
        if (fromGroup != null ? !fromGroup.equals(wxMessage.fromGroup) : wxMessage.fromGroup != null) {
            return false;
        }
        if (fromUser != null ? !fromUser.equals(wxMessage.fromUser) : wxMessage.fromUser != null) {
            return false;
        }
        if (toContact != null ? !toContact.equals(wxMessage.toContact) : wxMessage.toContact != null) {
            return false;
        }
        return content != null ? content.equals(wxMessage.content) : wxMessage.content == null;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (int) (idLocal ^ (idLocal >>> 32));
        result = 31 * result + (int) (timestamp ^ (timestamp >>> 32));
        result = 31 * result + (fromGroup != null ? fromGroup.hashCode() : 0);
        result = 31 * result + (fromUser != null ? fromUser.hashCode() : 0);
        result = 31 * result + (toContact != null ? toContact.hashCode() : 0);
        result = 31 * result + (content != null ? content.hashCode() : 0);
        return result;
    }

    @Override
    public WXMessage clone() {
        try {
            WXMessage wxMessage = (WXMessage) super.clone();
            if (wxMessage.fromGroup != null) {
                wxMessage.fromGroup = this.fromGroup.clone();
            }
            if (wxMessage.fromUser != null) {
                wxMessage.fromUser = this.fromUser.clone();
            }
            if (wxMessage.toContact != null) {
                wxMessage.toContact = this.toContact.clone();
            }
            return wxMessage;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            throw new IllegalStateException();
        }
    }
}
