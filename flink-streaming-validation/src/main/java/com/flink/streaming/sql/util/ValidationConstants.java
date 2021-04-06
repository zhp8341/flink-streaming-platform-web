package com.flink.streaming.sql.util;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2021/4/5
 * @time 10:05
 */
public class ValidationConstants {

    public static final String MESSAGE_010 = "必须包含 insert or insert overwrite 语句";

    public static final String MESSAGE_011 = "暂时不支持直接使用select语句，请使用 insert into  select 语法";
}
