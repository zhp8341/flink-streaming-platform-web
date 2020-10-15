package com.flink.streaming.core.model;

import lombok.Data;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2020-08-21
 * @time 02:10
 */
@Data
public class JobRunParam {
    /**
     * sql语句目录
     */
    private String sqlPath;


    /**
     * CheckPoint 参数
     */
    private CheckPointParam checkPointParam;


    /**
     * udf注册名称如
     * utc2local|com.streaming.flink.udf.UTC2Local 多个可用;分隔
     * utc2local代表组册的名称
     * com.streaming.flink.udf.UTC2Local代表类名
     *
     */
    private String udfRegisterName;

    /**
     * udf地址 如http://xxx.xxx.com/flink-streaming-udf.jar
     */
    private String  udfJarPath;



}
