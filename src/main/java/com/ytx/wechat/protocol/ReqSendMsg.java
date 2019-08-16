package com.ytx.wechat.protocol;

public class ReqSendMsg {
    public com.ytx.wechat.protocol.BaseRequest BaseRequest;
    public Msg Msg;
    public int Scene;

    public ReqSendMsg(com.ytx.wechat.protocol.BaseRequest baseRequest, Msg msg) {
        this.BaseRequest = baseRequest;
        this.Msg = msg;
        this.Scene = 0;
    }

    public static class Msg {
        public int Type;
        public Integer EmojiFlag;
        public String MediaId;
        public String Content;
        public String Signature;
        public String FromUserName;
        public String ToUserName;
        public String LocalID;
        public String ClientMsgId;

        public Msg(int type, String mediaId, Integer emojiFlag, String content, String signature, String fromUserName, String toUserName) {
            this.Type = type;
            this.MediaId = mediaId;
            this.EmojiFlag = emojiFlag;
            this.Content = content;
            this.Signature = signature;
            this.FromUserName = fromUserName;
            this.ToUserName = toUserName;
            this.LocalID = msgId();
            this.ClientMsgId = LocalID;
        }

        public static String msgId() {
            StringBuilder sbRandom = new StringBuilder().append(System.currentTimeMillis());
            for (int i = 0; i < 4; i++) {
                sbRandom.append((int) (Math.random() * 10));
            }
            return sbRandom.toString();
        }
    }
}
