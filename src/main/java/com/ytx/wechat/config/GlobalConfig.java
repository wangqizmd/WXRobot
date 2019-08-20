package com.ytx.wechat.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

@Slf4j
public class GlobalConfig {

    private static Properties properties;

    static {
        properties = new Properties();
        try {
            properties.load(new InputStreamReader(GlobalConfig.class.getResourceAsStream("/config.properties"), StandardCharsets.UTF_8));
        } catch (Exception e) {
            log.error("读取config.properties文件异常!", e);
        }
    }

    public static String getValue(String key, String defaultValue) {
        if (StringUtils.isBlank(key)) {
            return defaultValue;
        }
        String value = properties.getProperty(key);
        return StringUtils.isBlank(value) ? defaultValue : value;
    }
}

