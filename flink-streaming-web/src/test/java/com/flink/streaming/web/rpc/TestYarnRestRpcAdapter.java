package com.flink.streaming.web.rpc;

import com.flink.streaming.web.base.TestRun;
import com.flink.streaming.web.enums.YarnStateEnum;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2020-08-06
 * @time 20:45
 */
public class TestYarnRestRpcAdapter extends TestRun {

    @Autowired
    private YarnRestRpcAdapter yarnRestRpcAdapter;


    @Test
    public void getAppId() {
        String appId = yarnRestRpcAdapter.getAppIdByYarn("com.test.rt.demo.Kafka2Mysql","streaming");
        System.out.println(appId);
    }

    @Test
    public void getJobStateByJobId() {
        YarnStateEnum yarnStateEnum = yarnRestRpcAdapter.getJobStateByJobId("application_1592398631005_10004");
        System.out.println(yarnStateEnum);
    }
}
