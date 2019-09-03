package com.ytx.wechat.api.weather.entity;

import lombok.Data;

import java.util.Date;

@Data
public class UpdateTime {

    private Date loc;
    private Date utc;
}