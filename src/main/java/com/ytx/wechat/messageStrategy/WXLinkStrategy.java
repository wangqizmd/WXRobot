package com.ytx.wechat.messageStrategy;

import com.ytx.wechat.client.WeChatClient;
import com.ytx.wechat.utils.GroupMsgUtil;
import lombok.extern.slf4j.Slf4j;
import me.xuxiaoxiao.chatapi.wechat.entity.contact.WXGroup;
import me.xuxiaoxiao.chatapi.wechat.entity.message.WXLink;
import me.xuxiaoxiao.chatapi.wechat.entity.message.WXMessage;

@Slf4j
public class WXLinkStrategy implements MessageStrategy {
    @Override
    public void handleMessage(WeChatClient client, WXMessage message) {
        if(message.fromUser == null){
            return ;
        }
        if (message.fromGroup != null) {
            String name = GroupMsgUtil.getUserDisplayOrName(message);
            log.info("收到群链接消息。来自群: {}，发送人：{}，内容: {}，链接: {}", message.fromGroup.name, name,((WXLink)message).linkName,((WXLink)message).linkUrl);
        } else {
            if(message.toContact instanceof WXGroup){
                log.info("自己向群\"{}\"发送链接消息，内容: {}，链接: {}", message.toContact.name,((WXLink)message).linkName,((WXLink)message).linkUrl);
            }else if(message.fromUser.id.equals(client.userMe().id)){
                log.info("自己向\"{}\"发送链接消息，内容: {}，链接: {}", message.toContact.name,((WXLink)message).linkName,((WXLink)message).linkUrl);
            }else{
                log.info("收到链接消息。来自：{}，内容: {}，链接: {}", message.fromUser.name,((WXLink)message).linkName,((WXLink)message).linkUrl);
            }
        }
    }
}
