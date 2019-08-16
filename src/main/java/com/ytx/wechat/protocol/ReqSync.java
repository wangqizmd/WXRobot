package com.ytx.wechat.protocol;

import me.xuxiaoxiao.chatapi.wechat.protocol.RspInit.SyncKey;

public class ReqSync {
    public final com.ytx.wechat.protocol.BaseRequest BaseRequest;
    public final SyncKey SyncKey;
    public final int rr;

    public ReqSync(com.ytx.wechat.protocol.BaseRequest baseRequest, SyncKey syncKey) {
        this.BaseRequest = baseRequest;
        this.SyncKey = syncKey;
        this.rr = (int) (~(System.currentTimeMillis()));
    }
}
