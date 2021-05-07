package com.flink.streaming.web.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2020-07-06
 * @time 00:43
 */
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {


    @Autowired
    private LoginInterceptor loginInterceptor;


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //注册loginInterceptor拦截器
        InterceptorRegistration registration = registry.addInterceptor(loginInterceptor);
        //所有路径都被拦截
        registration.addPathPatterns("/**");
        //添加不拦截路径
        registration.excludePathPatterns("/static/**", "/static/*", "/admin/index",
                "/admin/qrcode", "/api/login",
                "/ok","/alarmCallback","/log/*","/favicon.ico");


    }
}
