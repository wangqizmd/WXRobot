package com.ytx.wechat.client;

import com.alibaba.fastjson.JSONObject;
import com.ytx.wechat.protocol.*;
import me.xuxiaoxiao.xtools.common.XTools;
import me.xuxiaoxiao.xtools.common.http.executor.XHttpExecutor;
import me.xuxiaoxiao.xtools.common.http.executor.impl.XHttpExecutorImpl;
import me.xuxiaoxiao.xtools.common.http.executor.impl.XRequest;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;

/**
 * 网页版微信全部接口
 */
final class WeChatApi {
    public static final String CFG_WORKDIR = WeChatClient.CFG_PREFIX + "workdir";
    public static final String CFG_WORKDIR_DEFAULT = "";

    private static final String[] HOSTS = {"wx.qq.com", "wx2.qq.com", "wx8.qq.com", "web.wechat.com", "web2.wechat.com"};
    private static final Pattern REX_LOGIN = Pattern.compile("<error>[\\s\\S]+</error>");

    private final long timeInit = System.currentTimeMillis();
    private final AtomicBoolean firstLogin = new AtomicBoolean(true);
    private final XHttpExecutor httpExecutor = new XHttpExecutorImpl();

    String host;
    String uin;
    String sid;
    String dataTicket;
    File folder = new File(XTools.cfgDef(CFG_WORKDIR, CFG_WORKDIR_DEFAULT));

    private long time = timeInit;
    private int file = 0;
    private String uuid;
    private String skey;
    private String passticket;
    private RspInit.SyncKey synckey;
    private RspInit.SyncKey syncCheckKey;

    /**
     * 获取登录二维码
     *
     * @return 登录二维码网址
     */
    String getQrCode() {
        XRequest request = XRequest.GET("https://login.wx.qq.com/jslogin");
        request.query("_", time++);
        request.query("appid", "wx782c26e4c19acffb");
        request.query("fun", "new");
        request.query("lang", "zh_CN");
        request.query("redirect_uri", "https://wx.qq.com/cgi-bin/mmwebwx-bin/webwxnewloginpage");
        String rspStr = XTools.http(httpExecutor, request).string();
        if (XTools.strEmpty(rspStr)) {
            throw new IllegalStateException("获取登录二维码出错");
        } else {
            this.uuid = rspStr.substring(rspStr.indexOf('"') + 1, rspStr.lastIndexOf('"'));
            return "https://login.weixin.qq.com/qrcode/" + uuid;
        }
    }

    /**
     * 监听登录，循环请求该接口，如果用户扫描或授权登录，该接口立即返回，否则将会在25秒后返回
     *
     * @return 监听结果，code=200用户授权登录，code=201用户扫描二维码，code=408等待用户扫描或授权，其他则表示登录超时
     */
    RspLogin login() {
        XRequest request = XRequest.GET("https://login.wx.qq.com/cgi-bin/mmwebwx-bin/login");
        request.query("_", time++);
        request.query("loginicon", true);
        request.query("r", (int) (~(System.currentTimeMillis())));
        request.query("tip", firstLogin.getAndSet(false) ? 1 : 0);
        request.query("uuid", uuid);
        RspLogin rspLogin = new RspLogin(XTools.http(httpExecutor, request).string());
        if (!XTools.strEmpty(rspLogin.redirectUri)) {
            for (String host : HOSTS) {
                if (rspLogin.redirectUri.contains(host)) {
                    this.host = host;
                    break;
                }
            }
            if (XTools.strEmpty(host)) {
                throw new IllegalStateException("未知主机");
            }
        }
        return rspLogin;
    }

