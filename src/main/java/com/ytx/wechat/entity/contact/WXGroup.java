package com.ytx.wechat.entity.contact;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;

/**
 * 微信群
 */
@Data
public class WXGroup extends WXContact implements Serializable, Cloneable {
    /**
     * 是否是详细的群信息（主要是是否获取过群成员）。
     * 如果不是则可以通过WeChatClient.fetchContact方法获取群的详细信息。
     */
    public boolean isDetail;
    /**
     * 我自己是否是群主
     */
    public boolean isOwner;
    /**
     * 群成员id到entity的映射
     */
    public HashMap<String, Member> members;

    @Data
    public static class Member implements Serializable, Cloneable {
        /**
         * 群成员id
         */
        public String id;
        /**
         * 群成员昵称
         */
        public String name;
        /**
         * 群成员群名片
         */
        public String display;

    }
}
