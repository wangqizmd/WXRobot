package com.ytx.wechat.entity.message;

import java.io.Serializable;

/**
 * 微信名片消息
 */
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

        WXRecommend that = (WXRecommend) o;

        if (gender != that.gender) {
            return false;
        }
        if (verifyFlag != that.verifyFlag) {
            return false;
        }
        if (userId != null ? !userId.equals(that.userId) : that.userId != null) {
            return false;
        }
        if (userName != null ? !userName.equals(that.userName) : that.userName != null) {
            return false;
        }
        if (signature != null ? !signature.equals(that.signature) : that.signature != null) {
            return false;
        }
        if (province != null ? !province.equals(that.province) : that.province != null) {
            return false;
        }
        return city != null ? city.equals(that.city) : that.city == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        result = 31 * result + (userName != null ? userName.hashCode() : 0);
        result = 31 * result + gender;
        result = 31 * result + (signature != null ? signature.hashCode() : 0);
        result = 31 * result + (province != null ? province.hashCode() : 0);
        result = 31 * result + (city != null ? city.hashCode() : 0);
        result = 31 * result + verifyFlag;
        return result;
    }

    @Override
    public WXRecommend clone() {
        return (WXRecommend) super.clone();
    }
}
