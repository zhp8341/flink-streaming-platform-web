package com.flink.streaming.web.model.dto;

import cn.hutool.core.collection.CollectionUtil;
import com.flink.streaming.web.model.entity.SavepointBackup;
import lombok.Data;

import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author
 */
@Data
public class SavepointBackupDTO {

  private Long id;

  private Long jobConfigId;

  /**
   * backup地址
   */
  private String savepointPath;


  private Date backupTime;


  public static SavepointBackupDTO toDTO(SavepointBackup savepointBackup) {
    if (savepointBackup == null) {
      return null;
    }
    SavepointBackupDTO savepointBackupDTO = new SavepointBackupDTO();
    savepointBackupDTO.setId(savepointBackup.getId());
    savepointBackupDTO.setJobConfigId(savepointBackup.getJobConfigId());
    savepointBackupDTO.setSavepointPath(savepointBackup.getSavepointPath());
    savepointBackupDTO.setBackupTime(savepointBackup.getBackupTime());
    return savepointBackupDTO;
  }

  public static List<SavepointBackupDTO> toDTOList(List<SavepointBackup> savepointBackupList) {
    if (CollectionUtil.isEmpty(savepointBackupList)) {
      return Collections.emptyList();
    }
    List<SavepointBackupDTO> list = CollectionUtil.newArrayList();
    for (SavepointBackup savepointBackup : savepointBackupList) {
      list.add(toDTO(savepointBackup));
    }
    return list;
  }


}