    /**
     * 用户登录，返回uin,sid等重要信息，如果该接口返回数据为空，则uin，sid等数据在cookie中获取
     *
     * @param url 登录url
     */
    void webwxnewloginpage(String url) throws Exception {
        String rspStr = XTools.http(httpExecutor, XRequest.GET(url)).string();
        if (!XTools.strEmpty(rspStr) && REX_LOGIN.matcher(rspStr).find()) {
            String ret = rspStr.substring(rspStr.indexOf("<ret>") + "<ret>".length(), rspStr.indexOf("</ret>"));
            if(StringUtils.isNotEmpty(ret) && "0".equals(ret)){
                this.uin = rspStr.substring(rspStr.indexOf("<wxuin>") + "<wxuin>".length(), rspStr.indexOf("</wxuin>"));
                this.sid = rspStr.substring(rspStr.indexOf("<wxsid>") + "<wxsid>".length(), rspStr.indexOf("</wxsid>"));
                this.skey = rspStr.substring(rspStr.indexOf("<skey>") + "<skey>".length(), rspStr.indexOf("</skey>"));
                this.passticket = rspStr.substring(rspStr.indexOf("<pass_ticket>") + "<pass_ticket>".length(), rspStr.indexOf("</pass_ticket>"));
            }else{
                String message = rspStr.substring(rspStr.indexOf("<message>") + "<message>".length(), rspStr.indexOf("</message>"));
                throw new Exception(message);
            }
        }
    }

    /**
     * 初始化，获取自身信息，好友列表，活跃群等
     *
     * @return 初始化结果
     */
    RspInit webwxinit() {
        XRequest request = XRequest.POST(String.format("https://%s/cgi-bin/mmwebwx-bin/webwxinit", host));
        request.query("r", (int) (~(this.timeInit)));
        request.query("pass_ticket", this.passticket);
        request.content(new XRequest.StringContent(XRequest.MIME_JSON, JSONObject.toJSONString(new ReqInit(new BaseRequest(uin, sid, skey)))));
        RspInit rspInit = JSONObject.parseObject(XTools.http(httpExecutor, request).string(),RspInit.class);
        this.skey = rspInit.SKey;
        this.synckey = rspInit.SyncKey;
        return rspInit;
    }

    /**
     * 状态更新接口，登录登出，消息已读
     *
     * @param userName   目标联系人userName
     * @param notifyCode 状态码
     * @return 接口调用结果
     */
    RspStatusNotify webwxstatusnotify(String userName, int notifyCode) {
        XRequest request = XRequest.POST(String.format("https://%s/cgi-bin/mmwebwx-bin/webwxstatusnotify", host));
        request.query("pass_ticket", this.passticket);
        request.content(new XRequest.StringContent(XRequest.MIME_JSON, JSONObject.toJSONString(new ReqStatusNotify(new BaseRequest(uin, sid, skey), notifyCode, userName))));
        return JSONObject.parseObject(XTools.http(httpExecutor, request).string(),RspStatusNotify.class);
    }

    /**
     * 获取联系人列表
     *
     * @return 联系人列表
     */
    RspGetContact webwxgetcontact(int seq) {
        XRequest request = XRequest.GET(String.format("https://%s/cgi-bin/mmwebwx-bin/webwxgetcontact", host));
        request.query("r", System.currentTimeMillis());
        request.query("seq",seq);
        request.query("skey", this.skey);
        request.query("pass_ticket", this.passticket);
        return JSONObject.parseObject(XTools.http(httpExecutor, request).string(),RspGetContact.class);
    }

    /**
     * 批量获取联系人详细信息
     *
     * @param contactList 要获取的联系人列表
     * @return 联系人的详细信息
     */
    RspBatchGetContact webwxbatchgetcontact(List<ReqBatchGetContact.Contact> contactList) {
        XRequest request = XRequest.POST(String.format("https://%s/cgi-bin/mmwebwx-bin/webwxbatchgetcontact", host));
        request.query("r", System.currentTimeMillis());
        request.query("type", "ex");
        request.query("pass_ticket", this.passticket);
        request.content(new XRequest.StringContent(XRequest.MIME_JSON, JSONObject.toJSONString(new ReqBatchGetContact(new BaseRequest(uin, sid, skey), contactList))));
        return JSONObject.parseObject(XTools.http(httpExecutor, request).string(),RspBatchGetContact.class);
    }

    /**
     * 同步检查接口，需要无限循环请求该接口，如果有消息要同步，则该接口立即返回并携带参数，否则将在60秒后返回
     *
     * @return 检查结果
     */
    RspSyncCheck synccheck() {
        XRequest request = XRequest.GET(String.format("https://webpush.%s/cgi-bin/mmwebwx-bin/synccheck", host));
        request.query("uin", this.uin);
        request.query("sid", this.sid);
        request.query("skey", this.skey);
        request.query("deviceId", BaseRequest.deviceId());
        request.query("synckey", this.syncCheckKey != null ? this.syncCheckKey : this.synckey);
        request.query("r", System.currentTimeMillis());
        request.query("_", time++);
        return new RspSyncCheck(XTools.http(httpExecutor, request).string());
    }

