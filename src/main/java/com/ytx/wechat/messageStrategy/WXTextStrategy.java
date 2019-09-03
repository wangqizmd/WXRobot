package com.ytx.wechat.messageStrategy;

import com.ytx.wechat.api.weather.WeatherApi;
import com.ytx.wechat.client.WeChatClient;
import com.ytx.wechat.listener.WeChatClientListener;
import com.ytx.wechat.utils.GroupMsgUtil;
import lombok.extern.slf4j.Slf4j;
import com.ytx.wechat.entity.message.WXMessage;
import com.ytx.wechat.entity.contact.WXGroup;
import org.apache.commons.lang3.StringUtils;

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
            if(message.fromGroup.permission==2){
                String result = WeatherApi.dealWeatherMsg(message);
                if(StringUtils.isNotEmpty(result)){
                    String  atPrefix = "@" + message.fromGroup.members.get(message.fromUser.id).name + WeChatClientListener.AT_ME_SPACE;
                    client.sendText(message.fromGroup, atPrefix + " " + result);
                }
            }
        } else {
            if(message.toContact instanceof WXGroup){
                log.info("自己向群\"{}\"发送消息，内容: {}", message.toContact.name,message.content);
                setPermission(message);
            }else if(message.fromUser.id.equals(client.userMe().id)){
                log.info("自己向\"{}\"发送消息，内容: {}", message.toContact.name,message.content);
                setPermission(message);
            }else{
                String name = StringUtils.isEmpty(message.fromUser.remark) ? message.fromUser.name : message.fromUser.remark;
                log.info("收到消息。来自：{}，内容: {}", name,message.content);
                if(message.fromUser.permission==2){
                    String result = WeatherApi.dealWeatherMsg(message);
                    if(StringUtils.isNotEmpty(result)){
                        client.sendText(message.fromUser, result);
                    }
                }
            }
        }
    }

    private void setPermission(WXMessage message) {
        if(message.content.contains("白名单")){
            message.toContact.permission=2;
        }else if(message.content.contains("黑名单")){
            message.toContact.permission=3;
        }else if(message.content.contains("指令模式")){
            message.toContact.permission=1;
        }else if(message.content.contains("默认")){
            message.toContact.permission=0;
        }
    }
}
