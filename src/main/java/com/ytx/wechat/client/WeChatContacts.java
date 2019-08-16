package com.ytx.wechat.client;

import com.ytx.wechat.entity.contact.WXGroup;
import com.ytx.wechat.entity.contact.WXUser;
import lombok.extern.slf4j.Slf4j;
import me.xuxiaoxiao.chatapi.wechat.entity.contact.WXContact;
import me.xuxiaoxiao.chatapi.wechat.entity.contact.WXGroup;
import me.xuxiaoxiao.chatapi.wechat.entity.contact.WXUser;
import me.xuxiaoxiao.chatapi.wechat.protocol.RspInit;

import java.util.HashMap;

/**
 * 模拟网页微信客户端联系人
 */
@SuppressWarnings("unchecked")
@Slf4j
final class WeChatContacts {
    private final HashMap<String, WXContact> contacts = new HashMap<>();
    private final HashMap<String, WXUser> friends = new HashMap<>();
    private final HashMap<String, WXGroup> groups = new HashMap<>();
    private WXUser me;

    private static <T extends WXContact> T parseContact(String host, RspInit.User contact) {
        if (contact.UserName.startsWith("@@")) {
            WXGroup group = new WXGroup();
            group.id = contact.UserName;
            group.name = contact.NickName;
            group.namePY = contact.PYInitial;
            group.nameQP = contact.PYQuanPin;
            group.avatarUrl = String.format("https://%s%s", host, contact.HeadImgUrl);
            group.contactFlag = contact.ContactFlag;
            group.isDetail = false;
            group.isOwner = contact.IsOwner > 0;
            group.members = new HashMap<>();
            if(contact.MemberList!= null){
                for (RspInit.User user : contact.MemberList) {
                    WXGroup.Member member = new WXGroup.Member();
                    member.id = user.UserName;
                    member.name = user.NickName;
                    member.display = user.DisplayName;
                    group.members.put(member.id, member);
                }
            }
            return (T) group;
        } else {
            WXUser user = new WXUser();
            user.id = contact.UserName;
            user.name = contact.NickName;
            user.namePY = contact.PYInitial;
            user.nameQP = contact.PYQuanPin;
            user.avatarUrl = String.format("https://%s%s", host, contact.HeadImgUrl);
            user.contactFlag = contact.ContactFlag;
            user.gender = contact.Sex;
            user.signature = contact.Signature;
            user.remark = contact.RemarkName;
            user.remarkPY = contact.RemarkPYInitial;
            user.remarkQP = contact.RemarkPYQuanPin;
            user.province = contact.Province;
            user.city = contact.City;
            user.verifyFlag = contact.VerifyFlag;
            return (T) user;
        }
    }

    /**
     * 获取自身信息
     *
     * @return 自身信息
     */
    WXUser getMe() {
        return this.me;
    }

    /**
     * 获取好友信息
     *
     * @param id 好友id
     * @return 好友信息
     */
    WXUser getFriend(String id) {
        return this.friends.get(id);
    }

    /**
     * 获取所有好友
     *
     * @return 所有好友
     */
    HashMap<String, WXUser> getFriends() {
        return this.friends;
    }

    /**
     * 获取群信息
     *
     * @param id 群id
     * @return 群信息
     */
    WXGroup getGroup(String id) {
        return this.groups.get(id);
    }

    /**
     * 获取所有群
     *
     * @return 所有群
     */
    HashMap<String, WXGroup> getGroups() {
        return this.groups;
    }

    /**
     * 获取联系人信息
     *
     * @param userId 联系人id
     * @return 联系人信息
     */
    WXContact getContact(String userId) {
        return this.contacts.get(userId);
    }

    /**
     * 设置自身信息
     *
     * @param userMe 自身信息
     */
    void setMe(String host, RspInit.User userMe) {
        this.me = WeChatContacts.parseContact(host, userMe);
        this.contacts.put(this.me.id, this.me);
    }

    void putContact(String host, RspInit.User userContact) {
        WXContact contact = WeChatContacts.parseContact(host, userContact);
        this.contacts.put(contact.id, contact);
        if (contact instanceof WXGroup) {
            WXGroup group = (WXGroup) contact;
            groups.put(group.id, group);
        } else {
            WXUser user = (WXUser) contact;
            if ((user.contactFlag & WXContact.CONTACT) > 0) {
                friends.put(user.id, user);
            }
        }
    }

    /**
     * 移除联系人
     *
     * @param userId 联系人id
     */
    WXContact rmvContact(String userId) {
        this.groups.remove(userId);
        this.friends.remove(userId);
        return this.contacts.remove(userId);
    }
}