    /**
     * 同步接口，将服务端数据同步到本地，并更新本地SyncKey
     *
     * @return 获取到的数据
     */
    RspSync webwxsync() {
        XRequest request = XRequest.POST(String.format("https://%s/cgi-bin/mmwebwx-bin/webwxsync", host));
        request.query("sid", this.sid);
        request.query("skey", this.skey);
        request.query("pass_ticket", this.passticket);
        request.content(new XRequest.StringContent(XRequest.MIME_JSON, JSONObject.toJSONString(new ReqSync(new BaseRequest(uin, sid, skey), this.synckey))));
        RspSync rspSync = JSONObject.parseObject(XTools.http(httpExecutor, request).string(),RspSync.class);
        if (rspSync.SyncKey != null && rspSync.SyncKey.List != null && !rspSync.SyncKey.List.isEmpty()) {
            this.synckey = rspSync.SyncKey;
        }
        if (rspSync.SyncCheckKey != null && rspSync.SyncCheckKey.List != null && !rspSync.SyncCheckKey.List.isEmpty()) {
            this.syncCheckKey = rspSync.SyncCheckKey;
        }
        return rspSync;
    }

    /**
     * 发送消息接口
     *
     * @param msg 需要发送的消息
     * @return 发送的结果
     */
    RspSendMsg webwxsendmsg(ReqSendMsg.Msg msg) {
        XRequest request = XRequest.POST(String.format("https://%s/cgi-bin/mmwebwx-bin/webwxsendmsg", host));
        request.query("pass_ticket", this.passticket);
        request.content(new XRequest.StringContent(XRequest.MIME_JSON, JSONObject.toJSONString(new ReqSendMsg(new BaseRequest(uin, sid, skey), msg))));
        return JSONObject.parseObject(XTools.http(httpExecutor, request).string(), RspSendMsg.class);
    }

    /**
     * 发送图片消息
     *
     * @param msg 需要发送的图片消息
     * @return 发送的结果
     */
    RspSendMsg webwxsendmsgimg(ReqSendMsg.Msg msg) {
        XRequest request = XRequest.POST(String.format("https://%s/cgi-bin/mmwebwx-bin/webwxsendmsgimg", host));
        request.query("fun", "async");
        request.query("f", "json");
        request.query("pass_ticket", this.passticket);
        request.content(new XRequest.StringContent(XRequest.MIME_JSON, JSONObject.toJSONString(new ReqSendMsg(new BaseRequest(uin, sid, skey), msg))));
        return JSONObject.parseObject(XTools.http(httpExecutor, request).string(), RspSendMsg.class);
    }

    /**
     * 发送视频消息
     *
     * @param msg 需要发送的视频消息
     * @return 发送的结果
     */
    RspSendMsg webwxsendvideomsg(ReqSendMsg.Msg msg) {
        XRequest request = XRequest.POST(String.format("https://%s/cgi-bin/mmwebwx-bin/webwxsendvideomsg", host));
        request.query("fun", "async");
        request.query("f", "json");
        request.query("pass_ticket", this.passticket);
        request.content(new XRequest.StringContent(XRequest.MIME_JSON, JSONObject.toJSONString(new ReqSendMsg(new BaseRequest(uin, sid, skey), msg))));
        return JSONObject.parseObject(XTools.http(httpExecutor, request).string(), RspSendMsg.class);
    }

    /**
     * 发送动态表情消息
     *
     * @param msg 需要发送的动态表情消息
     * @return 发送的结果
     */
    RspSendMsg webwxsendemoticon(ReqSendMsg.Msg msg) {
        XRequest request = XRequest.POST(String.format("https://%s/cgi-bin/mmwebwx-bin/webwxsendemoticon", host));
        request.query("fun", "sys");
        request.query("f", "json");
        request.query("pass_ticket", this.passticket);
        request.content(new XRequest.StringContent(XRequest.MIME_JSON, JSONObject.toJSONString(new ReqSendMsg(new BaseRequest(uin, sid, skey), msg))));
        return JSONObject.parseObject(XTools.http(httpExecutor, request).string(), RspSendMsg.class);
    }

