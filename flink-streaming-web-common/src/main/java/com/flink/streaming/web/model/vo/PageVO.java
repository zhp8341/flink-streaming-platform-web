package com.flink.streaming.web.model.vo;

import lombok.Data;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2020-08-05
 * @time 22:43
 */
@Data
public class PageVO<T> {

  /**
   * 当前页码
   */
  private int pageNum;

  /**
   * 每页数量
   */
  private int pageSize;

  /**
   * 页码
   */
  private int pages;

  /**
   * 总条数
   */
  private long total;


  private T data;
}
