package com.ytx.wechat.messageStrategy;

import com.alibaba.fastjson.JSONObject;
import com.ytx.wechat.client.WeChatClient;
import lombok.extern.slf4j.Slf4j;
import me.xuxiaoxiao.chatapi.wechat.entity.message.WXMessage;

@Slf4j
public class WXMoneyStrategy implements MessageStrategy {
    @Override
    public void handleMessage(WeChatClient client, WXMessage message) {
        log.info("收到转账消息。来自：{}，内容: {}", JSONObject.toJSONString(message));
    }
}
