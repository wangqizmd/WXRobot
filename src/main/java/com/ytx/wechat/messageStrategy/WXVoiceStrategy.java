package com.ytx.wechat.messageStrategy;

import com.alibaba.fastjson.JSONObject;
import com.ytx.wechat.client.WeChatClient;
import com.ytx.wechat.entity.message.WXMessage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WXVoiceStrategy implements MessageStrategy {
    @Override
    public void handleMessage(WeChatClient client, WXMessage message) {
//        log.info("收到语音消息。来自：{}，内容: {}", JSONObject.toJSONString(message));
    }
}
