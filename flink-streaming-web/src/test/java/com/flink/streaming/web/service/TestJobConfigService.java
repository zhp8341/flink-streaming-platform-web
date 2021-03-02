package com.flink.streaming.web.service;

import com.flink.streaming.web.base.TestRun;
import com.flink.streaming.web.enums.DeployModeEnum;
import com.flink.streaming.web.enums.JobConfigStatus;
import com.flink.streaming.web.enums.YN;
import com.flink.streaming.web.model.dto.JobConfigDTO;
import com.flink.streaming.web.model.dto.PageModel;
import com.flink.streaming.web.model.param.JobConfigParam;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2020-07-14
 * @time 02:23
 */
public class TestJobConfigService extends TestRun {

    @Autowired
    private JobConfigService jobConfigService;


    @Test
    public void add() {
        for (int i = 2; i <20 ; i++) {
            JobConfigDTO jobConfigDTO = new JobConfigDTO();
            jobConfigDTO.setFlinkCheckpointConfig("FlinkCheckpointConfig");
            jobConfigDTO.setFlinkRunConfig("FlinkRunConfig");
            jobConfigDTO.setFlinkSql("sql");
            jobConfigDTO.setDeployModeEnum(DeployModeEnum.STANDALONE);
            jobConfigDTO.setIsOpen(YN.N.getValue());
            jobConfigDTO.setJobName("job_name_"+i);
            jobConfigDTO.setStatus(JobConfigStatus.STOP);
            jobConfigDTO.setCreator("sys");
            jobConfigDTO.setEditor("sys");
            jobConfigService.addJobConfig(jobConfigDTO);
        }

        //Assert.assertNotNull(jobConfigDTO.getId());
    }


    @Test
    public void updae() {
        JobConfigDTO jobConfigDTO = new JobConfigDTO();
        jobConfigDTO.setFlinkCheckpointConfig("FlinkCheckpointConfig1");
        jobConfigDTO.setFlinkRunConfig("FlinkRunConfig1");
        jobConfigDTO.setFlinkSql("sql1");
        jobConfigDTO.setDeployModeEnum(DeployModeEnum.STANDALONE);
        jobConfigDTO.setIsOpen(YN.N.getValue());
        jobConfigDTO.setJobName("job_name1");
        jobConfigDTO.setStatus(JobConfigStatus.STOP);
        jobConfigDTO.setCreator("sys1");
        jobConfigDTO.setEditor("sys1");
        jobConfigDTO.setId(1L);
        jobConfigService.updateJobConfigById(jobConfigDTO);

    }

    @Test
    public void getJobConfigById() {
        JobConfigDTO jobConfigDTO = jobConfigService.getJobConfigById(1L);
        System.out.println(jobConfigDTO);
        Assert.assertNotNull(jobConfigDTO);
    }

    @Test
    public void openOrClose() {
        jobConfigService.openOrClose(1L, YN.Y, "test");

    }

    @Test
    public void delete() {
        jobConfigService.deleteJobConfigById(1L, "test1");
    }

    @Test
    public void findJobConfigByStatus() {
        List<JobConfigDTO> jobConfigDTOList = jobConfigService.findJobConfigByStatus(JobConfigStatus.RUN.getCode(),JobConfigStatus.STOP.getCode());
        System.out.println(jobConfigDTOList);
    }

    @Test
    public void queryJobConfig(){
        JobConfigParam jobConfigParam=new JobConfigParam();
        jobConfigParam.setPageNum(2);
        PageModel<JobConfigDTO> pageModel = jobConfigService.queryJobConfig(jobConfigParam);
        Assert.assertNotNull(pageModel);
    }
}
