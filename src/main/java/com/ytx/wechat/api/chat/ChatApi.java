package com.ytx.wechat.api.chat;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ytx.wechat.api.weather.entity.Weather;
import jodd.http.HttpRequest;
import jodd.http.HttpResponse;
import me.xuxiaoxiao.xtools.common.XTools;
import me.xuxiaoxiao.xtools.common.http.executor.impl.XRequest;
import okhttp3.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

/**
 * @author wangqi
 * @version 1.0
 * @className ChatApi
 * @description TODO
 * @date 2019/9/5 17:21
 */
public class ChatApi {

    private final static OkHttpClient CLIENT = new OkHttpClient.Builder()
            .connectionPool(new ConnectionPool(20, 5, TimeUnit.MINUTES)).readTimeout(3, TimeUnit.SECONDS)
            .connectTimeout(3, TimeUnit.SECONDS).build();

    public static void main(String[] args) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("app_id", "2121538684");
        params.put("time_stamp", System.currentTimeMillis() / 1000);
        params.put("nonce_str", "123");
        params.put("question", "你叫什么");
        params.put("session", "wangqiceshi");
        params.put("sign", getSignature(params));
        System.out.println(JSON.toJSONString(params));


        long start = System.currentTimeMillis();
        XRequest request = XRequest.GET("https://api.ai.qq.com/fcgi-bin/nlp/nlp_textchat");
        request.query("app_id", "2121538684");
        request.query("time_stamp", params.get("time_stamp"));
        request.query("nonce_str", params.get("nonce_str"));
        request.query("question", "你叫什么");
        request.query("session", "wangqiceshi");
        request.query("sign", params.get("sign"));
        String resp = XTools.http(request).string();
        long end = System.currentTimeMillis();
        System.out.println("请求时间：" + (end - start));


        HttpResponse response =
                HttpRequest.post("https://api.ai.qq.com/fcgi-bin/nlp/nlp_textchat").form(params)
                        .send();
        String resp1 = response.bodyText();
        System.out.println("请求时间：" + (System.currentTimeMillis() - end));

    }


    public static String getSignature(Map<String, Object> params) throws IOException {
        Map<String, Object> sortedParams = new TreeMap<>(params);
        Set<Map.Entry<String, Object>> entrys = sortedParams.entrySet();
        StringBuilder baseString = new StringBuilder();
        for (Map.Entry<String, Object> param : entrys) {
            if (param.getValue() != null && !"".equals(param.getKey().trim()) &&
                    !"sign".equals(param.getKey().trim()) && !"".equals(param.getValue())) {
                baseString.append(param.getKey().trim()).append("=")
                        .append(URLEncoder.encode(param.getValue().toString(), "UTF-8")).append("&");
            }
        }
        if (baseString.length() > 0) {
            baseString.deleteCharAt(baseString.length() - 1).append("&app_key=")
                    .append("IQszD18ZUp9fYMJo");
        }
        try {
            String sign = md5(baseString.toString());
            System.out.println("sign:" + sign.toUpperCase());
            return sign.toUpperCase();
        } catch (Exception ex) {
            throw new IOException(ex);
        }
    }

    public static String md5(String s){
// 获取信息摘要 - 参数字典排序后字符串
        try {
            // 指定sha1算法
            MessageDigest digest = MessageDigest.getInstance("MD5");//sun.security.provider.SHA@74c6fd6e //sun.security.provider.MD2@4e2c390c
            digest.update(s.getBytes("UTF-8"));
            // 获取字节数组
            byte messageDigest[] = digest.digest();
            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            // 字节数组转换为 十六进制 数
            for (int i = 0; i < messageDigest.length; i++) {
                String shaHex = Integer.toHexString(messageDigest[i] & 0xFF);
                if (shaHex.length() < 2) {
                    hexString.append(0);
                }
                hexString.append(shaHex);
            }
            return hexString.toString().toUpperCase();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;

    }
}
