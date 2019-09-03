package com.ytx.wechat.api.weather.entity;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

public enum LifeStyleType {
    comf("comf","舒适度指数"),
    cw("cw","洗车指数"),
    drsg("drsg","穿衣指数"),
    flu("flu","感冒指数"),
    sport("sport","运动指数"),
    trav("trav","旅游指数"),
    uv("uv","紫外线指数"),
    air("air","空气污染扩散条件指数"),
    ac("ac","空调开启指数"),
    ag("ag","过敏指数"),
    gl("gl","太阳镜指数"),
    mu("mu","化妆指数"),
    airc("airc","晾晒指数"),
    ptfc("ptfc","交通指数"),
    fsh("fsh","钓鱼指数"),
    spi("spi","防晒指数")
    ;
    String type;
    String name;

    LifeStyleType(String type, String name) {
        this.type = type;
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static LifeStyleType getInStance(String type){
        LifeStyleType lifeStyleType = null;
        if(StringUtils.isEmpty(type)){
            return null;
        }
        for(LifeStyleType styleType : LifeStyleType.values()){
            if(styleType.type.equals(type)){
                lifeStyleType = styleType;
                break;
            }
        }
        return lifeStyleType;
    }
}
