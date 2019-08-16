package com.ytx.wechat.protocol;

import me.xuxiaoxiao.chatapi.wechat.protocol.RspInit.SyncKey;

import java.util.ArrayList;

public class RspSync {
    public com.ytx.wechat.protocol.BaseResponse BaseResponse;
    public int AddMsgCount;
    public ArrayList<AddMsg> AddMsgList;
    public int ModContactCount;
    public ArrayList<RspInit.User> ModContactList;
    public int DelContactCount;
    public ArrayList<RspInit.User> DelContactList;
    public int ModChatRoomMemberCount;
    public ArrayList<RspInit.User> ModChatRoomMemberList;
    public Profile Profile;
    public int ContinueFlag;
    public SyncKey SyncKey;
    public String Skey;
    public SyncKey SyncCheckKey;

    public static class Profile {
        public int BitFlag;
        public ProfileItem UserName;
        public ProfileItem NickName;
        public long BindUin;
        public ProfileItem BindEmail;
        public ProfileItem BindMobile;
        public long Status;
        public int Sex;
        public int PersonalCard;
        public String Alias;
        public int HeadImgUpdateFlag;
        public String HeadImgUrl;
        public String Signature;

        public static class ProfileItem {
            public String Buff;
        }
    }

    public static class AddMsg {
        /**
         * 文字消息
         */
        public static final int TYPE_TEXT = 1;
        /**
         * 图片消息
         */
        public static final int TYPE_IMAGE = 3;
        /**
         * 语音消息
         */
        public static final int TYPE_VOICE = 34;
        /**
         * 好友请求
         */
        public static final int TYPE_VERIFY = 37;
        /**
         * 名片消息
         */
        public static final int TYPE_RECOMMEND = 42;
        /**
         * 视频消息
         */
        public static final int TYPE_VIDEO = 43;
        /**
         * 收藏的表情
         */
        public static final int TYPE_EMOJI = 47;
        /**
         * 定位消息
         */
        public static final int TYPE_LOCATION = 48;
        /**
         * 转账、文件、链接、笔记等
         */
        public static final int TYPE_OTHER = 49;

        /**
         * 消息已读
         */
        public static final int TYPE_NOTIFY = 51;
        /**
         * 系统消息
         */
        public static final int TYPE_SYSTEM = 10000;
        /**
         * 系统消息，撤回消息
         */
        public static final int TYPE_REVOKE = 10002;

        public long MsgId;
        public String FromUserName;
        public String ToUserName;
        public int MsgType;
        public String Content;
        public long Status;
        public long ImgStatus;
        public long CreateTime;
        public long VoiceLength;
        public int PlayLength;
        public String FileName;
        public String FileSize;
        public String MediaId;
        public String Url;
        public int AppMsgType;
        public int StatusNotifyCode;
        public String StatusNotifyUserName;
        public RecommendInfo RecommendInfo;
        public int ForwardFlag;
        public AppInfo AppInfo;
        public int HasProductId;
        public String Ticket;
        public int ImgHeight;
        public int ImgWidth;
        public int SubMsgType;
        public long NewMsgId;

        public static class RecommendInfo {
            public String UserName;
            public String NickName;
            public long QQNum;
            public String Province;
            public String City;
            public String Content;
            public String Signature;
            public String Alias;
            public int Scene;
            public int VerifyFlag;
            public long AttrStatus;
            public int Sex;
            public String Ticket;
            public int OpCode;
        }

        public static class AppInfo {
            public String AppID;
            public int Type;
        }
    }
}