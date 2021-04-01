package com.flink.streaming.web.model.to;

import lombok.Data;

/**
 * yarn http基本信息
 *
 * @author zhuhuipei
 * @Description:
 * @date 2020-08-06
 * @time 23:14
 */
@Data
public class AppTO {

    /**
     * yarn appid
     */
    private String id;

    /**
     * 运行用户 如 hadoop
     */
    private String user;

    /**
     * 运行的任务名称
     */
    private String name;

    /**
     * 队列名称
     */
    private String queue;

    /**
     * 运行状态
     */
    private String state;

    private String finalStatus;

    private Integer progress;

    private String trackingUI;

    /**
     * 连接地址
     */
    private String trackingUrl;


    /**
     * 应用类型
     */
    private String applicationType;


    /**
     * 运行开始时间
     */
    private Long startedTime;


    private Long finishedTime;

    /**
     * AM 容器地址
     */
    private String amContainerLogs;

}
