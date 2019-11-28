package com.ytx.wechat.protocol;


import lombok.Data;

import java.util.ArrayList;

@Data
public class RspGetContact {
    public com.ytx.wechat.protocol.BaseResponse BaseResponse;
    public int MemberCount;
    public ArrayList<RspInit.User> MemberList;
    public int Seq;
}
