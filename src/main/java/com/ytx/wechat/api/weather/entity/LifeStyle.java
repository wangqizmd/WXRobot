package com.ytx.wechat.api.weather.entity;

import lombok.Data;

@Data
public class LifeStyle {

    private LifeStyleType type;
    private String brf;
    private String txt;


    public LifeStyleType getType() {
        return type;
    }

    public void setType(String type) {
        if(type == null){
            return;
        }
        this.type = LifeStyleType.getInStance(type);
    }

    @Override
    public String toString() {
        if(type ==null){
            return null;
        }
        return type.name +"：" + brf +"，"+ txt +"\n";
    }
}
