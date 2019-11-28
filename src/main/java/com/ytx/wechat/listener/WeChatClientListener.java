package com.ytx.wechat.listener;

import com.ytx.wechat.client.WeChatClient;
import com.ytx.wechat.entity.contact.WXContact;
import com.ytx.wechat.entity.contact.WXGroup;
import com.ytx.wechat.entity.contact.WXUser;
import com.ytx.wechat.entity.message.*;
import com.ytx.wechat.messageStrategy.*;
import com.ytx.wechat.utils.HTMLSpirit;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;


@Slf4j
public class WeChatClientListener extends WeChatClient.WeChatListener {

    /**
     * 微信群里@人后面的类似空格的字符。不是空格。如“@nickname ”  \u2005 ?？
     */
    public static final String AT_ME_SPACE = " ";

    Map<String,MessageStrategy> strategyMap ;

    public WeChatClientListener() {
        //注册消息处理策略
        this.strategyMap = new HashMap<>();
        strategyMap.put(WXText.class.getName(), new WXTextStrategy());
        strategyMap.put(WXEmoji.class.getName(),new WXEmojiStrategy());
        strategyMap.put(WXImage.class.getName(),new WXImageStrategy());
        strategyMap.put(WXFile.class.getName(),new WXFileStrategy());
        strategyMap.put(WXLink.class.getName(),new WXLinkStrategy());
        strategyMap.put(WXLocation.class.getName(),new WXLocationStrategy());
        strategyMap.put(WXMoney.class.getName(),new WXMoneyStrategy());
        strategyMap.put(WXNotify.class.getName(),new WXNotifyStrategy());
        strategyMap.put(WXRecommend.class.getName(),new WXRecommendStrategy());
        strategyMap.put(WXRevoke.class.getName(),new WXRevokeStrategy());
        strategyMap.put(WXSystem.class.getName(),new WXSystemStrategy());
        strategyMap.put(WXUnknown.class.getName(),new WXUnknownStrategy());
        strategyMap.put(WXVerify.class.getName(),new WXVerifyStrategy());
        strategyMap.put(WXVideo.class.getName(),new WXVideoStrategy());
        strategyMap.put(WXVoice.class.getName(),new WXVoiceStrategy());
    }

    @Override
    public void onQRCode(@Nonnull WeChatClient weChatClient, @Nonnull String qrCode) {
//        log.info("onQRCode：{}", qrCode);
    }

    /**
     * 获取用户头像，base64编码
     *
     * @param base64Avatar base64编码的用户头像
     */
    @Override
    public void onAvatar(@Nonnull WeChatClient client, @Nonnull String base64Avatar) {
    }

    /**
     * 模拟网页微信客户端异常退出
     *
     * @param reason 错误原因
     */
    @Override
    public void onFailure(@Nonnull WeChatClient client, @Nonnull String reason) {
        log.error("出现错误:{}",reason);
    }

    /**
     * 用户登录并初始化成功
     */
    @Override
    public void onLogin(@Nonnull WeChatClient client) {
        log.debug("登陆成功：您有{}名好友、活跃微信群{}个,关注了{}个公众号", client.userFriends().size(), client.userGroups().size(),client.userOfficials().size());
        HashMap<String, WXGroup> group = client.userGroups();
        if(!group.isEmpty()){
            for (WXGroup wxGroup: group.values()) {
                if(wxGroup.permission > 1){
//                    client.sendText(wxGroup, "[玫瑰][玫瑰][玫瑰][玫瑰]大家好，本群的微信智能助手已上线，祝您玩的开心哦！！[玫瑰][玫瑰][玫瑰][玫瑰]");
                }
            }
        }
        HashMap<String, WXUser> user = client.userFriends();
        if(!user.isEmpty()){
            for (WXUser wxUser: user.values()) {
                if(wxUser.permission > 1){
                    client.sendText(wxUser, "[玫瑰][玫瑰][玫瑰][玫瑰]您好，您的微信智能助手已上线，祝您玩的开心哦！！[玫瑰][玫瑰][玫瑰][玫瑰]");
                }
            }
        }
    }

