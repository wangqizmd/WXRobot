package com.ytx.wechat.api.chat;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ytx.wechat.config.GlobalConfig;
import com.ytx.wechat.utils.HTMLSpirit;
import lombok.extern.slf4j.Slf4j;
import me.xuxiaoxiao.xtools.common.XTools;
import me.xuxiaoxiao.xtools.common.http.executor.impl.XRequest;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import static com.ytx.wechat.utils.MD5.md5;

/**
 * @author wangqi
 * @version 1.0
 * @className ChatApi
 * @description TODO
 * @date 2019/9/5 17:21
 */
@Slf4j
public class ChatApi {

    private static final String APP_URL = GlobalConfig.getValue("appUrl", "");

    private static final String APP_ID = GlobalConfig.getValue("appId", "");

    private static final String APP_KEY = GlobalConfig.getValue("appKey", "");

    /**
     * 总入口
     *
     * @param
     * @return
     */
    public static String dealMsg(String content) {
        try{

            Map<String, Object> params = new HashMap<>();
            params.put("app_id", APP_ID);
            params.put("time_stamp", System.currentTimeMillis() / 1000);
            params.put("nonce_str", "123");
            params.put("question", HTMLSpirit.delHTMLTag(content));
            params.put("session", "wangqiceshi");
            params.put("sign", getSignature(params));
            XRequest request = XRequest.GET(APP_URL);
            request.query("app_id", params.get("app_id"));
            request.query("time_stamp", params.get("time_stamp"));
            request.query("nonce_str", params.get("nonce_str"));
            request.query("question", params.get("question"));
            request.query("session", params.get("session"));
            request.query("sign", params.get("sign"));
            String resp = XTools.http(request).string();
            JSONObject object = JSONObject.parseObject(resp);
            if(object.getInteger("ret")==0){
                return object.getJSONObject("data").getString("answer");
            }
        }catch (Exception e){
            log.error("闲聊接口出现问题",e);
        }
        return null;
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
                    .append(APP_KEY);
        }
        try {
            String sign = md5(baseString.toString());
            return sign.toUpperCase();
        } catch (Exception ex) {
            throw new IOException(ex);
        }
    }

}
