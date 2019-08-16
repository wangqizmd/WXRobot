package com.ytx.wechat.entity.message;

import java.io.Serializable;

/**
 * 微信表情商店表情消息，该类型的消息无法下载图片
 */
public class WXEmoji extends WXMessage implements Serializable, Cloneable {
    /**
     * 图片宽度
     */
    public int imgWidth;
    /**
     * 图片高度
     */
    public int imgHeight;

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

        WXEmoji wxEmoji = (WXEmoji) o;
        if (imgWidth != wxEmoji.imgWidth) {
            return false;
        }
        return imgHeight == wxEmoji.imgHeight;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + imgWidth;
        result = 31 * result + imgHeight;
        return result;
    }

    @Override
    public WXEmoji clone() {
        return (WXEmoji) super.clone();
    }
}
