package com.ytx.wechat.protocol;

public class ReqUpdateChatroom {
    public com.ytx.wechat.protocol.BaseRequest BaseRequest;
    public String ChatRoomName;
    public String NewTopic;
    public String AddMemberList;
    public String DelMemberList;

    public ReqUpdateChatroom(com.ytx.wechat.protocol.BaseRequest baseRequest, String chatroomName, String fun, String name, String members) {
        this.BaseRequest = baseRequest;
        this.ChatRoomName = chatroomName;
        this.NewTopic = name;
        switch (fun) {
            case "addmember":
                this.AddMemberList = members;
                break;
            case "delmember":
                this.DelMemberList = members;
                break;
        }
    }
}
