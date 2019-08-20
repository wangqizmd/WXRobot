package com.ytx.wechat.utils;

import com.ytx.wechat.entity.message.WXMessage;
import org.apache.commons.lang3.StringUtils;


/**
 * Created by IntelliJ IDEA.
 *
 * @author
 * @date 2019/6/20 17:36
 */
public class GroupMsgUtil {

    public static String getUserDisplayOrName(WXMessage message) {
        String display = message.fromGroup.members.containsKey(message.fromUser.id)
                ? message.fromGroup.members.get(message.fromUser.id).display : null;
        return StringUtils.isBlank(display) ? message.fromUser.name : display.trim();
    }
}
