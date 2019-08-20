package com.ytx.wechat.entity.message;

import lombok.Data;

import java.io.File;
import java.io.Serializable;

/**
 * 微信语音消息
 */
@Data
public class WXVoice extends WXMessage implements Serializable, Cloneable {
    /**
     * 语音长度，毫秒
     */
    public long voiceLength;
    /**
     * 语音文件
     */
    public File voice;
}
