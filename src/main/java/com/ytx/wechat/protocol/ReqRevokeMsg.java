package com.ytx.wechat.protocol;

public class ReqRevokeMsg {
    public com.ytx.wechat.protocol.BaseRequest BaseRequest;
    public String ClientMsgId;
    public String SvrMsgId;
    public String ToUserName;

    public ReqRevokeMsg(com.ytx.wechat.protocol.BaseRequest baseRequest, String clientMsgId, String serverMsgId, String toUserName) {
        this.BaseRequest = baseRequest;
        this.ClientMsgId = clientMsgId;
        this.SvrMsgId = serverMsgId;
        this.ToUserName = toUserName;
    }
}
