package com.flink.streaming.web.rpc;

import com.flink.streaming.web.base.TestRun;
import com.flink.streaming.web.enums.DeployModeEnum;
import com.flink.streaming.web.rpc.model.JobInfo;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2020-09-18
 * @time 01:37
 */
public class TestFlinkYarnRestRpcAdapter extends TestRun {

    @Autowired
    private YarnRestRpcAdapter yarnRestRpcAdapter;

    @Autowired
    private FlinkRestRpcAdapter flinkRestRpcAdapter;


    @Test
    public void jobs() {
        JobInfo jobInfo = yarnRestRpcAdapter.getJobInfoForPerYarnByAppId("application_1592398631005_150001");
        System.out.println(jobInfo);
    }



    @Test
    public void cancelJob() {
        yarnRestRpcAdapter.cancelJobForYarnByAppId("application_1592398631005_15450", "2e0d9c6c4afda42c8b847cd4bddb029b");
    }


    @Test
    public void getSavepointPath() {
        yarnRestRpcAdapter.getSavepointPath("application_1592398631005_15496", "f4b53cd1e62fa42eeaf7bebd979fd665");
    }

    @Test
    public void savepoint() {
       String savepointPath= flinkRestRpcAdapter.savepointPath("d4951885fc4e6b7ccb774c4aae1d3415",
               DeployModeEnum.LOCAL);
       System.out.println(savepointPath);
    }
}
