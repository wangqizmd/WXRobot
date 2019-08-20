package com.ytx.wechat.entity.message;


import java.io.Serializable;

import com.ytx.wechat.entity.contact.WXGroup;
import com.ytx.wechat.entity.contact.WXContact;
import com.ytx.wechat.entity.contact.WXUser;
import lombok.Data;

/**
 * 微信消息
 */
@Data
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
}
