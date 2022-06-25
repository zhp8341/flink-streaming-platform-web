package com.flink.streaming.web.common.util;

import lombok.Data;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2020-07-15
 * @time 19:45
 */
@Data
public class Pages {

  /**
   * 当前页码
   */
  private int pageNum;
  /**
   * 每页数量
   */
  private int pageSize;
  /**
   * 记录总数
   */
  private long totalSize;
  /**
   * 页码总数
   */
  private int totalPages;
}
