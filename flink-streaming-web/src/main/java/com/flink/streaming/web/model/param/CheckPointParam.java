package com.flink.streaming.web.model.param;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import lombok.Data;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2020-08-21
 * @time 23:16
 */
@Data
public class CheckPointParam {

    //默认60S
    private Long checkpointInterval;

    //默认CheckpointingMode.EXACTLY_ONCE
    private String checkpointingMode;

    //默认超时10 minutes.
    private Long checkpointTimeout;

    private String checkpointDir;

    //设置失败次数 默认一次
    Integer tolerableCheckpointFailureNumber;

    //是否异步
    Boolean asynchronousSnapshots;

    //作业取消时，检查点是否保留，true代表为DELETE_ON_CANCELLATION，false代表为RETAIN_ON_CANCELLATION
    private String externalizedCheckpointCleanup;

    public static void main(String[] args) {
        CheckPointParam checkPointParam = new CheckPointParam();
        System.out.println(JSON.toJSONString(checkPointParam, SerializerFeature.WriteMapNullValue));

    }

}
