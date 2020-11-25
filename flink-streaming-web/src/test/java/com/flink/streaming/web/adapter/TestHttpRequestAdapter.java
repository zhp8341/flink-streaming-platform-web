package com.flink.streaming.web.adapter;

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
public class TestHttpRequestAdapter extends TestRun {

    @Autowired
    private HttpRequestAdapter httpRequestAdapter;


    @Test
    public void getAppId() {
        String appId = httpRequestAdapter.getAppIdByYarn("com.test.rt.demo.Kafka2Mysql","streaming");
        System.out.println(appId);
    }

    @Test
    public void getJobStateByJobId() {
        YarnStateEnum yarnStateEnum = httpRequestAdapter.getJobStateByJobId("application_1592398631005_10004");
        System.out.println(yarnStateEnum);
    }
}
