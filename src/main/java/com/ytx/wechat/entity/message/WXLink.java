package com.ytx.wechat.entity.message;

import lombok.Data;

import java.io.Serializable;

/**
 * 微信链接消息
 */
@Data
public class WXLink extends WXMessage implements Serializable, Cloneable {
    /**
     * 链接标题
     */
    public String linkName;
    /**
     * 链接地址
     */
    public String linkUrl;
}
