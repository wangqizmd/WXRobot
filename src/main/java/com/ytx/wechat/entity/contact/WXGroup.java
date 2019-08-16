package com.ytx.wechat.entity.contact;

import java.io.Serializable;
import java.util.HashMap;

/**
 * 微信群
 */
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

        WXGroup wxGroup = (WXGroup) o;
        if (isDetail != wxGroup.isDetail) {
            return false;
        }
        if (isOwner != wxGroup.isOwner) {
            return false;
        }
        return members != null ? members.equals(wxGroup.members) : wxGroup.members == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (isDetail ? 1 : 0);
        result = 31 * result + (isOwner ? 1 : 0);
        result = 31 * result + (members != null ? members.hashCode() : 0);
        return result;
    }

    @Override
    public WXGroup clone() {
        WXGroup wxGroup = (WXGroup) super.clone();
        if (this.members != null) {
            wxGroup.members = (HashMap<String, Member>) this.members.clone();
        }
        return wxGroup;
    }

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

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            Member member = (Member) o;

            if (id != null ? !id.equals(member.id) : member.id != null) {
                return false;
            }
            if (name != null ? !name.equals(member.name) : member.name != null) {
                return false;
            }
            return display != null ? display.equals(member.display) : member.display == null;
        }

        @Override
        public int hashCode() {
            int result = id != null ? id.hashCode() : 0;
            result = 31 * result + (name != null ? name.hashCode() : 0);
            result = 31 * result + (display != null ? display.hashCode() : 0);
            return result;
        }

        @Override
        public Member clone() {
            try {
                return (Member) super.clone();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
                throw new IllegalStateException();
            }
        }
    }
}
