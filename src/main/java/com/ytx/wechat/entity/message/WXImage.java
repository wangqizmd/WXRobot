package com.ytx.wechat.entity.message;

import java.io.File;
import java.io.Serializable;

/**
 * 微信静态或动态图片消息
 */
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

        WXImage wxImage = (WXImage) o;

        if (imgWidth != wxImage.imgWidth) {
            return false;
        }
        if (imgHeight != wxImage.imgHeight) {
            return false;
        }
        if (image != null ? !image.equals(wxImage.image) : wxImage.image != null) {
            return false;
        }
        return origin != null ? origin.equals(wxImage.origin) : wxImage.origin == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + imgWidth;
        result = 31 * result + imgHeight;
        result = 31 * result + (image != null ? image.hashCode() : 0);
        result = 31 * result + (origin != null ? origin.hashCode() : 0);
        return result;
    }

    @Override
    public WXImage clone() {
        return (WXImage) super.clone();
    }
}
