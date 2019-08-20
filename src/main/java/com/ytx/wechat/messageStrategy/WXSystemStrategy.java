package com.ytx.wechat.messageStrategy;

import com.ytx.wechat.client.WeChatClient;
import com.ytx.wechat.entity.contact.WXGroup;
import lombok.extern.slf4j.Slf4j;
import com.ytx.wechat.entity.message.WXMessage;

@Slf4j
public class WXSystemStrategy implements MessageStrategy {
    @Override
    public void handleMessage(WeChatClient client, WXMessage message) {
        if (message.fromGroup != null) {
            log.info("收到群系统消息。来自群: {}，内容: {}", message.fromGroup.name, message.content);
        } else {
            if(message.toContact instanceof WXGroup){
                log.info("自己向群\"{}\"发送系统消息，内容: {}", message.toContact.name,message.content);
            }else if(message.fromUser.id.equals(client.userMe().id)){
                log.info("自己向\"{}\"发送系统消息，内容: {}", message.toContact.name,message.content);
            }else{
                log.info("收到系统消息。来自：{}，内容: {}", message.fromUser.name,message.content);
            }
        }
    }
}
