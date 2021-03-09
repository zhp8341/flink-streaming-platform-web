package com.flink.streaming.common.model;


import com.flink.streaming.common.enums.StateBackendEnum;
import lombok.Data;


/**
 * @author zhuhuipei
 * @Description:
 * @date 2020-08-21
 * @time 23:16
 */
@Data
public class CheckPointParam {

    /**
     * 默认60S
     */
    private long checkpointInterval = 1000 * 60L;

    /**
     * 默认CheckpointingMode.EXACTLY_ONCE
     */
    private String checkpointingMode = "EXACTLY_ONCE";

    /**
     * 默认超时10 minutes.
     */
    private long checkpointTimeout = 10 * 60 * 1000;

    /**
     * 目录
     */
    private String checkpointDir;

    /**
     * 设置失败次数 默认一次
     */

    private int tolerableCheckpointFailureNumber = 1;

    /**
     * 是否异步
     */
    private Boolean asynchronousSnapshots;

    /**
     * 检查点在作业取消后的保留策略，DELETE_ON_CANCELLATION代表删除，RETAIN_ON_CANCELLATION代表保留
     */
    private String externalizedCheckpointCleanup;

    /**
     * 后端状态类型
     */
    private StateBackendEnum stateBackendEnum;

    /**
     * 支持增量
     */
    private Boolean enableIncremental;


}
