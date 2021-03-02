package com.flink.streaming.web.model.entity;

import lombok.Data;

import java.util.Date;

/**
 *
 * @author zhuhuipei
 * @date 2021/2/27
 * @time 17:09
 */
@Data
public class JobAlarmConfig {

    private Long id;

    /**
     * job_config主表id
     */
    private Long jobId;

    /**
     * 类型 1:钉钉告警 2:url回调 3:异常自动拉起任务
     */
    private Integer type;

    /**
     * 更新版本号  
     */
    private Integer version;

    private Integer isDeleted;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date editTime;

    private String creator;

    private String editor;


}
