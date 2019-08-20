package com.ytx.wechat.entity.message;

import lombok.Data;

import java.io.File;
import java.io.Serializable;

/**
 * 微信视频消息
 */
@Data
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
}
