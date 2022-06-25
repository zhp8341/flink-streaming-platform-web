package com.flink.streaming.web.model.vo;

import com.flink.streaming.web.enums.SysConfigEnum;
import com.flink.streaming.web.model.dto.SystemConfigDTO;
import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author zhuhuipei
 * @date 2020-07-20
 * @time 23:37
 */
@Data
public class SystemConfigVO {


  private Long id;

  private String desc;

  private String key;

  private String val;


  public static SystemConfigVO toVO(SystemConfigDTO systemConfigDTO) {
    if (systemConfigDTO == null) {
      return null;
    }
    SystemConfigVO systemConfigVO = new SystemConfigVO();
    systemConfigVO.setId(systemConfigDTO.getId());
    if (SysConfigEnum.getSysConfigEnum(systemConfigDTO.getKey()) != null) {
      systemConfigVO.setDesc(SysConfigEnum.getSysConfigEnum(systemConfigDTO.getKey()).getDesc());
    }
    systemConfigVO.setKey(systemConfigDTO.getKey());
    systemConfigVO.setVal(systemConfigDTO.getVal());
    return systemConfigVO;
  }

  public static List<SystemConfigVO> toListVO(List<SystemConfigDTO> systemConfigDTOList) {
    if (CollectionUtils.isEmpty(systemConfigDTOList)) {
      return Collections.emptyList();
    }
    List<SystemConfigVO> list = new ArrayList<>();
    for (SystemConfigDTO systemConfigDTO : systemConfigDTOList) {
      list.add(toVO(systemConfigDTO));
    }
    return list;
  }


}
