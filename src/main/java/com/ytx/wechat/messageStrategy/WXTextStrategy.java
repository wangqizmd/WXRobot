package com.ytx.wechat.messageStrategy;

import com.ytx.wechat.client.WeChatClient;
import com.ytx.wechat.utils.GroupMsgUtil;
import lombok.extern.slf4j.Slf4j;
import com.ytx.wechat.entity.message.WXMessage;
import com.ytx.wechat.entity.contact.WXGroup;

@Slf4j
public class WXTextStrategy implements MessageStrategy {

    /**
     * 处理文本消息
     */
    @Override
    public void handleMessage(WeChatClient client,WXMessage message) {
        if(message.fromUser == null){
            return ;
        }
        if (message.fromGroup != null) {
            String name = GroupMsgUtil.getUserDisplayOrName(message);
            log.info("收到群消息。来自群: {}，发送人：{}，内容: {}", message.fromGroup.name, name,message.content);
        } else {
            if(message.toContact instanceof WXGroup){
                log.info("自己向群\"{}\"发送消息，内容: {}", message.toContact.name,message.content);
            }else if(message.fromUser.id.equals(client.userMe().id)){
                log.info("自己向\"{}\"发送消息，内容: {}", message.toContact.name,message.content);
            }else{
                log.info("收到消息。来自：{}，内容: {}", message.fromUser.name,message.content);
            }
        }
    }
}
