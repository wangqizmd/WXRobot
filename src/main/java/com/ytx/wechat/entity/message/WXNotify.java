package com.ytx.wechat.entity.message;

import lombok.Data;

import java.io.Serializable;

/**
 * 微信状态消息
 */
@Data
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
}