    /**
     * 发送文件附件消息
     *
     * @param msg 需要发送的文件附件消息
     * @return 发送的结果
     */
    RspSendMsg webwxsendappmsg(ReqSendMsg.Msg msg) {
        XRequest request = XRequest.POST(String.format("https://%s/cgi-bin/mmwebwx-bin/webwxsendappmsg", host));
        request.query("fun", "async");
        request.query("f", "json");
        request.query("pass_ticket", this.passticket);
        request.content(new XRequest.StringContent(XRequest.MIME_JSON, JSONObject.toJSONString(new ReqSendMsg(new BaseRequest(uin, sid, skey), msg))));
        return JSONObject.parseObject(XTools.http(httpExecutor, request).string(), RspSendMsg.class);
    }

    /**
     * 撤回消息
     *
     * @param clientMsgId 本地消息id
     * @param serverMsgId 服务端消息id
     * @param userName    消息接收人userName
     * @return 撤回结果
     */
    RspRevokeMsg webwxrevokemsg(long clientMsgId, long serverMsgId, String userName) {
        XRequest request = XRequest.POST(String.format("https://%s/cgi-bin/mmwebwx-bin/webwxrevokemsg", host));
        request.content(new XRequest.StringContent(XRequest.MIME_JSON, JSONObject.toJSONString(new ReqRevokeMsg(new BaseRequest(uin, sid, skey), String.valueOf(clientMsgId), String.valueOf(serverMsgId), userName))));
        return JSONObject.parseObject(XTools.http(httpExecutor, request).string(), RspRevokeMsg.class);
    }

