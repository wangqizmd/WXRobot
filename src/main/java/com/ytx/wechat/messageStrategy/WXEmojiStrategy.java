package com.ytx.wechat.messageStrategy;

import com.ytx.wechat.client.WeChatClient;
import com.ytx.wechat.entity.contact.WXGroup;
import com.ytx.wechat.entity.message.WXMessage;
import com.ytx.wechat.utils.GroupMsgUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WXEmojiStrategy implements MessageStrategy {
    @Override
    public void handleMessage(WeChatClient client, WXMessage message) {
        if(message.fromUser == null){
            return ;
        }
        if (message.fromGroup != null) {
            String name = GroupMsgUtil.getUserDisplayOrName(message);
            log.info("收到群表情消息。来自群: {}，发送人：{}", message.fromGroup.name, name);
        } else {
            if(message.toContact instanceof WXGroup){
                log.info("自己向群\"{}\"发送表情消息", message.toContact.name);
            }else if(message.fromUser.id.equals(client.userMe().id)){
                log.info("自己向\"{}\"发送表情消息", message.toContact.name);
            }else{
                log.info("收到表情消息。来自：{}", message.fromUser.name);
            }
        }
    }
}
