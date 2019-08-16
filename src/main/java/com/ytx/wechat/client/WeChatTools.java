package com.ytx.wechat.client;

import me.xuxiaoxiao.xtools.common.http.executor.impl.XRequest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;

final class WeChatTools {
    private static final char[] HEX = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    /**
     * 判断文件的media类型，共有三种，pic：图片，video：短视频，doc：其他文件
     *
     * @param file 需要判断的文件
     * @return 文件的media类型
     */
    public static String fileType(File file) {
        switch (WeChatTools.fileSuffix(file)) {
            case "bmp":
            case "png":
            case "jpeg":
            case "jpg":
                return "pic";
            case "mp4":
                return "video";
            default:
                return "doc";
        }
    }

    /**
     * 获取文件的扩展名，图片类型的文件，会根据文件内容自动判断文件扩展名
     *
     * @param file 要获取文件扩展名的文件
     * @return 文件扩展名
     */
    public static String fileSuffix(File file) {
        try (FileInputStream is = new FileInputStream(file)) {
            byte[] b = new byte[3];
            is.read(b, 0, b.length);
            String fileCode = bytesToHex(b);

            switch (fileCode) {
                case "ffd8ff":
                    return "jpg";
                case "89504e":
                    return "png";
                case "474946":
                    return "gif";
                default:
                    if (fileCode.startsWith("424d")) {
                        return "bmp";
                    } else if (file.getName().lastIndexOf('.') > 0) {
                        return file.getName().substring(file.getName().lastIndexOf('.') + 1);
                    } else {
                        return "";
                    }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 将字节数组转换成16进制字符串
     *
     * @param bytes 要转换的字节数组
     * @return 转换后的字符串，全小写字母
     */
    private static String bytesToHex(byte[] bytes) {
        char[] chars = new char[bytes.length * 2];
        for (int i = 0; i < bytes.length; i++) {
            byte b = bytes[i];
            chars[i << 1] = HEX[b >>> 4 & 0xf];
            chars[(i << 1) + 1] = HEX[b & 0xf];
        }
        return new String(chars);
    }

    /**
     * 文件分片的请求体Part，微信在上传文件时，超过1M的文件，会进行分片上传，每片大小会根据网速等因素调整。
     * 为了简单起见，本库每片大小512KB
     */
    public static final class Slice extends XRequest.MultipartContent.Part {
        /**
         * 文件名称
         */
        public String fileName;
        /**
         * 文件的MIME类型
         */
        public String fileMime;
        /**
         * 字节数组内容的数量，字节数组大小总是512K而实际内容可能并没有这么多
         */
        public int count;

        public Slice(String name, String fileName, String fileMime, byte[] slice, int count) {
            super(name, slice);
            this.fileName = fileName;
            this.fileMime = fileMime;
            this.count = count;
        }

        @Override
        public String[] headers() throws IOException {
            String disposition = String.format("Content-Disposition: form-data; name=\"%s\"; filename=\"%s\"", name, URLEncoder.encode(fileName, "utf-8"));
            String type = String.format("Content-Type: %s", fileMime);
            return new String[]{disposition, type};
        }

        @Override
        public long partLength() {
            return count;
        }

        @Override
        public void partWrite(OutputStream doStream) throws IOException {
            doStream.write((byte[]) value, 0, count);
        }
    }
}
