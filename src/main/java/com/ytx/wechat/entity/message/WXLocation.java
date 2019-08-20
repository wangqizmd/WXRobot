package com.ytx.wechat.entity.message;

import lombok.Data;

import java.io.Serializable;

/**
 * 微信位置消息
 */
@Data
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
}
