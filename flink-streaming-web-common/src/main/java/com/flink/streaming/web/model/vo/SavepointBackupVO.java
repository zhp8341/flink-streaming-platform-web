package com.flink.streaming.web.model.vo;

import cn.hutool.core.collection.CollectionUtil;
import com.flink.streaming.web.common.util.DateFormatUtils;
import com.flink.streaming.web.model.dto.SavepointBackupDTO;
import lombok.Data;

import java.util.Collections;
import java.util.List;

/**
 * @author zhuhuipei
 * @date 2020-09-21
 * @time 00:02
 */
@Data
public class SavepointBackupVO {


  private Long id;
  /**
   * backup地址
   */
  private String savepointPath;


  private String backupTime;


  public static SavepointBackupVO toDTO(SavepointBackupDTO savepointBackupDTO) {
    if (savepointBackupDTO == null) {
      return null;
    }
    SavepointBackupVO savepointBackupVO = new SavepointBackupVO();
    savepointBackupVO.setId(savepointBackupDTO.getId());
    savepointBackupVO.setSavepointPath(savepointBackupDTO.getSavepointPath());
    savepointBackupVO
        .setBackupTime(DateFormatUtils.toFormatString(savepointBackupDTO.getBackupTime()));
    return savepointBackupVO;
  }

  public static List<SavepointBackupVO> toDTOList(List<SavepointBackupDTO> savepointBackupList) {
    if (CollectionUtil.isEmpty(savepointBackupList)) {
      return Collections.emptyList();
    }
    List<SavepointBackupVO> list = CollectionUtil.newArrayList();
    for (SavepointBackupDTO savepointBackupDTO : savepointBackupList) {
      list.add(toDTO(savepointBackupDTO));
    }
    return list;
  }

}
