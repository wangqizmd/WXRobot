package com.ytx.wechat.protocol;

import lombok.Data;

import java.util.ArrayList;

@Data
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

    @Data
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

    @Data
    public static class User {
        public long Uin;
        /**
         * 用户唯一标识，一个"@"为好友，两个"@"为群组
         */
        public String UserName;
        /**
         * 微信昵称
         */
        public String NickName;
        /**
         * 微信头像URL
         */
        public String HeadImgUrl;
        /**
         * 备注名
         */
        public String RemarkName;
        /**
         * 用户名拼音缩写
         */
        public String PYInitial;
        /**
         * 用户名拼音全拼
         */
        public String PYQuanPin;
        /**
         * 备注拼音缩写
         */
        public String RemarkPYInitial;
        /**
         * 备注拼音全拼
         */
        public String RemarkPYQuanPin;
        /**
         * 隐藏输入框
         */
        public int HideInputBarFlag;
        /**
         * 星标朋友
         */
        public int StarFriend;
        /**
         * 性别，0-未设置（公众号、保密），1-男，2-女
         */
        public int Sex;
        public String Signature;
        public int AppAccountFlag;
        public int VerifyFlag;
        /**
         * 1-好友，2-群组，3-公众号
         */
        public int ContactFlag;
        public int WebWxPluginSwitch;
        public int HeadImgFlag;
        public int SnsFlag;

        ///////////////////群聊相关/////////////////////

        public long OwnerUin;
        /**
         * 成员数量，只有在群组信息中才有效
         */
        public int MemberCount;
        /**
         * 群成员列表
         */
        public ArrayList<User> MemberList;
        public long Statues;
        public long AttrStatus;
        public long MemberStatus;
        /**
         * 省
         */
        public String Province;
        /**
         * 市
         */
        public String City;
        /**
         * 别名
         */
        public String Alias;
        public long UniFriend;
        public String DisplayName;
        public long ChatRoomId;
        public String KeyWord;
        public int IsOwner;
        /**
         * 群id
         */
        public String EncryChatRoomId;
    }
}
