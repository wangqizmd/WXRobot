package com.ytx.wechat.api.weather.entity;

import lombok.Data;

@Data
public class WeatherInfo {
    private String cloud;
    private String cond_code;
    private String cond_txt;
    private String fl;
    private String hum;
    private String pcpn;
    private String pres;
    private String tmp;
    private String vis;
    private String wind_deg;
    private String wind_dir;
    private String wind_sc;
    private String wind_spd;

    @Override
    public String toString() {
        return "今日：" + cond_txt +"，当前温度" +
                tmp + "度，体感温度" + fl + "度，" +
                wind_dir + wind_sc + "级，" +
                "风速"+ wind_spd +"公里/小时，" +
                "相对湿度" + hum + "%，" +
                "降水量" + pcpn + "毫升，" +
                "能见度" + vis + "公里\n" ;
    }
}
