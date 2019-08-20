package com.ytx.wechat.client;


import com.sun.istack.internal.Nullable;
import com.ytx.wechat.entity.contact.WXContact;
import com.ytx.wechat.entity.contact.WXGroup;
import com.ytx.wechat.entity.contact.WXUser;
import com.ytx.wechat.entity.message.*;
import com.ytx.wechat.protocol.*;
import lombok.extern.slf4j.Slf4j;
import me.xuxiaoxiao.xtools.common.XTools;
import me.xuxiaoxiao.xtools.common.http.XHttpTools;
import me.xuxiaoxiao.xtools.common.http.executor.impl.XRequest;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.net.HttpCookie;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 模拟网页微信客户端
 */
@Slf4j
public final class WeChatClient {
    public static final String CFG_PREFIX = "me.xuxiaoxiao$chatapi-wechat$";

    public static final String LOGIN_TIMEOUT = "登陆超时";

    public static final int STATUS_EXCEPTION = -1;
    public static final int STATUS_READY = 0;
    public static final int STATUS_SCAN = 1;
    public static final int STATUS_PERMIT = 2;
    public static final int STATUS_WORKING = 3;
    public static final int STATUS_LOGOUT = 4;

    private static final Pattern REX_GROUPMSG = Pattern.compile("(@[0-9a-zA-Z]+):<br/>([\\s\\S]*)");
    private static final Pattern REX_REVOKE_ID = Pattern.compile("&lt;msgid&gt;(\\d+)&lt;/msgid&gt;");
    private static final Pattern REX_REVOKE_REPLACE = Pattern.compile("&lt;replacemsg&gt;&lt;!\\[CDATA\\[([\\s\\S]*)]]&gt;&lt;/replacemsg&gt;");

    private final WeChatClient.WeChatThread wxThread = new WeChatClient.WeChatThread();
    private final WeChatContacts wxContacts = new WeChatContacts();
    private final WeChatApi wxAPI = new WeChatApi();
    private volatile WeChatClient.WeChatListener wxListener;
    private volatile int status = STATUS_READY;

    /**
     * 处理监听器，二维码事件
     *
     * @param qrcode 二维码地址
     */
    private void handleQRCode(@Nonnull String qrcode) {
        this.status = STATUS_SCAN;
        WeChatClient.WeChatListener listener = this.wxListener;
        if (listener != null) {
            listener.onQRCode(this, qrcode);
        }
    }

    /**
     * 处理监听器，头像事件
     *
     * @param base64Avatar base64编码头像
     */
    private void handleAvatar(@Nonnull String base64Avatar) {
        this.status = STATUS_PERMIT;
        WeChatClient.WeChatListener listener = this.wxListener;
        if (listener != null) {
            listener.onAvatar(this, base64Avatar);
        }
    }

    /**
     * 处理监听器，异常事件
     *
     * @param reason 异常信息
     */
    private void handleFailure(@Nonnull String reason) {
        this.status = STATUS_EXCEPTION;
        WeChatClient.WeChatListener listener = this.wxListener;
        if (listener != null) {
            listener.onFailure(this, reason);
        }
    }

    /**
     * 处理监听器，登录完成事件
     */
    private void handleLogin() {
        this.status = STATUS_WORKING;
        WeChatClient.WeChatListener listener = this.wxListener;
        if (listener != null) {
            listener.onLogin(this);
        }
    }

    /**
     * 处理监听器，新消息事件
     *
     * @param message 微信消息
     */
    private void handleMessage(WXMessage message) {
        this.status = STATUS_WORKING;
        WeChatClient.WeChatListener listener = this.wxListener;
        if (listener != null) {
            listener.onMessage(this, message);
        }
    }

    /**
     * 处理监听器，联系人变动事件
     *
     * @param oldContact 旧联系人，新增联系人时为null
     * @param newContact 新联系人，删除联系人时为null
     */
    private void handleContact(WXContact oldContact, WXContact newContact) {
        this.status = STATUS_WORKING;
        WeChatClient.WeChatListener listener = this.wxListener;
        if (listener != null) {
            listener.onContact(this, oldContact, newContact);
        }
    }

    /**
     * 处理监听器，退出登录事件
     */
    private void handleLogout() {
        this.status = STATUS_LOGOUT;
        WeChatClient.WeChatListener listener = this.wxListener;
        if (listener != null) {
            listener.onLogout(this);
        }
    }

    /**
     * 获取并保存不限数量和类型的联系人信息
     *
     * @param userNames 逗号分隔的联系人userName
     */
    private void loadContacts(@Nonnull String userNames, boolean useCache) {
        if (StringUtils.isNotEmpty(userNames)) {
            LinkedList<ReqBatchGetContact.Contact> contacts = new LinkedList<>();
            for (String userName : userNames.split(",")) {
                if (StringUtils.isNotEmpty(userName)) {
                    contacts.add(new ReqBatchGetContact.Contact(userName, ""));
                }
            }
            loadContacts(contacts, useCache);
        }
    }

