package com.ytx.wechat.protocol;

import java.util.Random;

public class ReqUploadMedia {
    public com.ytx.wechat.protocol.BaseRequest BaseRequest;
    public long ClientMediaId;
    public int UploadType;
    public long TotalLen;
    public long StartPos;
    public long DataLen;
    public int MediaType;
    public String FromUserName;
    public String ToUserName;
    public String FileMd5;
    public String AESKey;
    public String Signature;

    public ReqUploadMedia(com.ytx.wechat.protocol.BaseRequest baseRequest, long clientMediaId, int uploadType, long totalLen, long startPos, long dataLen, String fileMd5, String aesKey, String signature, String fromUserName, String toUserName) {
        this.BaseRequest = baseRequest;
        this.ClientMediaId = clientMediaId;
        this.UploadType = uploadType;
        this.TotalLen = totalLen;
        this.StartPos = startPos;
        this.DataLen = dataLen;
        this.FileMd5 = fileMd5;
        this.AESKey = aesKey;
        this.Signature = signature;
        this.MediaType = 4;
        this.FromUserName = fromUserName;
        this.ToUserName = toUserName;
    }

    public static long clientMediaId() {
        return System.currentTimeMillis() * 10000 + new Random().nextInt(10000);
    }
}
