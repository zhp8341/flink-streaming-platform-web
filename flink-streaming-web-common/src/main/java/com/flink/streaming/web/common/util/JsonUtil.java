package com.flink.streaming.web.common.util;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2021/3/31
 * @time 19:14
 */
@Slf4j
public class JsonUtil<T> {

    public static <T> T tojson(String res, Class<T> clazz) {

        try {
            return JSON.parseObject(res, clazz);
        } catch (Exception e) {
            log.error("parseObject is error");
        }
        return null;
    }
}
