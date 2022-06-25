package com.flink.streaming.web.exceptions;

import com.flink.streaming.web.common.RestResult;
import com.flink.streaming.web.enums.SysErrorEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2020-07-13
 * @time 21:59
 */
@ControllerAdvice
@Slf4j
public class GlobalException {

  /**
   * 处理自定义的业务异常
   *
   * @param req
   * @param e
   * @return
   */
  @ExceptionHandler(value = BizException.class)
  @ResponseBody
  public RestResult bizExceptionHandler(HttpServletRequest req, BizException e) {
    log.error("发生业务异常！原因是：{}", e);
    return RestResult.error(e.getCode(), e.getErrorMsg());
  }

  /**
   * 处理空指针的异常
   *
   * @param req
   * @param e
   * @return
   */
  @ExceptionHandler(value = NullPointerException.class)
  @ResponseBody
  public RestResult exceptionHandler(HttpServletRequest req, NullPointerException e) {
    log.error("发生空指针异常！原因是:", e);
    return RestResult.error(SysErrorEnum.BODY_NOT_MATCH);
  }


  /**
   * 处理其他异常
   *
   * @param req
   * @param e
   * @return
   */
  @ExceptionHandler(value = Exception.class)
  @ResponseBody
  public RestResult exceptionHandler(HttpServletRequest req, Exception e) {
    log.error("未知异常！原因是:", e);
    return RestResult.error(SysErrorEnum.INTERNAL_SERVER_ERROR);
  }
}
