package com.ytx.wechat.messageStrategy;

import com.ytx.wechat.client.WeChatClient;
import com.ytx.wechat.message.WXMessage;
/**
 * 消息处理
 */

public interface MessageStrategy {
    void handleMessage(WeChatClient client, WXMessage message);
}
