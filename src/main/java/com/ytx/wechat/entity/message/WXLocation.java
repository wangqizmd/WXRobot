package com.ytx.wechat.entity.message;

import java.io.Serializable;

/**
 * 微信位置消息
 */
public class WXLocation extends WXMessage implements Serializable, Cloneable {
    /**
     * 地点名称
     */
    public String locationName;
    /**
     * 地点地图图片
     */
    public String locationImage;
    /**
     * 地点的腾讯地图url
     */
    public String locationUrl;

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

        WXLocation that = (WXLocation) o;

        if (locationName != null ? !locationName.equals(that.locationName) : that.locationName != null) {
            return false;
        }
        if (locationImage != null ? !locationImage.equals(that.locationImage) : that.locationImage != null) {
            return false;
        }
        return locationUrl != null ? locationUrl.equals(that.locationUrl) : that.locationUrl == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (locationName != null ? locationName.hashCode() : 0);
        result = 31 * result + (locationImage != null ? locationImage.hashCode() : 0);
        result = 31 * result + (locationUrl != null ? locationUrl.hashCode() : 0);
        return result;
    }

    @Override
    public WXLocation clone() {
        return (WXLocation) super.clone();
    }
}
