package com.flink.streaming.web.controller.web;

import com.flink.streaming.web.ao.JobConfigAO;
import com.flink.streaming.web.enums.*;
import com.flink.streaming.web.model.dto.JobConfigDTO;
import com.flink.streaming.web.model.dto.PageModel;
import com.flink.streaming.web.model.param.JobConfigParam;
import com.flink.streaming.web.model.vo.DetailJobConfigVO;
import com.flink.streaming.web.model.vo.JobConfigVO;
import com.flink.streaming.web.model.vo.PageVO;
import com.flink.streaming.web.service.JobAlarmConfigService;
import com.flink.streaming.web.service.JobConfigService;
import com.flink.streaming.web.service.SystemConfigService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2020-08-16
 * @time 23:24
 */
@Controller
@RequestMapping("/admin")
@Slf4j
public class JobConfigController {

    @Autowired
    private JobConfigService jobConfigService;

    @Autowired
    private SystemConfigService systemConfigService;

    @Autowired
    public JobAlarmConfigService jobAlarmConfigService;
    
    @RequestMapping(value = "/listPage")
    public String listPage(ModelMap modelMap, JobConfigParam jobConfigParam) {
        if (jobConfigParam==null){
            jobConfigParam=new JobConfigParam();
        }
        jobConfigParam.setJobType(JobTypeEnum.SQL.getCode());
        this.list(modelMap, jobConfigParam);
        modelMap.put("active", "list");
        return "screen/job_config/listPage";
    }

    @RequestMapping(value = "/jarListPage")
    public String jarlistPage(ModelMap modelMap, JobConfigParam jobConfigParam) {
        if (jobConfigParam==null){
            jobConfigParam=new JobConfigParam();
        }
        jobConfigParam.setJobType(JobTypeEnum.JAR.getCode());
        this.list(modelMap, jobConfigParam);
        modelMap.put("active", "jarlist");
        return "screen/job_config/jarListPage";
    }

    @RequestMapping("/addPage")
    public String addPage(ModelMap modelMap) {
        modelMap.put("active", "addPage");
        modelMap.put("open", "config");
        return "screen/job_config/addPage";
    }


    @RequestMapping("/addJarPage")
    public String addJarPage(ModelMap modelMap) {
        modelMap.put("active", "addPage");
        modelMap.put("open", "config");
        return "screen/job_config/addJarPage";
    }


    @RequestMapping("/editPage")
    public String editPage(ModelMap modelMap, Long id) {
        JobConfigDTO jobConfigDTO = jobConfigService.getJobConfigById(id);
        modelMap.put("jobConfig", DetailJobConfigVO.toVO(jobConfigDTO));
        return "screen/job_config/editPage";
    }

    @RequestMapping("/editJarPage")
    public String editJarPage(ModelMap modelMap, Long id) {
        JobConfigDTO jobConfigDTO = jobConfigService.getJobConfigById(id);
        modelMap.put("jobConfig", DetailJobConfigVO.toVO(jobConfigDTO));
        return "screen/job_config/editJarPage";
    }


    @RequestMapping("/detailPage")
    public String detailPage(ModelMap modelMap, Long id) {
        JobConfigDTO jobConfigDTO = jobConfigService.getJobConfigById(id);
        if (jobConfigDTO == null) {
            modelMap.put("message", "数据不存在");
        } else {
            modelMap.put("jobConfig", DetailJobConfigVO.toVO(jobConfigDTO));
        }
        return "screen/job_config/detailPage";
    }



    private void list(ModelMap modelMap, JobConfigParam jobConfigParam){
        PageModel<JobConfigDTO> pageModel = jobConfigService.queryJobConfig(jobConfigParam);
        PageVO pageVO = new PageVO();
        pageVO.setPageNum(pageModel.getPageNum());
        pageVO.setPages(pageModel.getPages());
        pageVO.setPageSize(pageModel.getPageSize());
        pageVO.setTotal(pageModel.getTotal());
        modelMap.put("pageVO", pageVO);
        modelMap.put("jobConfigParam", jobConfigParam);
        List<JobConfigVO> jobConfigVOList = null;
        if (CollectionUtils.isEmpty(pageModel.getResult())) {
            jobConfigVOList = Collections.emptyList();
        } else {
            Map<DeployModeEnum, String> domainKey = new HashMap<>();
            domainKey.put(DeployModeEnum.YARN_PER,
                    systemConfigService.getSystemConfigByKey(SysConfigEnum.YARN_RM_HTTP_ADDRESS.getKey()));
            domainKey.put(DeployModeEnum.LOCAL,
                    systemConfigService.getSystemConfigByKey(SysConfigEnum.FLINK_REST_HTTP_ADDRESS.getKey()));
            domainKey.put(DeployModeEnum.STANDALONE,
                    systemConfigService.getSystemConfigByKey(SysConfigEnum.FLINK_REST_HA_HTTP_ADDRESS.getKey()));
            jobConfigVOList = JobConfigVO.toListVO(pageModel.getResult(), domainKey);

           List<Long> jobIdList=
                   jobConfigVOList.stream().map(jobConfigVO ->jobConfigVO.getId() ).collect(Collectors.toList());
            Map<Long ,List<AlarmTypeEnum>>  map=  jobAlarmConfigService.findByJobIdList(jobIdList);

            JobConfigVO.buildAlarm(jobConfigVOList,map);

        }
        modelMap.put("jobConfigList", jobConfigVOList);
        modelMap.put("open", "config");
    }

}
