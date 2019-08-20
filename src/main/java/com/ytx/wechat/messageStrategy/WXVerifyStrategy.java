package com.ytx.wechat.messageStrategy;

import com.ytx.wechat.client.WeChatClient;
import com.ytx.wechat.entity.message.WXMessage;
import com.ytx.wechat.entity.message.WXVerify;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WXVerifyStrategy implements MessageStrategy {
    @Override
    public void handleMessage(WeChatClient client, WXMessage message) {
        //是好友请求消息，自动同意好友申请
        log.info("收到好友{}的申请", ((WXVerify) message).userName);
        client.passVerify((WXVerify) message);

    }
}
