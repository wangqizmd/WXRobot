package com.ytx.wechat.entity.message;

import lombok.Data;

import java.io.Serializable;

/**
 * 微信撤回消息
 */
@Data
public class WXRevoke extends WXMessage implements Serializable, Cloneable {
    /**
     * 撤回消息id
     */
    public long msgId;
    /**
     * 撤回消息提示文字
     */
    public String msgReplace;
}
