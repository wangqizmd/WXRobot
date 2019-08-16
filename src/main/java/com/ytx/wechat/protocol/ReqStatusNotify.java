package com.ytx.wechat.protocol;

public class ReqStatusNotify {
    public com.ytx.wechat.protocol.BaseRequest BaseRequest;
    public int Code;
    public String FromUserName;
    public String ToUserName;
    public long ClientMsgId;

    public ReqStatusNotify(com.ytx.wechat.protocol.BaseRequest baseRequest, int code, String myName) {
        this.BaseRequest = baseRequest;
        this.Code = code;
        this.FromUserName = myName;
        this.ToUserName = myName;
        this.ClientMsgId = System.currentTimeMillis();
    }
}
