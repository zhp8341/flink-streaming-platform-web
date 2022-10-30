package com.flink.streaming.web.factory;


import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2022/10/29
 */

@Component
public final class  ApplicationContextProvider implements ApplicationContextAware {

  private static ApplicationContext context;

  private ApplicationContextProvider() {
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    context = applicationContext;
  }

  public static <T> T getBean(Class<T> aClass) {
    return context.getBean(aClass);
  }
}