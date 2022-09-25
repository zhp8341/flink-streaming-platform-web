package com.flink.streaming.web.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.flink.streaming.web.common.RestResult;
import com.flink.streaming.web.config.CustomConfig;
import com.flink.streaming.web.controller.web.BaseController;
import com.flink.streaming.web.enums.YN;
import com.flink.streaming.web.model.dto.JobRunLogDTO;
import com.flink.streaming.web.model.dto.PageModel;
import com.flink.streaming.web.model.param.JobRunLogParam;
import com.flink.streaming.web.model.vo.JobRunLogVO;
import com.flink.streaming.web.model.vo.PageVO;
import com.flink.streaming.web.service.JobRunLogService;

/**
 * 日志API
 *
 * @author wxj
 * @version V1.0
 * @date 2021年12月13日 下午4:16:29
 */
@RestController
@RequestMapping("/api")
public class JobLogApiController extends BaseController {

  @Autowired
  private JobRunLogService jobRunLogService;

  @Autowired
  private CustomConfig customConfig;


  /**
   * 查询作业列表
   *
   * @param jobRunLogParam
   * @return
   * @author wxj
   * @date 2021年12月1日 下午5:09:06
   * @version V1.0
   */
  @RequestMapping(value = "/logList", method = {RequestMethod.POST})
  public RestResult<?> listTask(JobRunLogParam jobRunLogParam) {
    PageModel<JobRunLogDTO> pageModel = jobRunLogService.queryJobRunLog(jobRunLogParam);
    PageVO<PageModel<JobRunLogDTO>> pageVO = new PageVO<PageModel<JobRunLogDTO>>();
    pageVO.setPageNum(pageModel.getPageNum());
    pageVO.setPages(pageModel.getPages());
    pageVO.setPageSize(pageModel.getPageSize());
    pageVO.setTotal(pageModel.getTotal());
    pageVO.setData(pageModel);
    return RestResult.success(pageVO);
  }

  /**
   * 查询日志详情
   *
   * @param modelMap
   * @param id
   * @return
   * @author wxj
   * @date 2021年12月14日 上午9:35:20
   * @version V1.0
   */
  @RequestMapping(value = "/logDetail", method = {RequestMethod.POST})
  public RestResult<?> sysConfig(ModelMap modelMap, Long logid) {
    JobRunLogVO vo = JobRunLogVO
        .toVO(jobRunLogService.getDetailLogById(logid), YN.Y.getCode(), customConfig.getWebPort());
    return RestResult.success(vo);
  }
}
