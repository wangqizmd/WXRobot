package com.ytx.wechat.entity.message;

import java.io.File;
import java.io.Serializable;

/**
 * 微信语音消息
 */
public class WXVoice extends WXMessage implements Serializable, Cloneable {
    /**
     * 语音长度，毫秒
     */
    public long voiceLength;
    /**
     * 语音文件
     */
    public File voice;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        WXVoice wxVoice = (WXVoice) o;

        if (voiceLength != wxVoice.voiceLength) {
            return false;
        }
        return voice != null ? voice.equals(wxVoice.voice) : wxVoice.voice == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (int) (voiceLength ^ (voiceLength >>> 32));
        result = 31 * result + (voice != null ? voice.hashCode() : 0);
        return result;
    }

    @Override
    public WXVoice clone() {
        return (WXVoice) super.clone();
    }
}
