package com.ytx.wechat.entity.message;

import java.io.File;
import java.io.Serializable;

/**
 * 微信文件消息
 */
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

        WXFile wxFile = (WXFile) o;

        if (fileSize != wxFile.fileSize) {
            return false;
        }
        if (fileId != null ? !fileId.equals(wxFile.fileId) : wxFile.fileId != null) {
            return false;
        }
        if (fileName != null ? !fileName.equals(wxFile.fileName) : wxFile.fileName != null) {
            return false;
        }
        return file != null ? file.equals(wxFile.file) : wxFile.file == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (fileId != null ? fileId.hashCode() : 0);
        result = 31 * result + (fileName != null ? fileName.hashCode() : 0);
        result = 31 * result + (int) (fileSize ^ (fileSize >>> 32));
        result = 31 * result + (file != null ? file.hashCode() : 0);
        return result;
    }

    @Override
    public WXFile clone() {
        return (WXFile) super.clone();
    }
}
