package com.flink.streaming.web.utils;

import com.flink.streaming.web.common.RestResult;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WebUtil {


  /**
   * 判断是否为Ajax请求
   *
   * @param request
   * @return
   * @author wxj
   * @date 2020年8月7日 上午10:09:03
   * @version V1.0
   */
  public static boolean isAjaxRequest(HttpServletRequest request) {
    return "XMLHttpRequest".equalsIgnoreCase(request.getHeader("X-Requested-With"));
  }

  /**
   * 响应修改为Restful风格返回Json
   *
   * @param response
   * @param status
   * @param message
   * @author wxj
   * @date 2020年8月7日 上午10:31:45
   * @version V1.0
   */
  public static void restResponse(HttpServletResponse response, String status, String message) {
    RestResult<Object> respone = RestResult.newInstance(status, message, null);
    restResponse(response, respone);
  }

  /**
   * 响应修改为Restful风格返回Json
   *
   * @param response
   * @param status
   * @param message
   * @author wxj
   * @date 2020年8月7日 上午10:33:08
   * @version V1.0
   */
  public static void restResponseWithFlush(HttpServletResponse response, String status,
      String message) {
    restResponse(response, status, message);
    flush(response);
  }

  /**
   * 响应修改为Restful风格返回Json
   *
   * @param response
   * @param status
   * @param message
   * @param data
   * @author wxj
   * @date 2020年8月7日 上午10:33:12
   * @version V1.0
   */
  public static void restResponse(HttpServletResponse response, String status, String message,
      Object data) {
    RestResult<Object> respone = RestResult.newInstance(status, message, data);
    restResponse(response, respone);
  }

  /**
   * 响应修改为Restful风格返回Json
   *
   * @param response
   * @param status
   * @param message
   * @param data
   * @author wxj
   * @date 2020年8月7日 上午10:33:16
   * @version V1.0
   */
  public static void restResponseWithFlush(HttpServletResponse response, String status,
      String message, Object data) {
    restResponse(response, status, message, data);
    flush(response);
  }

  /**
   * 响应修改为Restful风格返回Json
   *
   * @param response
   * @param data
   * @author wxj
   * @date 2020年8月7日 上午10:33:20
   * @version V1.0
   */
  public static void restResponse(HttpServletResponse response, RestResult<?> data) {
    try {
      response.setContentType("application/json;charset=utf-8");
      PrintWriter out = response.getWriter();
      out.write(JacksonUtil.toJsonString(data));
      out.flush();
    } catch (IOException e) {
      log.error(e.getMessage(), e);
    }
  }

  /**
   * 响应修改为Restful风格返回Json
   *
   * @param response
   * @param data
   * @author wxj
   * @date 2020年8月7日 上午10:33:24
   * @version V1.0
   */
  public static void restResponseWithFlush(HttpServletResponse response, RestResult<?> data) {
    restResponse(response, data);
    flush(response);
  }

  /**
   * Flush Response，设置完成状态
   *
   * @param response
   * @author wxj
   * @date 2020年8月7日 上午10:33:31
   * @version V1.0
   */
  public static void flush(HttpServletResponse response) {
    try {
      response.getWriter().flush();
    } catch (IOException e) {
      log.error(e.getMessage(), e);
    }
  }
}
