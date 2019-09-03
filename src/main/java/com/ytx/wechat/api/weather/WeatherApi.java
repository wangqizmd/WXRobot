package com.ytx.wechat.api.weather;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ytx.wechat.api.weather.entity.Weather;
import com.ytx.wechat.config.GlobalConfig;
import com.ytx.wechat.entity.message.WXMessage;
import lombok.extern.slf4j.Slf4j;
import me.xuxiaoxiao.xtools.common.XTools;
import me.xuxiaoxiao.xtools.common.http.executor.impl.XRequest;
import org.apache.commons.lang3.StringUtils;

/**
 * 查询天气API
 */

@Slf4j
public class WeatherApi {

    private static final String WEATHER_API = GlobalConfig.getValue("weatherApi", "");

    private static final String WEATHER_KEY = GlobalConfig.getValue("weatherKey", "");

    /**
     * 总入口
     *
     * @param message
     * @return
     */
    public static String dealWeatherMsg(WXMessage message) {
        String keyword = message.content.trim();
        if (StringUtils.isBlank(keyword)) {
            return null;
        }
        if ("天气".equals(keyword)) {
            if (StringUtils.isBlank(WEATHER_API)||StringUtils.isBlank(WEATHER_KEY)) {
                return "抱歉，天气功能暂未开启，请联系管理员开启\n";
            }
            return getFastWeatherCommand(message);
        }
        if (keyword.endsWith("天气")) {
            if (StringUtils.isBlank(WEATHER_API)||StringUtils.isBlank(WEATHER_KEY)) {
                return "抱歉，天气功能暂未开启，请联系管理员开启\n";
            }
            return getWeatherByKeyword(keyword);
        }
        return null;
    }

    /**
     * 快速查询天气，根据发消息的用户微信名片上的地址发送天气预报
     *
     * @param message
     * @return
     */
    private static String getFastWeatherCommand(WXMessage message) {
        String fromUserCity = message.fromUser.city;
        if(StringUtils.isBlank(fromUserCity)){
            return "抱歉，未获取到您所在城市。输入指定市/区/县查天气，如\"北京天气\"。\n";
        }
        String response = getWeatherFromApi(fromUserCity);
        return StringUtils.isNotBlank(response) ? response :
                "抱歉，未获取到您所在城市。输入指定市/区/县查天气，如\"北京天气\"。\n";
    }

    /**
     * 根据关键字查询天气接口
     *
     * @param keyword 关键字指以“天气”开头的词，举例：北京天气
     * @return 查询到天气返回天气；未查询到返回抱歉语句。
     */
    public static String getWeatherByKeyword(String keyword) {
        String cityName = keyword.substring(0, keyword.length() - 2).trim();
        String response = getWeatherFromApi(cityName);
        if (StringUtils.isBlank(response)) {
            response = "抱歉，未查询到\"" + keyword + "\"。" + "只支持查询国内(部分)市/区/县天气。\n";
        }
        log.info("WeatherApi::getWeatherByKeyword, keyword:{}, cityName:{},response:{}", keyword, cityName, response);
        return response;
    }

    /**
     * 根据城市名查询今日天气。未查询到返回null
     *
     * @param cityName
     * @return
     */
    private static String getWeatherFromApi(String cityName) {
        String result = null;
        try{
            XRequest request = XRequest.GET(WEATHER_API + cityName + "&key=" + WEATHER_KEY);
            Weather weather = JSON.parseObject(JSON.parseArray(JSONObject.parseObject(XTools.http(request).string()).get("HeWeather6").toString()).get(0).toString(), Weather.class);
            result = weather.toString();
        }catch (Exception e){
            log.error("查询{}天气失败：{}",cityName,e.getMessage());
        }

        return result;
    }

}
