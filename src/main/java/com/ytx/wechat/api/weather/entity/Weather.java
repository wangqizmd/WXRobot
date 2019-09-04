/**
 * Copyright 2019 bejson.com
 */
package com.ytx.wechat.api.weather.entity;

import lombok.Data;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

@Data
public class Weather {

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private BasicInfo basic;
    private UpdateTime update;
    private String status;
    private WeatherInfo now;
    private List<DailyForecast> daily_forecast;
    private List<LifeStyle> lifestyle;

    @Override
    public String toString() {
        if(!"ok".equals(status)){
            return "查询天气失败";
        }
        String lsString = "";
        for(LifeStyle tmp:lifestyle){
            if(tmp.toString()!=null){
                lsString = lsString + tmp.toString();
            }
        }
        return "【" +
                basic.getLocation() +
                "天气】\n" +
                "当前时间：" +
                DATE_FORMAT.format(update.getLoc()) + "\n" +
                now.toString() +
                lsString;
    }
}