    /**
     * 获取并保存不限数量和类型的联系人信息
     *
     * @param contacts 要获取的联系人的列表，数量和类型不限
     */
    private void loadContacts(@Nonnull List<ReqBatchGetContact.Contact> contacts, boolean useCache) {
        if (useCache) {
            Iterator<ReqBatchGetContact.Contact> iterator = contacts.iterator();
            while (iterator.hasNext()) {
                ReqBatchGetContact.Contact contact = iterator.next();
                if (!contact.UserName.startsWith("@@") && wxContacts.getContact(contact.UserName) != null) {
                    //不是群聊，并且已经获取过，就不再次获取
                    iterator.remove();
                }
            }
        }
        //拆分成每次50个联系人分批获取
        if (contacts.size() > 50) {
            LinkedList<ReqBatchGetContact.Contact> temp = new LinkedList<>();
            for (ReqBatchGetContact.Contact contact : contacts) {
                temp.add(contact);
                if (temp.size() >= 50) {
                    RspBatchGetContact rspBatchGetContact = wxAPI.webwxbatchgetcontact(contacts);
                    for (RspInit.User user : rspBatchGetContact.ContactList) {
                        wxContacts.putContact(wxAPI.host, user);
                    }
                    temp.clear();
                }
            }
            contacts = temp;
        }
        if (contacts.size() > 0) {
            RspBatchGetContact rspBatchGetContact = wxAPI.webwxbatchgetcontact(contacts);
            for (RspInit.User user : rspBatchGetContact.ContactList) {
                wxContacts.putContact(wxAPI.host, user);
            }
        }
    }

    /**
     * 设置客户端的监听器
     *
     * @param listener 监听器对象
     */
    public void setListener(@Nonnull WeChatClient.WeChatListener listener) {
        this.wxListener = listener;
    }

    /**
     * 获取客户端的监听器
     *
     * @return 监听器对象
     */
    @Nullable
    public WeChatClient.WeChatListener getListener() {
        return this.wxListener;
    }

    /**
     * 启动客户端，注意：一个客户端类的实例只能被启动一次
     */
    public void start() {
        wxThread.start();
    }

    /**
     * 获取客户端的状态
     *
     * @return 客户端的当前状态
     */
    public int status() {
        return this.status;
    }

    /**
     * 关闭客户端，注意：关闭后的客户端不能再被启动
     */
    public void shutdown() {
        wxAPI.webwxlogout();
        wxThread.interrupt();
    }

    /**
     * 获取当前登录的用户信息
     *
     * @return 当前登录的用户信息
     */
    public WXUser userMe() {
        return wxContacts.getMe();
    }

    /**
     * 根据userId获取用户好友
     *
     * @param userId 好友的id
     * @return 好友的信息
     */
    @Nullable
    public WXUser userFriend(@Nonnull String userId) {
        return wxContacts.getFriend(userId);
    }

    /**
     * 获取用户所有好友
     *
     * @return 用户所有好友
     */
    @Nonnull
    public HashMap<String, WXUser> userFriends() {
        return wxContacts.getFriends();
    }

    /**
     * 根据群id获取群信息
     *
     * @param groupId 群id
     * @return 群信息
     */
    @Nullable
    public WXGroup userGroup(@Nonnull String groupId) {
        return wxContacts.getGroup(groupId);
    }

    /**
     * 获取用户所有群
     *
     * @return 用户所有群
     */
    @Nonnull
    public HashMap<String, WXGroup> userGroups() {
        return wxContacts.getGroups();
    }

    /**
     * 根据联系人id获取用户联系人信息
     *
     * @param contactId 联系人id
     * @return 联系人信息
     */
    @Nullable
    public WXContact userContact(@Nonnull String contactId) {
        return wxContacts.getContact(contactId);
    }

    /**
     * 发送文字消息
     *
     * @param wxContact 目标联系人
     * @param text      要发送的文字
     * @return 文本消息
     */
    @Nonnull
    public WXText sendText(@Nonnull WXContact wxContact, @Nonnull String text) {
        log.info( "向{}发送消息：{}", wxContact.id, text);
        RspSendMsg rspSendMsg = wxAPI.webwxsendmsg(new ReqSendMsg.Msg(RspSync.AddMsg.TYPE_TEXT, null, 0, text, null, wxContacts.getMe().id, wxContact.id));

        WXText wxText = new WXText();
        wxText.id = Long.valueOf(rspSendMsg.MsgID);
        wxText.idLocal = Long.valueOf(rspSendMsg.LocalID);
        wxText.timestamp = System.currentTimeMillis();
        wxText.fromGroup = null;
        wxText.fromUser = wxContacts.getMe();
        wxText.toContact = wxContact;
        wxText.content = text;
        return wxText;
    }

