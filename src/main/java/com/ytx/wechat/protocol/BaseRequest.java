package com.ytx.wechat.protocol;

public class BaseRequest {
    public final String Uin;
    public final String Sid;
    public final String Skey;
    public final String DeviceID;

    public BaseRequest(String uin, String sid, String skey) {
        this(uin, sid, skey, deviceId());
    }

    public BaseRequest(String uin, String sid, String skey, String deviceID) {
        this.Uin = uin;
        this.Sid = sid;
        this.Skey = skey;
        this.DeviceID = deviceID;
    }

    public static String deviceId() {
        StringBuilder sb = new StringBuilder("e");
        for (int i = 0; i < 15; i++) {
            sb.append((int) (Math.random() * 10));
        }
        return sb.toString();
    }
}
