package com.flink.streaming.web.model.dto;

import com.flink.streaming.web.enums.SysConfigEnumType;
import com.flink.streaming.web.model.entity.SystemConfig;
import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author zhuhuipei
 * @date 2020-07-20
 * @time 23:37
 */
@Data
public class SystemConfigDTO implements Serializable {

  private static final long serialVersionUID = 1L;

  private Long id;

  private String key;

  private String val;

  private SysConfigEnumType sysConfigEnumType;


  public SystemConfigDTO() {

  }

  public SystemConfigDTO(String key, String val) {
    this.key = key;
    this.val = val;
  }

  public static SystemConfig toEntity(SystemConfigDTO systemConfigDTO) {
    if (systemConfigDTO == null) {
      return null;
    }
    SystemConfig systemConfig = new SystemConfig();
    systemConfig.setId(systemConfigDTO.getId());
    systemConfig.setKey(systemConfigDTO.getKey());
    systemConfig.setVal(systemConfigDTO.getVal());
    return systemConfig;
  }

  public static SystemConfigDTO toDTO(SystemConfig systemConfig) {
    if (systemConfig == null) {
      return null;
    }
    SystemConfigDTO systemConfigDTO = new SystemConfigDTO();
    systemConfigDTO.setId(systemConfig.getId());
    systemConfigDTO.setKey(systemConfig.getKey());
    systemConfigDTO.setVal(systemConfig.getVal());
    return systemConfigDTO;
  }

  public static List<SystemConfigDTO> toListDTO(List<SystemConfig> systemConfigList) {
    if (CollectionUtils.isEmpty(systemConfigList)) {
      return Collections.emptyList();
    }
    List<SystemConfigDTO> list = new ArrayList();
    for (SystemConfig systemConfig : systemConfigList) {
      list.add(toDTO(systemConfig));
    }
    return list;
  }

  public static Map<String, String> toMap(List<SystemConfigDTO> systemConfigDTOList) {
    if (CollectionUtils.isEmpty(systemConfigDTOList)) {
      return Collections.EMPTY_MAP;
    }

    return systemConfigDTOList.stream().collect(Collectors.toMap(SystemConfigDTO::getKey,
        SystemConfigDTO::getVal, (key1, key2) -> key2));

  }


}
