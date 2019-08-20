package com.ytx.wechat.entity.contact;

import lombok.Data;

import java.io.Serializable;

/**
 * 微信用户
 */
@Data
public class WXUser extends WXContact implements Serializable, Cloneable {

    public static final int GENDER_UNKNOWN = 0;
    public static final int GENDER_MALE = 1;
    public static final int GENDER_FEMALE = 2;

    public static final int VERIFY_BIZ = 1;
    public static final int VERIFY_FAMOUS = 2;
    public static final int VERIFY_BIZ_BIG = 4;
    public static final int VERIFY_BIZ_BRAND = 8;
    public static final int VERIFY_BIZ_VERIFIED = 16;

    /**
     * 用户性别
     */
    public int gender;
    /**
     * 用户的个性签名
     */
    public String signature;
    /**
     * 自己给用户设置的备注名
     */
    public String remark;
    /**
     * 备注名的拼音首字母
     */
    public String remarkPY;
    /**
     * 备注名的拼音全拼
     */
    public String remarkQP;
    /**
     * 用户所在省份
     */
    public String province;
    /**
     * 用户所在城市
     */
    public String city;
    /**
     * 认证标志字段
     */
    public int verifyFlag;

}
