package com.flink.streaming.web.interceptor;

import com.flink.streaming.web.enums.SysConfigEnum;
import com.flink.streaming.web.service.SystemConfigService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
public class SysConfigInterceptor implements HandlerInterceptor {


    @Autowired
    private SystemConfigService systemConfigService;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        log.debug("进入SysConfigInterceptor拦截器 {}",request.getRequestURI());
        for (String key : SysConfigEnum.getMustKey()) {
            String value = systemConfigService.getSystemConfigByKey(key);
            if (StringUtils.isEmpty(value)) {
                response.sendRedirect("/admin/sysConfig");
                return false;
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {

    }


}
