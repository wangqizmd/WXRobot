package com.ytx.wechat.entity.message;

import lombok.Data;

import java.io.File;
import java.io.Serializable;

/**
 * 微信静态或动态图片消息
 */
@Data
public class WXImage extends WXMessage implements Serializable, Cloneable {
    /**
     * 图片宽度
     */
    public int imgWidth;
    /**
     * 图片高度
     */
    public int imgHeight;
    /**
     * 静态图消息中的缩略图，动态图消息中的原图
     */
    public File image;
    /**
     * 静态图获取原图之前为null，获取原图之后为原图，动态图一开始就是原图
     */
    public File origin;

}
