package com.flink.streaming.web.model.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2020-07-15
 * @time 23:24
 */
@Data
public class PageModel<E> extends ArrayList<E> {

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

  public List<E> getResult() {
    return this;
  }


}
