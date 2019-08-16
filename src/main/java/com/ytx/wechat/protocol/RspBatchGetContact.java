package com.ytx.wechat.protocol;

import java.util.ArrayList;

public class RspBatchGetContact {
    public com.ytx.wechat.protocol.BaseResponse BaseResponse;
    public int Count;
    public ArrayList<RspInit.User> ContactList;
}
