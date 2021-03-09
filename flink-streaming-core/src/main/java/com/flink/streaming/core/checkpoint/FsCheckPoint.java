package com.flink.streaming.core.checkpoint;


import com.flink.streaming.common.model.CheckPointParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.contrib.streaming.state.RocksDBStateBackend;
import org.apache.flink.runtime.state.filesystem.FsStateBackend;
import org.apache.flink.runtime.state.memory.MemoryStateBackend;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.CheckpointConfig.ExternalizedCheckpointCleanup;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.io.IOException;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2021/1/17
 * @time 20:54
 */
@Slf4j
public class FsCheckPoint {

    public static void setCheckpoint(StreamExecutionEnvironment env, CheckPointParam checkPointParam) throws Exception {
        if (checkPointParam == null) {
            log.warn("############没有启用Checkpoint############");
            return;
        }
        if (StringUtils.isEmpty(checkPointParam.getCheckpointDir())) {
            throw new RuntimeException("checkpoint目录不存在");
        }

        // 默认每60s保存一次checkpoint
        env.enableCheckpointing(checkPointParam.getCheckpointInterval());

        CheckpointConfig checkpointConfig = env.getCheckpointConfig();

        //开始一致性模式是：精确一次 exactly-once
        if (StringUtils.isEmpty(checkPointParam.getCheckpointingMode()) ||
                CheckpointingMode.EXACTLY_ONCE.name().equalsIgnoreCase(checkPointParam.getCheckpointingMode())) {
            checkpointConfig.setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
            log.info("本次CheckpointingMode模式 精确一次 即exactly-once");
        } else {
            checkpointConfig.setCheckpointingMode(CheckpointingMode.AT_LEAST_ONCE);
            log.info("本次CheckpointingMode模式 至少一次 即AT_LEAST_ONCE");
        }

        //默认超时10 minutes.
        checkpointConfig.setCheckpointTimeout(checkPointParam.getCheckpointTimeout());
        //确保检查点之间有至少500 ms的间隔【checkpoint最小间隔】
        checkpointConfig.setMinPauseBetweenCheckpoints(500);
        //同一时间只允许进行一个检查点
        checkpointConfig.setMaxConcurrentCheckpoints(2);

        //设置失败次数
        checkpointConfig.setTolerableCheckpointFailureNumber(checkPointParam.getTolerableCheckpointFailureNumber());

        //设置后端状态
        setStateBackend(env, checkPointParam);

        //检查点在作业取消后的保留策略，DELETE_ON_CANCELLATION代表删除，RETAIN_ON_CANCELLATION代表保留
        if (checkPointParam.getExternalizedCheckpointCleanup() != null) {
            if (checkPointParam.getExternalizedCheckpointCleanup().
                    equalsIgnoreCase(ExternalizedCheckpointCleanup.DELETE_ON_CANCELLATION.name())) {
                env.getCheckpointConfig()
                        .enableExternalizedCheckpoints(ExternalizedCheckpointCleanup.DELETE_ON_CANCELLATION);
                log.info("本次使用DELETE_ON_CANCELLATION代表删除");
            } else if (checkPointParam.getExternalizedCheckpointCleanup().
                    equalsIgnoreCase(ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION.name())) {
                env.getCheckpointConfig()
                        .enableExternalizedCheckpoints(ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);
                log.info("本次使用RETAIN_ON_CANCELLATION代表保留");
            }
        }

    }

    private static void setStateBackend(StreamExecutionEnvironment env, CheckPointParam checkPointParam) throws IOException {
        switch (checkPointParam.getStateBackendEnum()) {
            case MEMORY:
                log.info("开启MEMORY模式");
                env.setStateBackend(new MemoryStateBackend(MemoryStateBackend.DEFAULT_MAX_STATE_SIZE * 100));
                break;
            case FILE:
                log.info("开启FILE模式");
                if (checkPointParam.getAsynchronousSnapshots() != null) {
                    env.setStateBackend(new FsStateBackend(checkPointParam.getCheckpointDir(),
                            checkPointParam.getAsynchronousSnapshots()));
                } else {
                    env.setStateBackend(new FsStateBackend(checkPointParam.getCheckpointDir()));
                }
                break;
            case ROCKSDB:
                log.info("开启ROCKSDB模式");
                if (checkPointParam.getEnableIncremental() != null) {
                    env.setStateBackend(new RocksDBStateBackend(checkPointParam.getCheckpointDir(),
                            checkPointParam.getEnableIncremental()));
                } else {
                    env.setStateBackend(new RocksDBStateBackend(checkPointParam.getCheckpointDir()));
                }
                break;
            default:
                throw new RuntimeException("不支持这种后端状态" + checkPointParam.getStateBackendEnum());

        }
    }

}
