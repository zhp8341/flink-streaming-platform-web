package com.flink.streaming.web.controller.web;

import com.flink.streaming.web.enums.JobConfigStatus;
import com.flink.streaming.web.enums.YN;
import com.flink.streaming.web.model.dto.JobConfigDTO;
import com.flink.streaming.web.model.dto.SavepointBackupDTO;
import com.flink.streaming.web.model.vo.SavepointBackupVO;
import com.flink.streaming.web.service.JobConfigService;
import com.flink.streaming.web.service.SavepointBackupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2020-09-21
 * @time 01:52
 */
@Controller
@RequestMapping("/admin")
@Slf4j
public class SavepointController {

    @Autowired
    private SavepointBackupService savepointBackupService;

    @Autowired
    private JobConfigService jobConfigService;


    @RequestMapping(value = "/savepointList")
    public String savepointList(ModelMap modelMap, Long jobConfigId) {
        List<SavepointBackupDTO> savepointBackupDTOList = savepointBackupService.lasterHistory10(jobConfigId);
        modelMap.put("savepointList", SavepointBackupVO.toDTOList(savepointBackupDTOList));
        JobConfigDTO jobConfigDTO = jobConfigService.getJobConfigById(jobConfigId);
        if (jobConfigDTO != null && JobConfigStatus.RUN.getCode().intValue() != jobConfigDTO.getStatus().getCode().intValue()
                && YN.getYNByValue(jobConfigDTO.getIsOpen()).getCode()) {
            modelMap.put("startButton", true);
        } else {
            modelMap.put("startButton", false);
        }
        modelMap.put("jobConfigId", jobConfigId);

        return "screen/savepoint/listPage";
    }

}
