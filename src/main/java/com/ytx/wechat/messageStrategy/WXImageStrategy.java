package com.ytx.wechat.messageStrategy;

import com.ytx.wechat.client.WeChatClient;
import com.ytx.wechat.entity.contact.WXGroup;
import com.ytx.wechat.entity.message.WXImage;
import com.ytx.wechat.utils.GroupMsgUtil;
import lombok.extern.slf4j.Slf4j;
import com.ytx.wechat.entity.message.WXMessage;

@Slf4j
public class WXImageStrategy implements MessageStrategy {
    @Override
    public void handleMessage(WeChatClient client, WXMessage message) {
        if(message.fromUser == null){
            return ;
        }
        if (message.fromGroup != null) {
            String name = GroupMsgUtil.getUserDisplayOrName(message);
            log.info("收到图片消息。来自群: {}，发送人：{}，消息id: {}，文件地址：{}", message.fromGroup.name, name,message.id,((WXImage) message).image.getAbsolutePath());
        } else {
            if(message.toContact instanceof WXGroup){
                log.info("自己向群\"{}\"发送图片消息。消息id: {}，文件地址：{}", message.toContact.name,message.id,((WXImage) message).image.getAbsolutePath());
            }else if(message.fromUser.id.equals(client.userMe().id)){
                log.info("自己向\"{}\"发送图片消息，消息id: {}，文件地址：{}", message.toContact.name,message.id,((WXImage) message).image.getAbsolutePath());
            }else{
                log.info("收到图片消息。来自：{}，消息id: {}，文件地址：{}", message.fromUser.name,message.id,((WXImage) message).image.getAbsolutePath());
            }
        }
    }
}
