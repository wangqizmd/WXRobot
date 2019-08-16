package com.ytx.wechat.protocol;

public class ReqOplog {
    public static final int CMD_REMARK = 2;
    public static final int CMD_TOP = 3;

    public static final int OP_NONE = 0;
    public static final int OP_TOP_FALSE = 0;
    public static final int OP_TOP_TRUE = 1;

    public com.ytx.wechat.protocol.BaseRequest BaseRequest;
    public int CmdId;
    public int OP;
    public String UserName;
    public String RemarkName;

    public ReqOplog(com.ytx.wechat.protocol.BaseRequest baseRequest, int cmdId, int op, String userName, String remarkName) {
        this.BaseRequest = baseRequest;
        this.CmdId = cmdId;
        this.OP = op;
        this.UserName = userName;
        this.RemarkName = remarkName;
    }
}
