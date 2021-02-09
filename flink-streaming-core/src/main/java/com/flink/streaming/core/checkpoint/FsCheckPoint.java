package com.flink.streaming.core.checkpoint;

import com.flink.streaming.core.model.CheckPointParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.runtime.state.filesystem.FsStateBackend;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.CheckpointConfig.ExternalizedCheckpointCleanup;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2021/1/17
 * @time 20:54
 */
@Slf4j
public class FsCheckPoint {
    public static void setCheckpoint(StreamExecutionEnvironment env, CheckPointParam checkPointParam) {

        if (checkPointParam == null) {
            log.warn("############没有启用Checkpoint############");
            return;
        }

        if (StringUtils.isEmpty(checkPointParam.getCheckpointDir())) {
            throw new RuntimeException("checkpoint目录不存在");
        }

        log.info("开启checkpoint checkPointParam={}", checkPointParam);

        // 默认每60s保存一次checkpoint
        env.enableCheckpointing(checkPointParam.getCheckpointInterval());

        CheckpointConfig checkpointConfig = env.getCheckpointConfig();

        //开始一致性模式是：精确一次 exactly-once
        if (StringUtils.isEmpty(checkPointParam.getCheckpointingMode()) ||
                CheckpointingMode.EXACTLY_ONCE.name().equalsIgnoreCase(checkPointParam.getCheckpointingMode())) {
            checkpointConfig.setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
        } else {
            checkpointConfig.setCheckpointingMode(CheckpointingMode.AT_LEAST_ONCE);
        }

        //默认超时10 minutes.
        checkpointConfig.setCheckpointTimeout(checkPointParam.getCheckpointTimeout());
        //确保检查点之间有至少500 ms的间隔【checkpoint最小间隔】
        checkpointConfig.setMinPauseBetweenCheckpoints(500);
        //同一时间只允许进行一个检查点
        checkpointConfig.setMaxConcurrentCheckpoints(2);

        checkpointConfig.setTolerableCheckpointFailureNumber(checkPointParam.getTolerableCheckpointFailureNumber());

        if (checkPointParam.getAsynchronousSnapshots() != null) {
            env.setStateBackend(new FsStateBackend(checkPointParam.getCheckpointDir(),
                    checkPointParam.getAsynchronousSnapshots()));
        } else {
            env.setStateBackend(new FsStateBackend(checkPointParam.getCheckpointDir()));
        }

        if (checkPointParam.getExternalizedCheckpointCleanup() != null) {
            if (checkPointParam.getExternalizedCheckpointCleanup().equalsIgnoreCase(ExternalizedCheckpointCleanup.DELETE_ON_CANCELLATION.name())) {
                env.getCheckpointConfig().enableExternalizedCheckpoints(ExternalizedCheckpointCleanup.DELETE_ON_CANCELLATION);
            } else if(checkPointParam.getExternalizedCheckpointCleanup().equalsIgnoreCase(ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION.name())) {
                env.getCheckpointConfig().enableExternalizedCheckpoints(ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);
            }
        }

    }

}
