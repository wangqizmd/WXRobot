package com.ytx.wechat.messageStrategy;

import com.ytx.wechat.api.chat.ChatApi;
import com.ytx.wechat.api.constellation.ConstellationApi;
import com.ytx.wechat.api.weather.WeatherApi;
import com.ytx.wechat.client.WeChatClient;
import com.ytx.wechat.entity.contact.WXGroup;
import com.ytx.wechat.entity.contact.WXUser;
import com.ytx.wechat.entity.message.WXMessage;
import com.ytx.wechat.utils.GroupMsgUtil;
import lombok.extern.slf4j.Slf4j;
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
            String result = dealContent(message,message.fromGroup.permission);
            if(StringUtils.isNotEmpty(result)){
                client.sendText(message.fromGroup, result);
            }
        } else {
            if(message.toContact instanceof WXGroup){
                log.info("自己向群\"{}\"发送消息，内容: {}", message.toContact.name,message.content);
                setPermission(message,client);
            }else if(message.fromUser.id.equals(client.userMe().id)){
                if(message.toContact!=null){
                    String name = StringUtils.isEmpty(((WXUser)message.toContact).remark) ? message.toContact.name : ((WXUser)message.toContact).remark;
                    log.info("自己向\"{}\"发送消息，内容: {}", name,message.content);
                    setPermission(message,client);
                }else{
                    log.info("自己向文件助手发送消息，内容: {}", message.content);
                }

            }else{
                String name = StringUtils.isEmpty(message.fromUser.remark) ? message.fromUser.name : message.fromUser.remark;
                log.info("收到消息。来自：{}，内容: {}", name,message.content);
                String result = dealContent(message,message.fromUser.permission);
                if(StringUtils.isNotEmpty(result)){
                    client.sendText(message.fromUser, result);
                }
            }
        }
    }

    private String dealContent(WXMessage message,int permission){
        if(permission > 1 ){
            //进行指令处理
            String result = dealModel(message);
            if(StringUtils.isNotEmpty(result)){
                return result;
            }
            if( permission > 2){
                //进行闲聊模式
                return ChatApi.dealMsg(message.content);
            }
        }
        return null;
    }

    private String dealModel(WXMessage message){
        String result = WeatherApi.dealWeatherMsg(message);
        if(StringUtils.isNotEmpty(result)){
            return result;
        }
        result = ConstellationApi.dealConstellationMsg(message);
        if(StringUtils.isNotEmpty(result)){
            return result;
        }
        //todo 后续指令
        return null;
    }
    private void setPermission(WXMessage message,WeChatClient client) {
        boolean flag = false;
        if(message.content.contains("默认")){
            message.toContact.permission=0;
        }else if(message.content.contains("指令模式")){
            message.toContact.permission=2;
            flag = true;
        }else if(message.content.contains("白名单")){
            message.toContact.permission=3;
            flag = true;
        }else if(message.content.contains("黑名单")){
            message.toContact.permission=1;
        }
        if(flag){
//            client.sendText(message.toContact, "[玫瑰][玫瑰][玫瑰][玫瑰]您的的微信智能助手已上线，祝您玩的开心哦！！[玫瑰][玫瑰][玫瑰][玫瑰]");
        }
    }
}
