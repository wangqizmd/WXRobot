package com.ytx.wechat.messageStrategy;

import com.alibaba.fastjson.JSONObject;
import com.ytx.wechat.client.WeChatClient;
import lombok.extern.slf4j.Slf4j;
import com.ytx.wechat.entity.message.WXMessage;

@Slf4j
public class WXRecommendStrategy implements MessageStrategy {
    @Override
    public void handleMessage(WeChatClient weChatClient, WXMessage message) {
        log.info("收到名片消息。来自：{}，内容: {}", JSONObject.toJSONString(message));
    }
}