    /**
     * 发送文件消息，可以是图片，动图，视频，文本等文件
     *
     * @param wxContact 目标联系人
     * @param file      要发送的文件
     * @return 图像或附件消息
     */
    @Nullable
    public WXMessage sendFile(@Nonnull WXContact wxContact, @Nonnull File file) {
        String suffix = WeChatTools.fileSuffix(file);
        if ("mp4".equals(suffix) && file.length() >= 20L * 1024L * 1024L) {
            log.warn("向{}发送的视频文件大于20M，无法发送", wxContact.id);
            return null;
        } else {
            try {
                log.info( "向{}发送文件：{}", wxContact.id, file.getAbsolutePath());
                String mediaId = null, aesKey = null, signature = null;
                if (file.length() >= 25L * 1024L * 1024L) {
                    RspCheckUpload rspCheckUpload = wxAPI.webwxcheckupload(file, wxContacts.getMe().id, wxContact.id);
                    mediaId = rspCheckUpload.MediaId;
                    aesKey = rspCheckUpload.AESKey;
                    signature = rspCheckUpload.Signature;
                }
                if (StringUtils.isEmpty(mediaId)) {
                    RspUploadMedia rspUploadMedia = wxAPI.webwxuploadmedia(wxContacts.getMe().id, wxContact.id, file, aesKey, signature);
                    mediaId = rspUploadMedia.MediaId;
                }

                if (StringUtils.isNotEmpty(mediaId)) {
                    switch (WeChatTools.fileType(file)) {
                        case "pic": {
                            RspSendMsg rspSendMsg = wxAPI.webwxsendmsgimg(new ReqSendMsg.Msg(RspSync.AddMsg.TYPE_IMAGE, mediaId, null, "", signature, wxContacts.getMe().id, wxContact.id));
                            WXImage wxImage = new WXImage();
                            wxImage.id = Long.valueOf(rspSendMsg.MsgID);
                            wxImage.idLocal = Long.valueOf(rspSendMsg.LocalID);
                            wxImage.timestamp = System.currentTimeMillis();
                            wxImage.fromGroup = null;
                            wxImage.fromUser = wxContacts.getMe();
                            wxImage.toContact = wxContact;
                            wxImage.imgWidth = 0;
                            wxImage.imgHeight = 0;
                            wxImage.image = wxAPI.webwxgetmsgimg(wxImage.id, "slave","image");
                            wxImage.origin = file;
                            return wxImage;
                        }
                        case "video": {
                            RspSendMsg rspSendMsg = wxAPI.webwxsendvideomsg(new ReqSendMsg.Msg(RspSync.AddMsg.TYPE_VIDEO, mediaId, null, "", signature, wxContacts.getMe().id, wxContact.id));
                            WXVideo wxVideo = new WXVideo();
                            wxVideo.id = Long.valueOf(rspSendMsg.MsgID);
                            wxVideo.idLocal = Long.valueOf(rspSendMsg.LocalID);
                            wxVideo.timestamp = System.currentTimeMillis();
                            wxVideo.fromGroup = null;
                            wxVideo.fromUser = wxContacts.getMe();
                            wxVideo.toContact = wxContact;
                            wxVideo.imgWidth = 0;
                            wxVideo.imgHeight = 0;
                            wxVideo.image = wxAPI.webwxgetmsgimg(wxVideo.id, "slave","video");
                            wxVideo.videoLength = 0;
                            wxVideo.video = file;
                            return wxVideo;
                        }
                        default:
                            if ("gif".equals(suffix)) {
                                RspSendMsg rspSendMsg = wxAPI.webwxsendemoticon(new ReqSendMsg.Msg(RspSync.AddMsg.TYPE_EMOJI, mediaId, 2, "", signature, wxContacts.getMe().id, wxContact.id));
                                WXImage wxImage = new WXImage();
                                wxImage.id = Long.valueOf(rspSendMsg.MsgID);
                                wxImage.idLocal = Long.valueOf(rspSendMsg.LocalID);
                                wxImage.timestamp = System.currentTimeMillis();
                                wxImage.fromGroup = null;
                                wxImage.fromUser = wxContacts.getMe();
                                wxImage.toContact = wxContact;
                                wxImage.imgWidth = 0;
                                wxImage.imgHeight = 0;
                                wxImage.image = file;
                                wxImage.origin = file;
                                return wxImage;
                            } else {
                                StringBuilder sbAppMsg = new StringBuilder();
                                sbAppMsg.append("<appmsg appid='wxeb7ec651dd0aefa9' sdkver=''>");
                                sbAppMsg.append("<title>").append(file.getName()).append("</title>");
                                sbAppMsg.append("<des></des>");
                                sbAppMsg.append("<action></action>");
                                sbAppMsg.append("<type>6</type>");
                                sbAppMsg.append("<content></content>");
                                sbAppMsg.append("<url></url>");
                                sbAppMsg.append("<lowurl></lowurl>");
                                sbAppMsg.append("<appattach>");
                                sbAppMsg.append("<totallen>").append(file.length()).append("</totallen>");
                                sbAppMsg.append("<attachid>").append(mediaId).append("</attachid>");
                                sbAppMsg.append("<fileext>").append(StringUtils.isEmpty(suffix) ? "undefined" : suffix).append("</fileext>");
                                sbAppMsg.append("</appattach>");
                                sbAppMsg.append("<extinfo></extinfo>");
                                sbAppMsg.append("</appmsg>");
                                RspSendMsg rspSendMsg = wxAPI.webwxsendappmsg(new ReqSendMsg.Msg(6, null, null, sbAppMsg.toString(), signature, wxContacts.getMe().id, wxContact.id));
                                WXFile wxFile = new WXFile();
                                wxFile.id = Long.valueOf(rspSendMsg.MsgID);
                                wxFile.idLocal = Long.valueOf(rspSendMsg.LocalID);
                                wxFile.timestamp = System.currentTimeMillis();
                                wxFile.fromGroup = null;
                                wxFile.fromUser = wxContacts.getMe();
                                wxFile.toContact = wxContact;
                                wxFile.content = sbAppMsg.toString();
                                wxFile.fileSize = file.length();
                                wxFile.fileName = file.getName();
                                wxFile.fileId = mediaId;
                                wxFile.file = file;
                                return wxFile;
                            }
                    }
                } else {
                    log.error("向{}发送的文件发送失败", wxContact.id);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 发送位置消息
     * <p>
     * 经纬度坐标可以通过腾讯坐标拾取工具获得(https://lbs.qq.com/tool/getpoint)
     * 其拾取的坐标默认格式为 lat,lon
     * </p>
     *
     * @param wxContact 目标联系人
     * @param lon       经度
     * @param lat       纬度
     * @param title     定位消息模块标题
     * @param lable     定位消息模块二级描述
     * @return 定位消息
     */
    @Nonnull
    public WXLocation sendLocation(@Nonnull WXContact wxContact, @Nonnull String lon, @Nonnull String lat, @Nonnull String title, @Nonnull String lable) {
        log.info( "向{}发送位置信息，坐标：{},{}，说明：{}({})", wxContact.id, lon, lat, title, lable);
        StringBuilder sbLocationMsg = new StringBuilder();
        sbLocationMsg.append("<?xml version=\"1.0\"?>\n");
        sbLocationMsg.append("<msg>\n");
        sbLocationMsg.append("<location x=\"" + lat + "\" y=\"" + lon + "\" scale=\"15\" label=\"" + lable + "\" maptype=\"roadmap\" poiname=\"" + title + "\" poiid=\"City\" />\n");
        sbLocationMsg.append("</msg>\n");
        RspSendMsg rspSendMsg = wxAPI.webwxsendmsg(new ReqSendMsg.Msg(RspSync.AddMsg.TYPE_LOCATION, null, 0, sbLocationMsg.toString(), null, wxContacts.getMe().id, wxContact.id));
        WXLocation wxLocation = new WXLocation();
        wxLocation.id = Long.valueOf(rspSendMsg.MsgID);
        wxLocation.idLocal = Long.valueOf(rspSendMsg.LocalID);
        wxLocation.timestamp = System.currentTimeMillis();
        wxLocation.fromGroup = null;
        wxLocation.fromUser = wxContacts.getMe();
        wxLocation.toContact = wxContact;
        wxLocation.content = sbLocationMsg.toString();
        return wxLocation;
    }

    /**
     * 获取用户联系人，如果获取的联系人是群组，则会自动获取群成员的详细信息
     * <strong>在联系人列表中获取到的群，没有群成员，可以通过这个方法，获取群的详细信息</strong>
     *
     * @param contactId 联系人id
     * @return 联系人的详细信息
     */
    @Nullable
    public WXContact fetchContact(@Nonnull String contactId) {
        loadContacts(contactId, false);
        WXContact contact = wxContacts.getContact(contactId);
        if (contact instanceof WXGroup) {
            List<ReqBatchGetContact.Contact> contacts = new LinkedList<>();
            for (WXGroup.Member member : ((WXGroup) contact).members.values()) {
                contacts.add(new ReqBatchGetContact.Contact(member.id, contact.id));
            }
            loadContacts(contacts, true);
            ((WXGroup) contact).isDetail = true;
        }
        return contact;
    }

    /**
     * 获取用户头像
     *
     * @param wxContact 要获取头像文件的用户
     * @return 获取头像文件后的用户
     */
    @Nonnull
    public WXContact fetchAvatar(@Nonnull WXContact wxContact) {
        wxContact.avatarFile = XTools.http(XHttpTools.EXECUTOR, XRequest.GET(wxContact.avatarUrl)).file(wxAPI.folder.getAbsolutePath() + File.separator + String.format("avatar-%d.jpg", System.currentTimeMillis() + new Random().nextInt(1000)));
        return wxContact;
    }

    /**
     * 获取图片消息的大图
     *
     * @param wxImage 要获取大图的图片消息
     * @return 获取大图后的图片消息
     */
    @Nonnull
    public WXImage fetchImage(@Nonnull WXImage wxImage) {
        String path = "image";
        if(wxImage.fromGroup !=null){
            path = wxImage.fromGroup.name.replaceAll("<[^>]+>", "") + File.separator + path ;
        }
        wxImage.origin = wxAPI.webwxgetmsgimg(wxImage.id, "big",path);
        return wxImage;
    }

    /**
     * 获取语音消息的语音文件
     *
     * @param wxVoice 语音消息
     * @return 获取语音文件后的语音消息
     */
    @Nonnull
    public WXVoice fetchVoice(@Nonnull WXVoice wxVoice) {
        wxVoice.voice = wxAPI.webwxgetvoice(wxVoice.id);
        return wxVoice;
    }

    /**
     * 获取视频消息的视频文件
     *
     * @param wxVideo 视频消息
     * @return 获取视频文件后的视频消息
     */
    @Nonnull
    public WXVideo fetchVideo(@Nonnull WXVideo wxVideo) {
        wxVideo.video = wxAPI.webwxgetvideo(wxVideo.id);
        return wxVideo;
    }

    /**
     * 获取文件消息的附件文件
     *
     * @param wxFile 文件消息
     * @return 获取附件文件后的文件消息
     */
    @Nonnull
    public WXFile fetchFile(@Nonnull WXFile wxFile) {
        wxFile.file = wxAPI.webwxgetmedia(wxFile.id, wxFile.fileName, wxFile.fileId, wxFile.fromUser.id);
        return wxFile;
    }

    /**
     * 撤回消息
     *
     * @param wxMessage 需要撤回的微信消息
     */
    public void revokeMsg(@Nonnull WXMessage wxMessage) {
        log.info("撤回向{}发送的消息：{}，{}", wxMessage.toContact.id, wxMessage.idLocal, wxMessage.id);
        wxAPI.webwxrevokemsg(wxMessage.idLocal, wxMessage.id, wxMessage.toContact.id);
    }

    /**
     * 同意好友申请
     *
     * @param wxVerify 好友验证消息
     */
    public void passVerify(@Nonnull WXVerify wxVerify) {
        log.info( "通过好友{}的申请", wxVerify.userName);
        wxAPI.webwxverifyuser(3, wxVerify.userId, wxVerify.ticket, "");
    }

    /**
     * 修改用户备注名
     *
     * @param wxUser 目标用户
     * @param remark 备注名称
     */
    public void editRemark(@Nonnull WXUser wxUser, @Nonnull String remark) {
        log.info( "修改{}的备注：{}", wxUser.id, remark);
        wxAPI.webwxoplog(ReqOplog.CMD_REMARK, ReqOplog.OP_NONE, wxUser.id, remark);
    }

    /**
     * 设置联系人置顶状态
     *
     * @param wxContact 需要设置置顶状态的联系人
     * @param isTop     是否置顶
     */
    public void topContact(@Nonnull WXContact wxContact, boolean isTop) {
        log.info("设置{}的置顶状态：{}", wxContact.id, isTop);
        wxAPI.webwxoplog(ReqOplog.CMD_TOP, isTop ? ReqOplog.OP_TOP_TRUE : ReqOplog.OP_TOP_FALSE, wxContact.id, null);
    }

    /**
     * 修改聊天室名称
     *
     * @param wxGroup 目标聊天室
     * @param name    目标名称
     */
    public void setGroupName(@Nonnull WXGroup wxGroup, @Nonnull String name) {
        log.info( "为群{}修改名称：{}", wxGroup.id, name);
        wxAPI.webwxupdatechartroom(wxGroup.id, "modtopic", name, new LinkedList<String>());
    }

    /**
     * 模拟网页微信客户端监听器
     */
    public static abstract class WeChatListener {
        /**
         * 获取到用户登录的二维码
         *
         * @param qrCode 用户登录二维码的url
         */
        public abstract void onQRCode(@Nonnull WeChatClient client, @Nonnull String qrCode);

        /**
         * 获取用户头像，base64编码
         *
         * @param base64Avatar base64编码的用户头像
         */
        public void onAvatar(@Nonnull WeChatClient client, @Nonnull String base64Avatar) {
        }

        /**
         * 模拟网页微信客户端异常退出
         *
         * @param reason 错误原因
         */
        public void onFailure(@Nonnull WeChatClient client, @Nonnull String reason) {
        }

        /**
         * 用户登录并初始化成功
         */
        public void onLogin(@Nonnull WeChatClient client) {
        }

        /**
         * 用户获取到消息
         *
         * @param message 用户获取到的消息
         */
        public void onMessage(@Nonnull WeChatClient client, @Nonnull WXMessage message) {
        }

        /**
         * 用户联系人变化
         *
         * @param client     微信客户端
         * @param oldContact 旧联系人，新增联系人时为null
         * @param newContact 新联系人，删除联系人时为null
         */
        public void onContact(@Nonnull WeChatClient client, @Nullable WXContact oldContact, @Nullable WXContact newContact) {
        }

        /**
         * 模拟网页微信客户端正常退出
         */
        public void onLogout(@Nonnull WeChatClient client) {
        }
    }

    /**
     * 模拟网页微信客户端工作线程
     */
    private class WeChatThread extends Thread {

        @Override
        public void run() {
            int loginCount = 0;
            while (!isInterrupted()) {

                //用户登录
                log.info("正在登录");
                String loginErr = login();
                if (StringUtils.isNotEmpty(loginErr)) {
                    handleFailure(loginErr);
                    //退出登录
                    log.info("正在退出登录");
                    handleLogout();
                    return;
                }else{
                    //用户初始化
                    log.info("正在初始化");
                    String initErr = initial();
                    if (StringUtils.isNotEmpty(initErr)) {
//                    log.error("初始化出现错误：{}", initErr);
                        handleFailure(initErr);
                        return;
                    }

                    handleLogin();

                    //同步消息
                    log.info("开始监听信息");
                    String listenErr = listen();
                    if (StringUtils.isNotEmpty(listenErr)) {
                        if (loginCount++ > 10) {
                            handleFailure(listenErr);
                            //退出登录
                            log.info("正在退出登录");
                            handleLogout();
                            return;
                        } else {
                            continue;
                        }
                    }
                }
            }
        }

        /**
         * 用户登录
         *
         * @return 登录时异常原因，为null表示正常登录
         */
        @Nullable
        private String login() {
            try {
                if (StringUtils.isEmpty(wxAPI.sid)) {
                    String qrCode = wxAPI.jslogin();
                    log.info("等待扫描二维码：{}", qrCode);
                    handleQRCode(qrCode);
                    while (true) {
                        RspLogin rspLogin = wxAPI.login();
                        switch (rspLogin.code) {
                            case 200:
                                log.info("已授权登录");
                                wxAPI.webwxnewloginpage(rspLogin.redirectUri);
                                return null;
                            case 201:
                                log.info("已扫描二维码");
                                handleAvatar(rspLogin.userAvatar);
                                break;
                            case 408:
                                log.info("等待授权登录");
                                break;
                            default:
                                log.error("登录超时");
                                return LOGIN_TIMEOUT;
                        }
                    }
                } else {
                    return null;
                }
            } catch (Exception e) {
                log.error("登录异常:{}",e.getMessage());
                return e.getMessage();
            }
        }

        /**
         * 初始化
         *
         * @return 初始化异常原因，为null表示正常初始化
         */
        @Nullable
        private String initial() {
            try {
                //通过Cookie获取重要参数
                log.info("正在获取Cookie");
                for (HttpCookie cookie : XHttpTools.EXECUTOR.getCookies()) {
                    if ("wxsid".equalsIgnoreCase(cookie.getName())) {
                        wxAPI.sid = cookie.getValue();
                    } else if ("wxuin".equalsIgnoreCase(cookie.getName())) {
                        wxAPI.uin = cookie.getValue();
                    } else if ("webwx_data_ticket".equalsIgnoreCase(cookie.getName())) {
                        wxAPI.dataTicket = cookie.getValue();
                    }
                }

                //获取自身信息
                log.info("正在获取自身信息");
                RspInit rspInit = wxAPI.webwxinit();
                wxContacts.setMe(wxAPI.host, rspInit.User);

                //获取并保存最近联系人
                log.info("正在获取并保存最近联系人");
                loadContacts(rspInit.ChatSet, true);

                //发送初始化状态信息
                wxAPI.webwxstatusnotify(wxContacts.getMe().id, WXNotify.NOTIFY_INITED);

                //获取好友、保存的群聊、公众号列表。
                //这里获取的群没有群成员，不过也不能用fetchContact方法暴力获取群成员，因为这样数据量会很大
                log.info("正在获取好友、群、公众号列表");
                RspGetContact rspGetContact = wxAPI.webwxgetcontact();
                for (RspInit.User user : rspGetContact.MemberList) {
                    wxContacts.putContact(wxAPI.host, user);
                }
                return null;
            } catch (Exception e) {
                e.printStackTrace();
                log.error("初始化异常:{}", e.getMessage());
                return e.getMessage();
            }
        }

        /**
         * 循环同步消息
         *
         * @return 同步消息的异常原因，为null表示正常结束
         */
        @Nullable
        private String listen() {
            int retryCount = 0;
            try {
                while (!isInterrupted()) {
                    RspSyncCheck rspSyncCheck;
                    try {
                        rspSyncCheck = wxAPI.synccheck();
                    } catch (Exception e) {
                        if (retryCount++ < 5) {
                           log.error("监听异常，重试第{}次,异常原因：{}", retryCount,e.getMessage());
                            continue;
                        } else {
                            log.error("监听异常，重试次数过多,异常原因：{}",e.getMessage());
                            return e.getMessage();
                        }
                    }
                    retryCount = 0;
                    if (rspSyncCheck.retcode > 0) {
                        log.info("停止监听信息：{}", rspSyncCheck.retcode);
                        return null;
                    } else if (rspSyncCheck.selector > 0) {
                        RspSync rspSync = wxAPI.webwxsync();
                        if (rspSync.DelContactList != null && !rspSync.DelContactList.isEmpty()) {
                            //删除好友立刻触发
                            //删除群后的任意一条消息触发
                            //被移出群不会触发（会收到一条被移出群的addMsg）
                            for (RspInit.User user : rspSync.DelContactList) {
                                WXContact oldContact = wxContacts.getContact(user.UserName);
                                if(oldContact instanceof WXUser){
                                    wxContacts.rmvContact(user.UserName);
                                    if (oldContact != null && StringUtils.isNotEmpty(oldContact.name)) {
                                        handleContact(oldContact, null);
                                    }
                                }else{
                                    wxContacts.putContact(wxAPI.host, user);
                                    WXContact newContact = wxContacts.getContact(user.UserName);
                                    handleContact(oldContact, newContact);
                                }
                            }
                        }
                        if (rspSync.ModContactList != null && !rspSync.ModContactList.isEmpty()) {
                            //添加好友立刻触发
                            //被拉入已存在的群立刻触发
                            //被拉入新群第一条消息触发（同时收到2条addMsg，一条被拉入群，一条聊天消息）
                            //群里有人加入或群里踢人或修改群信息之后第一条信息触发
                            for (RspInit.User user : rspSync.ModContactList) {
                                //由于在这里获取到的联系人（无论是群还是用户）的信息是不全的，所以使用接口重新获取
                                WXContact oldContact = wxContacts.getContact(user.UserName);
                                wxContacts.rmvContact(user.UserName);
                                if (oldContact != null && StringUtils.isEmpty(oldContact.name)) {
                                    oldContact = null;
                                }
                                wxContacts.putContact(wxAPI.host, user);
                                WXContact newContact = wxContacts.getContact(user.UserName);
                                if (newContact != null && StringUtils.isEmpty(newContact.name)) {
                                    wxContacts.rmvContact(user.UserName);
                                    newContact = null;
                                }
                                handleContact(oldContact, newContact);
                            }
                        }
                        if (rspSync.AddMsgList != null && !rspSync.AddMsgList.isEmpty()) {
                            for (RspSync.AddMsg addMsg : rspSync.AddMsgList) {
                                //接收到的消息，文字、图片、语音、地理位置等等
                                WXMessage wxMessage = parseMessage(addMsg);
                                if (wxMessage instanceof WXNotify) {
                                    //状态更新消息，需要处理后再交给监听器
                                    WXNotify wxNotify = (WXNotify) wxMessage;
                                    if (wxNotify.notifyCode == WXNotify.NOTIFY_SYNC_CONV) {
                                        //会话同步，网页微信仅仅只获取了相关联系人详情
                                        loadContacts(wxNotify.notifyContact, false);
                                    }
                                }
                                //最后交给监听器处理
                                handleMessage(wxMessage);
                            }
                        }
                    }
                }
                return null;
            } catch (Exception e) {
                log.error( "监听消息异常:{}",e);
                return e.getMessage();
            }
        }

        @Nonnull
        private <T extends WXMessage> T parseCommon(@Nonnull RspSync.AddMsg msg, @Nonnull T message) {
            message.id = msg.MsgId;
            message.idLocal = msg.MsgId;
            message.timestamp = msg.CreateTime * 1000;
            if (msg.FromUserName.startsWith("@@")) {
                //是群消息
                message.fromGroup = (WXGroup) wxContacts.getContact(msg.FromUserName);
                if (message.fromGroup == null || !message.fromGroup.isDetail || message.fromGroup.members.isEmpty()) {
                    if(!(message instanceof WXSystem)){
                        //如果群不存在，或者是未获取成员的群。获取并保存群的详细信息
                        message.fromGroup = (WXGroup) fetchContact(msg.FromUserName);
                    }
                }
                Matcher mGroupMsg = REX_GROUPMSG.matcher(msg.Content);
                if (mGroupMsg.matches()) {
                    //是群成员发送的消息
                    message.fromUser = (WXUser) wxContacts.getContact(mGroupMsg.group(1));
                    if (message.fromUser == null) {
                        //未获取成员。首先获取并保存群的详细信息，然后获取群成员信息
                        fetchContact(msg.FromUserName);
                        message.fromUser = (WXUser) wxContacts.getContact(mGroupMsg.group(1));
                    }
                    message.toContact = wxContacts.getContact(msg.ToUserName);
                    if (message.toContact == null) {
                        message.toContact = fetchContact(msg.ToUserName);
                    }
                    message.content = mGroupMsg.group(2);
                } else {
                    //不是群成员发送的消息
                    message.fromUser = null;
                    message.toContact = wxContacts.getContact(msg.ToUserName);
                    if (message.toContact == null) {
                        message.toContact = fetchContact(msg.ToUserName);
                    }
                    message.content = msg.Content;
                }
            } else {
                //不是群消息
                message.fromGroup = null;
                message.fromUser = (WXUser) wxContacts.getContact(msg.FromUserName);
                if (message.fromUser == null) {
                    //联系人不存在（一般不会出现这种情况），手动获取联系人
                    message.fromUser = (WXUser) fetchContact(msg.FromUserName);
                }
                message.toContact = wxContacts.getContact(msg.ToUserName);
                if (message.toContact == null) {
                    message.toContact = fetchContact(msg.ToUserName);
                }
                message.content = msg.Content;
            }
            return message;
        }

        @Nonnull
        private WXMessage parseMessage(@Nonnull RspSync.AddMsg msg) {
            try {
                switch (msg.MsgType) {
                    case RspSync.AddMsg.TYPE_TEXT: {
                        if (msg.SubMsgType == 0) {
                            return parseCommon(msg, new WXText());
                        } else if (msg.SubMsgType == 48) {
                            WXLocation wxLocation = parseCommon(msg, new WXLocation());
                            wxLocation.locationName = wxLocation.content.substring(0, wxLocation.content.indexOf(':'));
                            wxLocation.locationImage = String.format("https://%s%s", wxAPI.host, wxLocation.content.substring(wxLocation.content.indexOf(':') + ":<br/>".length()));
                            wxLocation.locationUrl = msg.Url;
                            return wxLocation;
                        }
                        break;
                    }
                    case RspSync.AddMsg.TYPE_IMAGE: {
                        WXImage wxImage = parseCommon(msg, new WXImage());
                        wxImage.imgWidth = msg.ImgWidth;
                        wxImage.imgHeight = msg.ImgHeight;
                        String path = "image";
                        if(wxImage.fromGroup !=null){
                            path = wxImage.fromGroup.name.replaceAll("<[^>]+>", "") + File.separator + path ;
                        }else if(wxImage.toContact instanceof WXGroup){
                            path = wxImage.toContact.name.replaceAll("<[^>]+>", "") + File.separator + path ;
                        }
                        wxImage.image = wxAPI.webwxgetmsgimg(msg.MsgId,"big",path);
                        return wxImage;
                    }
                    case RspSync.AddMsg.TYPE_VOICE: {
                        WXVoice wxVoice = parseCommon(msg, new WXVoice());
                        wxVoice.voiceLength = msg.VoiceLength;
                        return wxVoice;
                    }
                    case RspSync.AddMsg.TYPE_VERIFY: {
                        WXVerify wxVerify = parseCommon(msg, new WXVerify());
                        wxVerify.userId = msg.RecommendInfo.UserName;
                        wxVerify.userName = msg.RecommendInfo.NickName;
                        wxVerify.signature = msg.RecommendInfo.Signature;
                        wxVerify.province = msg.RecommendInfo.Province;
                        wxVerify.city = msg.RecommendInfo.City;
                        wxVerify.gender = msg.RecommendInfo.Sex;
                        wxVerify.verifyFlag = msg.RecommendInfo.VerifyFlag;
                        wxVerify.ticket = msg.RecommendInfo.Ticket;
                        return wxVerify;
                    }
                    case RspSync.AddMsg.TYPE_RECOMMEND: {
                        WXRecommend wxRecommend = parseCommon(msg, new WXRecommend());
                        wxRecommend.userId = msg.RecommendInfo.UserName;
                        wxRecommend.userName = msg.RecommendInfo.NickName;
                        wxRecommend.gender = msg.RecommendInfo.Sex;
                        wxRecommend.signature = msg.RecommendInfo.Signature;
                        wxRecommend.province = msg.RecommendInfo.Province;
                        wxRecommend.city = msg.RecommendInfo.City;
                        wxRecommend.verifyFlag = msg.RecommendInfo.VerifyFlag;
                        return wxRecommend;
                    }
                    case RspSync.AddMsg.TYPE_VIDEO: {
                        //视频貌似可以分片，后期测试
                        WXVideo wxVideo = parseCommon(msg, new WXVideo());
                        wxVideo.imgWidth = msg.ImgWidth;
                        wxVideo.imgHeight = msg.ImgHeight;
                        wxVideo.videoLength = msg.PlayLength;
                        String path = "video";
                        if(wxVideo.fromGroup !=null){
                            path = wxVideo.fromGroup.name.replaceAll("<[^>]+>", "") + File.separator + path ;
                        }else if(wxVideo.toContact instanceof WXGroup){
                            path = wxVideo.toContact.name.replaceAll("<[^>]+>", "") + File.separator + path ;
                        }
                        wxVideo.image = wxAPI.webwxgetmsgimg(msg.MsgId,"slave",path);
                        return wxVideo;
                    }
                    case RspSync.AddMsg.TYPE_EMOJI: {
                        WXEmoji wxEmoji = parseCommon(msg, new WXEmoji());
                        wxEmoji.imgWidth = msg.ImgWidth;
                        wxEmoji.imgHeight = msg.ImgHeight;
                        if (StringUtils.isEmpty(msg.Content) || msg.HasProductId > 0) {
                            //表情商店的表情，无法下载图片
                            return wxEmoji;
                        } else {
                            //非表情商店的表情，下载图片
                            String path = "emoji";
                            if(wxEmoji.fromGroup !=null){
                                path = wxEmoji.fromGroup.name.replaceAll("<[^>]+>", "") + File.separator + path ;
                            }else if(wxEmoji.toContact instanceof WXGroup){
                                path = wxEmoji.toContact.name.replaceAll("<[^>]+>", "") + File.separator + path ;
                            }
                            wxEmoji.image = wxAPI.webwxgetmsgimg(msg.MsgId, "big",path);
                            wxEmoji.origin = wxEmoji.image;
                            return wxEmoji;
                        }
                    }
                    case RspSync.AddMsg.TYPE_OTHER: {
                        if (msg.AppMsgType == 2) {
                            WXImage wxImage = parseCommon(msg, new WXImage());
                            wxImage.imgWidth = msg.ImgWidth;
                            wxImage.imgHeight = msg.ImgHeight;
                            String path = "image";
                            if(wxImage.fromGroup !=null){
                                path = wxImage.fromGroup.name.replaceAll("<[^>]+>", "") + File.separator + path ;
                            }else if(wxImage.toContact instanceof WXGroup){
                                path = wxImage.toContact.name.replaceAll("<[^>]+>", "") + File.separator + path ;
                            }
                            wxImage.image = wxAPI.webwxgetmsgimg(msg.MsgId, "big",path);
                            wxImage.origin = wxImage.image;
                            return wxImage;
                        } else if (msg.AppMsgType == 5) {
                            WXLink wxLink = parseCommon(msg, new WXLink());
                            wxLink.linkName = msg.FileName;
                            wxLink.linkUrl = msg.Url;
                            return wxLink;
                        } else if (msg.AppMsgType == 6) {
                            WXFile wxFile = parseCommon(msg, new WXFile());
                            wxFile.fileId = msg.MediaId;
                            wxFile.fileName = msg.FileName;
                            wxFile.fileSize = StringUtils.isEmpty(msg.FileSize) ? 0 : Long.valueOf(msg.FileSize);
                            return wxFile;
                        } else if (msg.AppMsgType == 8) {
                            WXEmoji wxEmoji = parseCommon(msg, new WXEmoji());
                            wxEmoji.imgWidth = msg.ImgWidth;
                            wxEmoji.imgHeight = msg.ImgHeight;
                            String path = "emoji";
                            if(wxEmoji.fromGroup !=null){
                                path = wxEmoji.fromGroup.name.replaceAll("<[^>]+>", "") + File.separator + path ;
                            }else if(wxEmoji.toContact instanceof WXGroup){
                                path = wxEmoji.toContact.name.replaceAll("<[^>]+>", "") + File.separator + path ;
                            }
                            wxEmoji.image = wxAPI.webwxgetmsgimg(msg.MsgId, "big",path);
                            wxEmoji.origin = wxEmoji.image;
                            return wxEmoji;
                        } else if (msg.AppMsgType == 2000) {
                            return parseCommon(msg, new WXMoney());
                        }
                        break;
                    }
                    case RspSync.AddMsg.TYPE_NOTIFY: {
                        WXNotify wxNotify = parseCommon(msg, new WXNotify());
                        wxNotify.notifyCode = msg.StatusNotifyCode;
                        wxNotify.notifyContact = msg.StatusNotifyUserName;
                        return wxNotify;
                    }
                    case RspSync.AddMsg.TYPE_SYSTEM: {
                        return parseCommon(msg, new WXSystem());
                    }
                    case RspSync.AddMsg.TYPE_REVOKE:
                        WXRevoke wxRevoke = parseCommon(msg, new WXRevoke());
                        Matcher idMatcher = REX_REVOKE_ID.matcher(wxRevoke.content);
                        if (idMatcher.find()) {
                            wxRevoke.msgId = Long.valueOf(idMatcher.group(1));
                        }
                        Matcher replaceMatcher = REX_REVOKE_REPLACE.matcher(wxRevoke.content);
                        if (replaceMatcher.find()) {
                            wxRevoke.msgReplace = replaceMatcher.group(1);
                        }
                        return wxRevoke;
                    default:
                        break;
                }
            } catch (Exception e) {
                log.error( "消息解析失败",e.getMessage());
            }
            return parseCommon(msg, new WXUnknown());
        }
    }
}

