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
     * udf地址 如http://xxx.xxx.com/flink-streaming-udf.jar
     */
    private String  udfJarPath;



}
