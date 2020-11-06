package com.flink.streaming.web.adapter;

import com.flink.streaming.web.base.TestRun;
import com.flink.streaming.web.model.flink.JobYarnInfo;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2020-09-18
 * @time 01:37
 */
public class TestFlinkHttpRequestAdapter extends TestRun {

    @Autowired
    private FlinkHttpRequestAdapter flinkHttpRequestAdapter;


    @Test
    public void jobs() {
        JobYarnInfo jobYarnInfo = flinkHttpRequestAdapter.getJobInfoForPerYarnByAppId("application_1592398631005_150001");
        System.out.println(jobYarnInfo);
    }

    @Test
    public void cancelJob() {
        flinkHttpRequestAdapter.cancelJobForYarnByAppId("application_1592398631005_15450", "2e0d9c6c4afda42c8b847cd4bddb029b");
    }


    @Test
    public void getSavepointPath() {
        flinkHttpRequestAdapter.getSavepointPath("application_1592398631005_15496", "f4b53cd1e62fa42eeaf7bebd979fd665");
    }
}
