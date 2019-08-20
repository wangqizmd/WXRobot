package com.ytx.wechat.entity.message;

import lombok.Data;

import java.io.Serializable;

/**
 * 微信名片消息
 */
@Data
public class WXRecommend extends WXMessage implements Serializable, Cloneable {
    /**
     * 名片用户id
     */
    public String userId;
    /**
     * 名片用户名称
     */
    public String userName;
    /**
     * 名片用户性别
     */
    public int gender;
    /**
     * 名片用户个性签名
     */
    public String signature;
    /**
     * 名片用户所在省份
     */
    public String province;
    /**
     * 名片用户所在城市
     */
    public String city;
    /**
     * 名片用户验证标志位
     */
    public int verifyFlag;
}
