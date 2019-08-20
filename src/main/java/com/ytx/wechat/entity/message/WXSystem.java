package com.ytx.wechat.entity.message;

import lombok.Data;

import java.io.Serializable;

/**
 * 微信系统消息，新人入群，被踢出群，红包消息等
 */
@Data
public class WXSystem extends WXMessage implements Serializable, Cloneable {
}
