package com.ytx.wechat.api.constellation.entity;

import com.ytx.wechat.api.weather.entity.LifeStyle;
import lombok.Data;

@Data
public class Constellation {
        private String resultcode;
        private String error_code;
        private String name;
        private String datetime;
        private String date;
        private String all;
        private String color;
        private String health;
        private String love;
        private String money;
        private String number;
        private String QFriend;
        private String summary;
        private String work;

        @Override
        public String toString() {
                if(!"200".equals(resultcode)){
                        return "查询星座运势失败";
                }
                return "【" +
                        name +
                        "今日运势】\n" +
                        "综合指数:" + all +"\n" +
                        "幸运颜色:" + color +"\n" +
                        "健康指数:" + health +"\n" +
                        "爱情指数:" + love +"\n" +
                        "财运指数:" + money +"\n" +
                        "幸运数字:" + number +"\n" +
//                        "速配星座:" + QFriend +"\n" +
                        "工作指数:" + work +"\n" +
                        "运势解析:" + summary ;
        }
}
