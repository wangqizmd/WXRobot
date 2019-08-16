package com.ytx.wechat.protocol;

import java.util.List;

public class ReqBatchGetContact {
    public final com.ytx.wechat.protocol.BaseRequest BaseRequest;
    public final int Count;
    public final List<Contact> List;

    public ReqBatchGetContact(com.ytx.wechat.protocol.BaseRequest baseRequest, List<Contact> contacts) {
        this.BaseRequest = baseRequest;
        this.Count = contacts.size();
        this.List = contacts;
    }

    public static class Contact {
        public final String UserName;
        public final String EncryChatRoomId;

        public Contact(String userName, String encryChatRoomId) {
            this.UserName = userName;
            this.EncryChatRoomId = encryChatRoomId;
        }
    }
}
