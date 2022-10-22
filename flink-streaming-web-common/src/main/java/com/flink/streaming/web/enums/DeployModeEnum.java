package com.flink.streaming.web.enums;

import com.flink.streaming.web.exceptions.BizException;
import org.apache.commons.lang3.StringUtils;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2020-08-15
 * @time 20:41
 */
public enum DeployModeEnum {
  YARN_PER, STANDALONE, LOCAL, YARN_APPLICATION;

  public static DeployModeEnum getModel(String model) {
    if (StringUtils.isEmpty(model)) {
      throw new BizException("运行模式不能为空");
    }
    for (DeployModeEnum deployModeEnum : DeployModeEnum.values()) {
      if (deployModeEnum.name().equals(model.trim().toUpperCase())) {
        return deployModeEnum;
      }

    }
    throw new BizException("运行模式不存在");
  }
}
