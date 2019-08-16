package com.ytx.wechat.entity.message;

import java.io.File;
import java.io.Serializable;

/**
 * 微信视频消息
 */
public class WXVideo extends WXMessage implements Serializable, Cloneable {
    /**
     * 视频缩略图宽度
     */
    public int imgWidth;
    /**
     * 视频缩略图高度
     */
    public int imgHeight;
    /**
     * 视频的长度，秒
     */
    public int videoLength;
    /**
     * 视频缩略图
     */
    public File image;
    /**
     * 视频文件
     */
    public File video;

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

        WXVideo wxVideo = (WXVideo) o;

        if (imgWidth != wxVideo.imgWidth) {
            return false;
        }
        if (imgHeight != wxVideo.imgHeight) {
            return false;
        }
        if (videoLength != wxVideo.videoLength) {
            return false;
        }
        if (image != null ? !image.equals(wxVideo.image) : wxVideo.image != null) {
            return false;
        }
        return video != null ? video.equals(wxVideo.video) : wxVideo.video == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + imgWidth;
        result = 31 * result + imgHeight;
        result = 31 * result + videoLength;
        result = 31 * result + (image != null ? image.hashCode() : 0);
        result = 31 * result + (video != null ? video.hashCode() : 0);
        return result;
    }

    @Override
    public WXVideo clone() {
        return (WXVideo) super.clone();
    }
}