    /**
     * 根据MsgId获取那条消息的图片
     *
     * @param msgId 消息ID
     * @param type  图片的类型，slave：小图，big：大图，不传该参数则默认获取大图
     * @return 获取到的图片文件
     */
    File webwxgetmsgimg(long msgId, String type,String path) {
        String uri = folder.getAbsolutePath() + File.separator + "image" +File.separator + path + File.separator;
        File tmp = new File(uri);
        if (!tmp.exists()) {// 判断目录是否存在
            if (!tmp.mkdirs()) {// 创建目标目录
                uri = folder.getAbsolutePath() + File.separator+ "image"+ File.separator;
            }
        }

        XRequest request = XRequest.GET(String.format("https://%s/cgi-bin/mmwebwx-bin/webwxgetmsgimg", host));
        request.query("MsgID", msgId);
        request.query("skey", skey);
        request.query("type", type);
        request.query("pass_ticket", passticket);
        File imgFile = XTools.http(httpExecutor, request).file(uri + String.format("image-%s-%s", String.valueOf(type), msgId));
        try {
            String suffix = WeChatTools.fileSuffix(imgFile);
            if (!XTools.strEmpty(suffix)) {
                File file = XTools.fileToFile(imgFile, uri + String.format("image-%s-%s.%s", String.valueOf(type), msgId, suffix));
                imgFile.delete();
                return file;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imgFile;
    }

    /**
     * 根据MsgId获取那条消息的语音
     *
     * @param msgId 消息ID
     * @return 获取到的语音文件
     */
    File webwxgetvoice(long msgId) {
        XRequest request = XRequest.GET(String.format("https://%s/cgi-bin/mmwebwx-bin/webwxgetvoice", host));
        request.query("msgid", msgId);
        request.query("skey", skey);
        request.query("pass_ticket", passticket);
        return XTools.http(httpExecutor, request).file(folder.getAbsolutePath() + File.separator + String.format("voice-%s.mp3", msgId));
    }

    /**
     * 根据MsgId获取那条消息的视频
     *
     * @param msgId 消息ID
     * @return 获取到的视频文件
     */
    File webwxgetvideo(long msgId) {
        XRequest request = XRequest.GET(String.format("https://%s/cgi-bin/mmwebwx-bin/webwxgetvideo", host));
        request.query("msgid", msgId);
        request.query("skey", this.skey);
        request.query("pass_ticket", this.passticket);
        request.header("Range", "bytes=0-");
        return XTools.http(httpExecutor, request).file(folder.getAbsolutePath() + File.separator + String.format("video-%d.mp4", msgId));
    }

    /**
     * 根据MsgId获取那条消息的附件文件
     *
     * @param msgId    消息ID
     * @param filename 转码后的文件名
     * @param mediaId  文件id
     * @param sender   文件发送者
     * @return 获取到的附件文件
     */
    File webwxgetmedia(long msgId, String filename, String mediaId, String sender) {
        XRequest request = XRequest.GET(String.format("https://file.%s/cgi-bin/mmwebwx-bin/webwxgetmedia", host));
        request.query("encryfilename", filename);
        request.query("fromuser", this.uin);
        request.query("mediaid", mediaId);
        request.query("pass_ticket", this.passticket);
        request.query("sender", sender);
        request.query("webwx_data_ticket", this.dataTicket);
        String suffix = filename.lastIndexOf('.') > 0 ? filename.substring(filename.lastIndexOf('.')) : "";
        return XTools.http(httpExecutor, request).file(folder.getAbsolutePath() + File.separator + String.format("media-%d%s", msgId, suffix));
    }

    /**
     * 发送或同意好友请求，现在发送好友请求功能已经失效
     *
     * @param opCode        操作，2：发送好友申请（已失效），3：同意好友申请
     * @param userName      目标用户的UserName
     * @param verifyTicket  验证票据
     * @param verifyContent 验证消息
     * @return 发送的结果
     */
    RspVerifyUser webwxverifyuser(int opCode, String userName, String verifyTicket, String verifyContent) {
        XRequest request = XRequest.POST(String.format("https://%s/cgi-bin/mmwebwx-bin/webwxverifyuser", host));
        request.query("r", System.currentTimeMillis());
        request.query("pass_ticket", this.passticket);
        request.content(new XRequest.StringContent(XRequest.MIME_JSON, JSONObject.toJSONString(new ReqVerifyUser(new BaseRequest(uin, sid, skey), opCode, userName, verifyTicket, verifyContent))));
        return JSONObject.parseObject(XTools.http(httpExecutor, request).string(), RspVerifyUser.class);
    }

    /**
     * 修改用户备注
     *
     * @param cmdId      指令id
     * @param op         操作码
     * @param userName   目标用户的UserName
     * @param remarkName 备注名称
     * @return 修改备注的结果
     */
    RspOplog webwxoplog(int cmdId, int op, String userName, String remarkName) {
        XRequest request = XRequest.POST(String.format("https://%s/cgi-bin/mmwebwx-bin/webwxoplog", host));
        request.query("pass_ticket", this.passticket);
        request.content(new XRequest.StringContent(XRequest.MIME_JSON, JSONObject.toJSONString(new ReqOplog(new BaseRequest(uin, sid, skey), cmdId, op, userName, remarkName))));
        return JSONObject.parseObject(XTools.http(httpExecutor, request).string(), RspOplog.class);
    }

    /**
     * 添加或移除聊天室成员
     *
     * @param chatroom   聊天室的UserName
     * @param fun        addmember：添加成员，delmember：移除成员
     * @param name       聊天室名称
     * @param memberList 成员列表
     * @return 添加或移除的结果
     */
    RspUpdateChatroom webwxupdatechartroom(String chatroom, String fun, String name, List<String> memberList) {
        XRequest request = XRequest.POST(String.format("https://%s/cgi-bin/mmwebwx-bin/webwxupdatechatroom", host));
        request.query("fun", fun);
        request.query("pass_ticket", this.passticket);
        request.content(new XRequest.StringContent(XRequest.MIME_JSON, JSONObject.toJSONString(new ReqUpdateChatroom(new BaseRequest(uin, sid, skey), chatroom, fun, name, XTools.strJoin(memberList, ",")))));
        return JSONObject.parseObject(XTools.http(httpExecutor, request).string(), RspUpdateChatroom.class);
    }

    /**
     * 文件秒传接口，传输大于25M的文件会先进行检查服务器是否已经存在该文件
     *
     * @param file         需要传输的文件
     * @param fromUserName 消息的发送方UserName
     * @param toUserName   消息的接收方UserName
     * @return 秒传结果，
     */
    RspCheckUpload webwxcheckupload(File file, String fromUserName, String toUserName) {
        XRequest request = XRequest.POST(String.format("https://%s/cgi-bin/mmwebwx-bin/webwxcheckupload", host));
        request.content(new XRequest.StringContent(XRequest.MIME_JSON, JSONObject.toJSONString(new ReqCheckUpload(new BaseRequest(uin, sid, skey), file, fromUserName, toUserName))));
        return JSONObject.parseObject(XTools.http(httpExecutor, request).string(), RspCheckUpload.class);
    }

    /**
     * 上传资源文件，超过1M的文件会被分片上传，每片512K
     *
     * @param fromUserName 消息的发送方UserName
     * @param toUserName   消息的接收方UserName
     * @param file         要上传的资源文件
     * @return 上传结果，包含了MediaId
     * @throws IOException 文件IO异常
     */
    RspUploadMedia webwxuploadmedia(String fromUserName, String toUserName, File file, String aesKey, String signature) throws IOException {
        int fileId = this.file++;
        String fileName = file.getName();
        String fileMime = Files.probeContentType(Paths.get(file.getAbsolutePath()));
        String fileMd5 = XTools.md5(file);
        String fileType = WeChatTools.fileType(file);
        long fileLength = file.length();
        long clientMediaId = ReqUploadMedia.clientMediaId();
        String deviceId = BaseRequest.deviceId();
        if (file.length() < 1024L * 1024L) {
            XRequest request = XRequest.POST(String.format("https://file.%s/cgi-bin/mmwebwx-bin/webwxuploadmedia", host));
            request.query("f", "json");
            request.content("id", String.format("WU_FILE_%d", fileId));
            request.content("name", fileName);
            request.content("type", fileMime);
            request.content("lastModifiedDate", new Date(file.lastModified()));
            request.content("size", fileLength);
            request.content("mediatype", fileType);
            request.content("uploadmediarequest", JSONObject.toJSONString(new ReqUploadMedia(new BaseRequest(uin, sid, skey, deviceId), clientMediaId, 2, fileLength, 0, fileLength, fileMd5, aesKey, signature, fromUserName, toUserName)));
            request.content("webwx_data_ticket", dataTicket);
            request.content("pass_ticket", XTools.strEmpty(passticket) ? "undefined" : passticket);
            request.content("filename", file);
            return JSONObject.parseObject(XTools.http(httpExecutor, request).string(), RspUploadMedia.class);
        } else {
            RspUploadMedia rspUploadMedia = null;
            byte[] sliceBuffer = new byte[512 * 1024];
            try (BufferedInputStream bfinStream = new BufferedInputStream(new FileInputStream(file))) {
                for (long sliceIndex = 0, sliceCount = (long) Math.ceil(file.length() / 512D / 1024D); sliceIndex < sliceCount; sliceIndex++) {
                    XRequest request = XRequest.POST(String.format("https://file.%s/cgi-bin/mmwebwx-bin/webwxuploadmedia", host));
                    request.query("f", "json");
                    request.content("id", String.format("WU_FILE_%d", fileId));
                    request.content("name", fileName);
                    request.content("type", fileMime);
                    request.content("lastModifiedDate", new Date(file.lastModified()));
                    request.content("size", fileLength);
                    request.content("chunks", sliceCount);
                    request.content("chunk", sliceIndex);
                    request.content("mediatype", fileType);
                    request.content("uploadmediarequest", JSONObject.toJSONString(new ReqUploadMedia(new BaseRequest(uin, sid, skey, deviceId), clientMediaId, 2, fileLength, 0, fileLength, fileMd5, aesKey, signature, fromUserName, toUserName)));
                    request.content("webwx_data_ticket", dataTicket);
                    request.content("pass_ticket", XTools.strEmpty(passticket) ? "undefined" : passticket);
                    int readCount;
                    WeChatTools.Slice slice = new WeChatTools.Slice("filename", fileName, fileMime, sliceBuffer, 0);
                    while ((readCount = bfinStream.read(sliceBuffer, slice.count, sliceBuffer.length - slice.count)) > 0) {
                        slice.count += readCount;
                        if (slice.count >= sliceBuffer.length) {
                            break;
                        }
                    }
                    request.content("filename", slice);
                    rspUploadMedia = JSONObject.parseObject(XTools.http(httpExecutor, request).string(), RspUploadMedia.class);
                }
            }
            return rspUploadMedia;
        }
    }

    /**
     * 退出登录接口
     */
    void webwxlogout() {
        XRequest request = XRequest.POST(String.format("https://%s/cgi-bin/mmwebwx-bin/webwxlogout", host));
        request.query("redirect", 1);
        request.query("type", 0);
        request.query("skey", this.skey);
        request.content("sid", this.sid);
        request.content("uin", this.uin);
        XTools.http(httpExecutor, request).string();
    }
}