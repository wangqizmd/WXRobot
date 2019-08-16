package com.ytx.wechat.entity.contact;

import java.io.Serializable;

/**
 * 微信用户
 */
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

        WXUser wxUser = (WXUser) o;

        if (gender != wxUser.gender) {
            return false;
        }
        if (signature != null ? !signature.equals(wxUser.signature) : wxUser.signature != null) {
            return false;
        }
        if (remark != null ? !remark.equals(wxUser.remark) : wxUser.remark != null) {
            return false;
        }
        if (remarkPY != null ? !remarkPY.equals(wxUser.remarkPY) : wxUser.remarkPY != null) {
            return false;
        }
        if (remarkQP != null ? !remarkQP.equals(wxUser.remarkQP) : wxUser.remarkQP != null) {
            return false;
        }
        if (province != null ? !province.equals(wxUser.province) : wxUser.province != null) {
            return false;
        }
        if (city != null ? !city.equals(wxUser.city) : wxUser.city != null) {
            return false;
        }
        return verifyFlag != wxUser.verifyFlag;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + gender;
        result = 31 * result + (signature != null ? signature.hashCode() : 0);
        result = 31 * result + (remark != null ? remark.hashCode() : 0);
        result = 31 * result + (remarkPY != null ? remarkPY.hashCode() : 0);
        result = 31 * result + (remarkQP != null ? remarkQP.hashCode() : 0);
        result = 31 * result + (province != null ? province.hashCode() : 0);
        result = 31 * result + (city != null ? city.hashCode() : 0);
        result = 31 * result + verifyFlag;
        return result;
    }

    @Override
    public WXUser clone() {
        return (WXUser) super.clone();
    }
}
