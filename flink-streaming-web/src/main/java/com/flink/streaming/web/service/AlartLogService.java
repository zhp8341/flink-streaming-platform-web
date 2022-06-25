package com.flink.streaming.web.service;

import com.flink.streaming.web.model.dto.AlartLogDTO;
import com.flink.streaming.web.model.dto.PageModel;
import com.flink.streaming.web.model.param.AlartLogParam;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2020-09-25
 * @time 21:43
 */
public interface AlartLogService {


  /**
   * 新增日志
   *
   * @author zhuhuipei
   * @date 2020-09-25
   * @time 21:49
   */
  void addAlartLog(AlartLogDTO alartLogDTO);


  /**
   * 按照id查询
   *
   * @author zhuhuipei
   * @date 2020-09-25
   * @time 21:49
   */
  AlartLogDTO findLogById(Long id);


  /**
   * 按照条件查询
   *
   * @author zhuhuipei
   * @date 2020-09-25
   * @time 21:51
   */
  PageModel<AlartLogDTO> queryAlartLog(AlartLogParam alartLogParam);

}
