package com.flink.streaming.web.common.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;

/**
 * 加密
 *
 * @author zhuhuipei
 * @date 2020-07-13
 * @time 23:30
 */
@Slf4j
public class Md5Utils {

    public static String getMD5String(String value) {

        return DigestUtils.md5Hex(value);

    }


    public static void main(String[] args) {

        System.out.println(getMD5String("123456"));
        System.out.println(getMD5String("123456"));

    }
}