    /**

    /**
     * 用户获取到消息
     *
     * @param message 用户获取到的消息
     */
    @Override
    public void onMessage(@Nonnull WeChatClient client, @Nonnull WXMessage message) {
        MessageStrategy messageStrategy = strategyMap.get(message.getClass().getName());
        if(messageStrategy!=null){
            messageStrategy.handleMessage(client,message);
        }
    }

    /**
     * 用户联系人变化
     *
     * @param client     微信客户端
     * @param oldContact 旧联系人，新增联系人时为null
     * @param newContact 新联系人，删除联系人时为null
     */
    @Override
    public void onContact(@Nonnull WeChatClient client, @Nullable WXContact oldContact, @Nullable WXContact newContact) {
        if (oldContact != null && newContact != null && oldContact.name.equals(newContact.name)) {
            if(!(oldContact instanceof WXGroup) || !(newContact instanceof WXGroup)){
                return;
            }
            List<WXGroup.Member> list = null;
            if(((WXGroup)newContact).members.size()==0||((WXGroup)oldContact).members.size()==0){
                return;
            }
            if(((WXGroup)newContact).members.size()>((WXGroup)oldContact).members.size()){
                list = compareHashMap(((WXGroup)oldContact).members,((WXGroup)newContact).members);
                if(list.isEmpty()){
                    return;
                }
                for (WXGroup.Member member: list) {
                    log.info("群{}新增联系人{}", newContact.name,member.name);
                    String  atPrefix = "@" + HTMLSpirit.delHTMLTag(member.name) + AT_ME_SPACE;
                    if(newContact.permission > 1){
                        client.sendText(newContact, atPrefix + "\n欢迎加入群\""+newContact.name+"\",我是本群的微信智能机器人，祝您玩的开心哦！！[玫瑰][玫瑰][玫瑰][玫瑰]");
                    }
                }
            }else{
                list = compareHashMap(((WXGroup)newContact).members,((WXGroup)oldContact).members);
                if(list.isEmpty()){
                    return;
                }
                for (WXGroup.Member member: list) {
                    log.info("群{}删除联系人{}", newContact.name, member.name);
                    if (newContact.permission > 1) {
                        client.sendText(newContact, "我是微信智能机器人，很遗憾的通知各位，成员" + HTMLSpirit.delHTMLTag((StringUtils.isNotEmpty(member.display) ? member.display : member.name)) + "刚才离开了群\"" + newContact.name + "\",大家记得跟他常联系哦！！");
                    }
                }
            }
        }else if(oldContact == null && newContact != null){
            if(newContact instanceof WXGroup){
                log.info("加入群: {}",  newContact.name);
                if(newContact.permission > 1) {
                    client.sendText(newContact, "谢谢群主大大和各位小可爱，我是你们的微信智能机器人，欢迎跟我玩耍哦！！[玫瑰][玫瑰][玫瑰][玫瑰]");
                }
            }else {
                log.info("新增联系人: {}",  newContact.name);
//                client.sendText(newContact, "你好，我是你的微信智能机器人，欢迎跟我玩耍哦！！[玫瑰][玫瑰][玫瑰][玫瑰]");
            }
        }else if(oldContact != null && newContact == null){
            if(oldContact instanceof WXGroup){
                log.info("退出群: {}",  newContact.name);
            }else {
                log.info("删除联系人: {}",  oldContact.name);
            }
        }
    }

    /**
     * 模拟网页微信客户端正常退出
     */
    @Override
    public void onLogout(@Nonnull WeChatClient client) {
        log.info("微信用户{}退出登录",client.userMe().name);
    }

    private List<WXGroup.Member> compareHashMap(Map<String, WXGroup.Member> oldMap, Map<String, WXGroup.Member> newMap){
        List<WXGroup.Member> result = new ArrayList<>();
        Set<String> keySet = newMap.keySet();
        for (String key:keySet) {
            if(oldMap.get(key)==null){
                result.add(newMap.get(key));
            }
        }
        return result;
    }
}
