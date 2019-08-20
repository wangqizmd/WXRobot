package com.ytx.wechat.entity.message;

import lombok.Data;

import java.io.Serializable;

/**
 * 微信好友请求消息
 */
@Data
public class WXVerify extends WXMessage implements Serializable, Cloneable {
    /**
     * 请求用户id
     */
    public String userId;
    /**
     * 请求用户名称
     */
    public String userName;
    /**
     * 请求用户个性签名
     */
    public String signature;
    /**
     * 请求用户所在省份
     */
    public String province;
    /**
     * 请求用户所在城市
     */
    public String city;
    /**
     * 请求用户性别
     */
    public int gender;
    /**
     * 请求用户验证标志位
     */
    public int verifyFlag;
    /**
     * 请求用户票据
     */
    public String ticket;
}
