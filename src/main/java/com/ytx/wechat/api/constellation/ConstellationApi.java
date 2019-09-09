package com.ytx.wechat.api.constellation;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ytx.wechat.api.constellation.entity.Constellation;
import com.ytx.wechat.config.GlobalConfig;
import com.ytx.wechat.entity.message.WXMessage;
import lombok.extern.slf4j.Slf4j;
import me.xuxiaoxiao.xtools.common.XTools;
import me.xuxiaoxiao.xtools.common.http.executor.impl.XRequest;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ConstellationApi {

    private static final String constellationApi = GlobalConfig.getValue("constellationApi", "");

    private static final String constellationKey = GlobalConfig.getValue("constellationKey", "");

    private static final String constellationType = GlobalConfig.getValue("constellationType", "");

    private static final List<String> constellationList = new ArrayList();

    static {
        constellationList.add("白羊座");
        constellationList.add("金牛座");
        constellationList.add("双子座");
        constellationList.add("巨蟹座");
        constellationList.add("狮子座");
        constellationList.add("处女座");
        constellationList.add("天秤座");
        constellationList.add("天蝎座");
        constellationList.add("射手座");
        constellationList.add("摩羯座");
        constellationList.add("水瓶座");
        constellationList.add("双鱼座");
    }
    public static String dealConstellationMsg(WXMessage message) {
        String keyword = message.content.trim();
        if (StringUtils.isBlank(keyword)) {
            return null;
        }
        if (constellationList.contains(keyword)) {
            if (StringUtils.isBlank(constellationApi)||StringUtils.isBlank(constellationKey)||StringUtils.isBlank(constellationType)) {
                return "抱歉，星座运势功能暂未开启，请联系管理员开启\n";
            }
            try{
                XRequest request = XRequest.GET(constellationApi + keyword + "&key=" + constellationKey + "&type=" + constellationType);
                Constellation constellation = JSON.parseObject(JSONObject.parseObject(XTools.http(request).string()).get("result1").toString(), Constellation.class);
                return constellation.toString();
            }catch (Exception e){
                log.error("查询{}运势失败：{}",keyword,e.getMessage());
            }
        }
        return null;
    }
}
