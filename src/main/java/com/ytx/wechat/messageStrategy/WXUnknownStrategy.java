package com.ytx.wechat.messageStrategy;

import com.ytx.wechat.client.WeChatClient;
import lombok.extern.slf4j.Slf4j;
import com.ytx.wechat.entity.message.WXMessage;

@Slf4j
public class WXUnknownStrategy implements MessageStrategy {
    @Override
    public void handleMessage(WeChatClient weChatClient, WXMessage wxMessage) {

    }
}
