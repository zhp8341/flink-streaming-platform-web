package com.flink.streaming.web.model.to;

import lombok.Data;

import java.util.List;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2020-08-06
 * @time 02:04
 */
@Data
public class AppListTO {

  private List<AppTO> app;
}
