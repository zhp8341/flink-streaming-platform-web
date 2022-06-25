package com.flink.streaming.web.service;

import com.flink.streaming.web.model.dto.SavepointBackupDTO;

import java.util.Date;
import java.util.List;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2020-09-17
 * @time 20:26
 */
public interface SavepointBackupService {

  /**
   * 新增
   *
   * @author zhuhuipei
   * @date 2020-09-17
   * @time 20:34
   */
  void insertSavepoint(Long jobConfigId, String savepointPath, Date backupTime);


  /**
   * 最近5条
   *
   * @author zhuhuipei
   * @date 2020-09-17
   * @time 20:34
   */
  List<SavepointBackupDTO> lasterHistory10(Long jobConfigId);


  /**
   * 获取SavepointPath详细地址
   *
   * @author zhuhuipei
   * @date 2020-09-21
   * @time 00:44
   */
  String getSavepointPathById(Long jobConfigId, Long id);

}
