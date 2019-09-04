package com.ytx.wechat.api.weather.entity;

import lombok.Data;

import java.util.Date;

@Data
public class DailyForecast {
    private String cond_code_d;
    private String cond_code_n;
    private String cond_txt_d;
    private String cond_txt_n;
    private Date date;
    private String hum;
    private String mr;
    private String ms;
    private String pcpn;
    private String pop;
    private String pres;
    private String sr;
    private String ss;
    private String tmp_max;
    private String tmp_min;
    private String uv_index;
    private String vis;
    private String wind_deg;
    private String wind_dir;
    private String wind_sc;
    private String wind_spd;
}
