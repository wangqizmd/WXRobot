package com.ytx.wechat.entity.contact;

import java.io.File;
import java.io.Serializable;

/**
 * 微信联系人
 */
public abstract class WXContact implements Serializable, Cloneable {
    public static final int CONTACT = 1;
    public static final int CONTACT_CHAT = 2;
    public static final int CONTACT_CHATROOM = 4;
    public static final int CONTACT_BLACKLIST = 8;
    public static final int CONTACT_DOMAIN = 16;
    public static final int CONTACT_HIDE = 32;
    public static final int CONTACT_FAVOUR = 64;
    public static final int CONTACT_3RDAPP = 128;
    public static final int CONTACT_SNSBLACKLIST = 256;
    public static final int CONTACT_NOTIFYCLOSE = 512;
    public static final int CONTACT_TOP = 2048;

    /**
     * 账户id，以@@开头的是群组，以@开头的是普通用户，其他的是特殊用户比如文件助手等
     */
    public String id;
    /**
     * 账户的名称
     */
    public String name;
    /**
     * 账户名称的拼音的首字母
     */
    public String namePY;
    /**
     * 账户名称的拼音全拼
     */
    public String nameQP;
    /**
     * 账户头像地址
     */
    public String avatarUrl;
    /**
     * 账户头像文件
     */
    public File avatarFile;
    /**
     * 联系人标志字段
     */
    public int contactFlag;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        WXContact wxContact = (WXContact) o;

        if (id != null ? !id.equals(wxContact.id) : wxContact.id != null) {
            return false;
        }
        if (name != null ? !name.equals(wxContact.name) : wxContact.name != null) {
            return false;
        }
        if (namePY != null ? !namePY.equals(wxContact.namePY) : wxContact.namePY != null) {
            return false;
        }
        if (nameQP != null ? !nameQP.equals(wxContact.nameQP) : wxContact.nameQP != null) {
            return false;
        }
        if (avatarUrl != null ? !avatarUrl.equals(wxContact.avatarUrl) : wxContact.avatarUrl != null) {
            return false;
        }
        if (avatarFile != null ? !avatarFile.equals(wxContact.avatarFile) : wxContact.avatarFile != null) {
            return false;
        }
        return contactFlag == wxContact.contactFlag;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (namePY != null ? namePY.hashCode() : 0);
        result = 31 * result + (nameQP != null ? nameQP.hashCode() : 0);
        result = 31 * result + (avatarUrl != null ? avatarUrl.hashCode() : 0);
        result = 31 * result + (avatarFile != null ? avatarFile.hashCode() : 0);
        result = 31 * result + contactFlag;
        return result;
    }

    @Override
    public WXContact clone() {
        try {
            return (WXContact) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            throw new IllegalStateException();
        }
    }
}
