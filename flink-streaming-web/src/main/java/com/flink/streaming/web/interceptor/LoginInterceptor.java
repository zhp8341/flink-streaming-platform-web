package com.flink.streaming.web.interceptor;

import com.flink.streaming.common.constant.SystemConstant;
import com.flink.streaming.web.common.RestResult;
import com.flink.streaming.web.common.util.UserSessionUtil;
import com.flink.streaming.web.model.dto.UserSession;
import com.flink.streaming.web.model.vo.Constant;
import com.flink.streaming.web.service.UserService;
import com.flink.streaming.web.utils.WebUtil;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2020-07-10
 * @time 01:27
 */
@Component
@Slf4j
public class LoginInterceptor implements HandlerInterceptor {


  @Autowired
  private UserService userService;


  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
      throws Exception {
    log.debug("进入LoginInterceptor拦截器 {}", request.getRequestURI());
    if (SystemConstant.VIRGULE .equals(request.getRequestURI())) {
      response.sendRedirect("/static/ui/index.html");
      return false;
    }
    UserSession userSession = UserSessionUtil.userSession(request);

    // ajax请求
    if (WebUtil.isAjaxRequest(request)) {
      boolean nologin = (userSession == null) || (!userService.checkLogin(userSession));
      if (nologin) {
        RestResult<Object> respdata = RestResult
            .newInstance(Constant.RESPONE_STATUS_UNAUTH, "未登录认证！", null);
        WebUtil.restResponseWithFlush(response, respdata);
        return false;
      }
      return true;
    }
    log.debug("未知请求={}", request.getRequestURI());
    return true;
  }

  @Override
  public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
      ModelAndView modelAndView) {
    UserSession userSession = UserSessionUtil.userSession(request);
    if (modelAndView != null && userSession != null) {
      modelAndView.addObject("user", userSession.getName());
    }

  }

  @Override
  public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
      Object handler, Exception ex) {

  }

}
