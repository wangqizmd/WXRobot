package com.ytx.wechat.protocol;

public class ReqSync {
    public final com.ytx.wechat.protocol.BaseRequest BaseRequest;
    public final RspInit.SyncKey SyncKey;
    public final int rr;

    public ReqSync(com.ytx.wechat.protocol.BaseRequest baseRequest, RspInit.SyncKey syncKey) {
        this.BaseRequest = baseRequest;
        this.SyncKey = syncKey;
        this.rr = (int) (~(System.currentTimeMillis()));
    }
}
