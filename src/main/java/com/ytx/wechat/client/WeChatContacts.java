package com.ytx.wechat.client;

import com.ytx.wechat.config.GlobalConfig;
import com.ytx.wechat.entity.contact.WXContact;
import com.ytx.wechat.entity.contact.WXGroup;
import com.ytx.wechat.entity.contact.WXUser;
import com.ytx.wechat.protocol.RspInit;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

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

    private static final String WHITELIST = GlobalConfig.getValue("friend.whitelist", "");

    private static final String BLACKLIST = GlobalConfig.getValue("friend.blacklist", "");

    private static final String GROUP_WHITELIST = GlobalConfig.getValue("group.whitelist", "");

    private static final String GROUP_WHITE_KEYWORD = GlobalConfig.getValue("group.whiteKeyword", "");

    private static final String GROUP_BLACKLIST = GlobalConfig.getValue("group.blacklist", "");

    private static final String GROUP_BLACK_KEYWORD = GlobalConfig.getValue("group.blackKeyword", "");

    private static final String GROUP_MODE_ONLY = GlobalConfig.getValue("group.modeOnly", "");

    private static final String GROUP_MODE_KEYWORD = GlobalConfig.getValue("group.modeOnlyKeyword", "");

    private static List<String> WHITE_LIST = new LinkedList<>();

    private static List<String> BLACK_LIST = new LinkedList<>();

    private static List<String> GROUP_WHITE_LIST = new LinkedList<>();

    private static List<String> GROUP_WHITE_KEYWORD_LIST = new LinkedList<>();

    private static List<String> GROUP_MODE_KEYWORD_LIST = new LinkedList<>();

    private static List<String> GROUP_MODE_ONLY_LIST = new LinkedList<>();

    private static List<String> GROUP_BLACK_KEYWORD_LIST = new LinkedList<>();

    private static List<String> GROUP_BLACK_LIST = new LinkedList<>();

    static {

        WHITE_LIST.addAll(Arrays.stream(WHITELIST.split("#")).filter(StringUtils::isNotBlank).collect(Collectors.toList()));

        BLACK_LIST.addAll(Arrays.stream(BLACKLIST.split("#")).filter(StringUtils::isNotBlank).collect(Collectors.toList()));

        GROUP_WHITE_LIST.addAll(Arrays.stream(GROUP_WHITELIST.split("#")).filter(StringUtils::isNotBlank).collect(Collectors.toList()));

        GROUP_WHITE_KEYWORD_LIST.addAll(Arrays.stream(GROUP_WHITE_KEYWORD.split("#")).filter(StringUtils::isNotBlank).collect(Collectors.toList()));

        GROUP_BLACK_KEYWORD_LIST.addAll(Arrays.stream(GROUP_BLACK_KEYWORD.split("#")).filter(StringUtils::isNotBlank).collect(Collectors.toList()));

        GROUP_BLACK_LIST.addAll(Arrays.stream(GROUP_BLACKLIST.split("#")).filter(StringUtils::isNotBlank).collect(Collectors.toList()));

        GROUP_MODE_ONLY_LIST.addAll(Arrays.stream(GROUP_MODE_ONLY.split("#")).filter(StringUtils::isNotBlank).collect(Collectors.toList()));

        GROUP_MODE_KEYWORD_LIST.addAll(Arrays.stream(GROUP_MODE_KEYWORD.split("#")).filter(StringUtils::isNotBlank).collect(Collectors.toList()));
    }


    private static void setGroupPermission(WXGroup group) {
        if(StringUtils.isEmpty(group.name)){
            return;
        }
        for(String mode:GROUP_MODE_KEYWORD_LIST){
            if(group.name.contains(mode)){
                group.permission = 1;
                break;
            }
        }
        if(group.permission != 1 ){
            for(String mode:GROUP_MODE_ONLY_LIST){
                if(group.name.equals(mode)){
                    group.permission = 1;
                    break;
                }
            }
        }
        for(String mode:GROUP_WHITE_KEYWORD_LIST){
            if(group.name.contains(mode)){
                group.permission = 2;
                break;
            }
        }
        if(group.permission != 2 ){
            for(String mode:GROUP_WHITE_LIST){
                if(group.name.equals(mode)){
                    group.permission = 2;
                    break;
                }
            }
        }
        for(String mode:GROUP_BLACK_KEYWORD_LIST){
            if(group.name.contains(mode)){
                group.permission = 3;
                break;
            }
        }
        if(group.permission != 3 ){
            for(String mode:GROUP_BLACK_LIST){
                if(group.name.equals(mode)){
                    group.permission = 3;
                    break;
                }
            }
        }
    }

    private static void setUserPermission(WXUser user) {
        if(StringUtils.isEmpty(user.name)&&StringUtils.isEmpty(user.remark)){
            return;
        }
        String name = StringUtils.isEmpty(user.remark) ? user.name : user.remark;
        for(String mode:WHITE_LIST){
            if(name.equals(mode)){
                user.permission = 2;
                break;
            }
        }
        for(String mode:BLACK_LIST){
            if(name.equals(mode)){
                user.permission = 3;
                break;
            }
        }

    }

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
            WeChatContacts.setGroupPermission(group);
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
            WeChatContacts.setUserPermission(user);
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
        //更新权限，如果是在微信里面设置的权限，在这顺延下去
        WXContact oldContact = this.contacts.get(contact.id);
        if(oldContact!=null ){
            if(oldContact.getPermission() != 0){
                contact.setPermission(oldContact.getPermission());
            }
            rmvContact(contact.id);
        }
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
