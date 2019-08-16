package com.ytx.wechat.protocol;

import java.util.ArrayList;

public class RspInit {
    public com.ytx.wechat.protocol.BaseResponse BaseResponse;
    public User User;
    public int Count;
    public ArrayList<User> ContactList;
    public SyncKey SyncKey;
    public String ChatSet;
    public String SKey;
    public long ClientVersion;
    public long SystemTime;
    public int GrayScale;
    public int InviteStartCount;
    public int MPSubscribeMsgCount;
    public ArrayList<MPSubscribeMsg> MPSubscribeMsgList;
    public long ClickReportInterval;

    public static class SyncKey {
        public int Count;
        public ArrayList<SyncKeyItem> List;

        @Override
        public String toString() {
            StringBuilder sbKey = new StringBuilder();
            for (SyncKeyItem item : List) {
                if (sbKey.length() > 0) {
                    sbKey.append("|");
                }
                sbKey.append(item.Key).append("_").append(item.Val);
            }
            return sbKey.toString();
        }

        public static class SyncKeyItem {
            public int Key;
            public int Val;
        }
    }

    public static class MPSubscribeMsg {
        public String UserName;
        public String NickName;
        public long Time;
        public int MPArticleCount;
        public ArrayList<MPArticle> MPArticleList;

        public static class MPArticle {
            public String Title;
            public String Digest;
            public String Cover;
            public String Url;
        }
    }

    public static class User {
        public long Uin;
        public String UserName;
        public String NickName;
        public String HeadImgUrl;
        public String RemarkName;
        public String PYInitial;
        public String PYQuanPin;
        public String RemarkPYInitial;
        public String RemarkPYQuanPin;
        public int HideInputBarFlag;
        public int StarFriend;
        public int Sex;
        public String Signature;
        public int AppAccountFlag;
        public int VerifyFlag;
        public int ContactFlag;
        public int WebWxPluginSwitch;
        public int HeadImgFlag;
        public int SnsFlag;

        public long OwnerUin;
        public int MemberCount;
        public ArrayList<User> MemberList;
        public long Statues;
        public long AttrStatus;
        public long MemberStatus;
        public String Province;
        public String City;
        public String Alias;
        public long UniFriend;
        public String DisplayName;
        public long ChatRoomId;
        public String KeyWord;
        public int IsOwner;
        public String EncryChatRoomId;
    }
}
