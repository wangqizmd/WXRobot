package com.ytx.wechat.messageStrategy;

import com.ytx.wechat.client.WeChatClient;
import lombok.extern.slf4j.Slf4j;
import com.ytx.wechat.entity.message.WXMessage;

@Slf4j
public class WXNotifyStrategy implements MessageStrategy {
    @Override
    public void handleMessage(WeChatClient client, WXMessage message) {
//        log.info("收到状态消息。来自：{}，内容: {}", JSONObject.toJSONString(message));
    }
}
