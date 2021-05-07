package com.flink.streaming.web.controller.web;

import com.flink.streaming.web.config.CustomConfig;
import com.flink.streaming.web.enums.YN;
import com.flink.streaming.web.model.dto.JobRunLogDTO;
import com.flink.streaming.web.model.dto.PageModel;
import com.flink.streaming.web.model.param.JobRunLogParam;
import com.flink.streaming.web.model.vo.JobRunLogVO;
import com.flink.streaming.web.model.vo.PageVO;
import com.flink.streaming.web.service.JobRunLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2020-08-17
 * @time 19:04
 */
@Controller
@RequestMapping("/admin")
@Slf4j
public class JobRunLogController extends BaseController {

    @Autowired
    private JobRunLogService jobRunLogService;

    @Autowired
    private CustomConfig customConfig;


    @RequestMapping(value = "/logList")
    public String sysConfig(ModelMap modelMap, JobRunLogParam jobRunLogParam) {
        PageModel<JobRunLogDTO> pageModel = jobRunLogService.queryJobRunLog(jobRunLogParam);
        PageVO pageVO = new PageVO();
        pageVO.setPageNum(pageModel.getPageNum());
        pageVO.setPages(pageModel.getPages());
        pageVO.setPageSize(pageModel.getPageSize());
        pageVO.setTotal(pageModel.getTotal());
        modelMap.put("pageVO", pageVO);
        modelMap.put("jobRunLogParam", jobRunLogParam);
        modelMap.put("jobRunLogList", JobRunLogVO.toListVO(pageModel.getResult(), YN.N.getCode()));
        modelMap.put("active", "logList");
        modelMap.put("open", "log");
        return "screen/job_log/listPage";
    }


    @RequestMapping(value = "/detailLog")
    public String sysConfig(ModelMap modelMap, Long id) {
        modelMap.put("jobRunLogDetail", JobRunLogVO.toVO(jobRunLogService.getDetailLogById(id),
                YN.Y.getCode(),customConfig.getWebPort()));
        return "screen/job_log/detailLogPage";
    }
}
