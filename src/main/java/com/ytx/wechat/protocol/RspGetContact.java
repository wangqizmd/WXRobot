package com.ytx.wechat.protocol;


import java.util.ArrayList;

public class RspGetContact {
    public com.ytx.wechat.protocol.BaseResponse BaseResponse;
    public int MemberCount;
    public ArrayList<RspInit.User> MemberList;
    public int Seq;
}
