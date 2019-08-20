package com.ytx.wechat.entity.contact;

import lombok.Data;

import java.io.File;
import java.io.Serializable;

/**
 * 微信联系人
 */
@Data
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

    /**
     * 权限,0-默认权限，1-指令模式，2-白名单，3-黑名单
     */
    public int permission;


}
