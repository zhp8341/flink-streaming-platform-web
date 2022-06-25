package com.flink.streaming.web.rpc.model;

import lombok.Data;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2020-09-18
 * @time 23:50
 */
@Data
public class JobStandaloneInfo {

  private String jid;

  private String state;

  private String errors;


}
