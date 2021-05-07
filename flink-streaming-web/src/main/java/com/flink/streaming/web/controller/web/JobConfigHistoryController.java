package com.flink.streaming.web.controller.web;

import com.flink.streaming.web.model.dto.JobConfigHistoryDTO;
import com.flink.streaming.web.model.vo.JobConfigHistoryVO;
import com.flink.streaming.web.service.JobConfigHistoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2020-08-16
 * @time 23:24
 */
@Controller
@RequestMapping("/admin")
@Slf4j
public class JobConfigHistoryController {

    @Autowired
    private JobConfigHistoryService jobConfigHistoryService;


    @RequestMapping(value = "/jobConfigHistoryPage")
    public String listPage(ModelMap modelMap, Long jobConfigId) {
        if (jobConfigId==null){
            modelMap.put("message", "jobConfigId参数不能为空");
            return "screen/job_config_history/listPage";
        }
        modelMap.put("jobConfigId", jobConfigId);
        List<JobConfigHistoryDTO> list = jobConfigHistoryService.getJobConfigHistoryByJobConfigId(jobConfigId);
        modelMap.put("jobConfigHistoryList", JobConfigHistoryVO.toListVO(list));
        return "screen/job_config_history/listPage";
    }


    @RequestMapping("/jobConfigHistoryDetailPage")
    public String detailPage(ModelMap modelMap, Long id) {
        JobConfigHistoryDTO jobConfigHistoryDTO= jobConfigHistoryService.getJobConfigHistoryById(id);
        modelMap.put("jobConfigHistory", JobConfigHistoryVO.toVO(jobConfigHistoryDTO,Boolean.TRUE));
        return "screen/job_config_history/detailPage";
    }


}
