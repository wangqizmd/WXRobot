package com.ytx.wechat.entity.message;

import java.io.Serializable;

/**
 * 微信好友请求消息
 */
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

        WXVerify wxVerify = (WXVerify) o;

        if (gender != wxVerify.gender) {
            return false;
        }
        if (verifyFlag != wxVerify.verifyFlag) {
            return false;
        }
        if (userId != null ? !userId.equals(wxVerify.userId) : wxVerify.userId != null) {
            return false;
        }
        if (userName != null ? !userName.equals(wxVerify.userName) : wxVerify.userName != null) {
            return false;
        }
        if (signature != null ? !signature.equals(wxVerify.signature) : wxVerify.signature != null) {
            return false;
        }
        if (province != null ? !province.equals(wxVerify.province) : wxVerify.province != null) {
            return false;
        }
        if (city != null ? !city.equals(wxVerify.city) : wxVerify.city != null) {
            return false;
        }
        return ticket != null ? ticket.equals(wxVerify.ticket) : wxVerify.ticket == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        result = 31 * result + (userName != null ? userName.hashCode() : 0);
        result = 31 * result + (signature != null ? signature.hashCode() : 0);
        result = 31 * result + (province != null ? province.hashCode() : 0);
        result = 31 * result + (city != null ? city.hashCode() : 0);
        result = 31 * result + gender;
        result = 31 * result + verifyFlag;
        result = 31 * result + (ticket != null ? ticket.hashCode() : 0);
        return result;
    }

    @Override
    public WXVerify clone() {
        return (WXVerify) super.clone();
    }
}
