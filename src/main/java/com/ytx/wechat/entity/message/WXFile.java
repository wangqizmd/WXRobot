package com.ytx.wechat.entity.message;

import lombok.Data;

import java.io.File;
import java.io.Serializable;

/**
 * 微信文件消息
 */
@Data
public class WXFile extends WXMessage implements Serializable, Cloneable {
    /**
     * 文件id，用于获取文件
     */
    public String fileId;
    /**
     * 文件名
     */
    public String fileName;
    /**
     * 文件大小
     */
    public long fileSize;
    /**
     * 文件内容，刚开始为null，需要手动获取之后才会有内容
     */
    public File file;
}
