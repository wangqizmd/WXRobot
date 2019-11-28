package com.ytx.wechat.entity.contact;

import lombok.Data;

import java.io.Serializable;

@Data
public class WXOfficial extends WXContact implements Serializable, Cloneable{
    /**
     * 公众号签名
     */
    public String signature;

    /**
     * 所在省份
     */
    public String province;
    /**
     * 所在城市
     */
    public String city;
}
