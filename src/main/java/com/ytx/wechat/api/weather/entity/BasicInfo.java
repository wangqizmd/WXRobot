package com.ytx.wechat.api.weather.entity;

import lombok.Data;

@Data
public class BasicInfo {
    private String cid;
    private String location;
    private String parent_city;
    private String admin_area;
    private String cnty;
    private String lat;
    private String lon;
    private String tz;
}
