package com.flink.streaming.core.utils;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2020-08-19
 * @time 23:31
 */
public class LoadJarUtil {


    private static final Logger logger = LoggerFactory.getLogger(LoadJarUtil.class.getName());

    /**
     * 加载jar包
     *
     * @author zhuhuipei
     * @date 2020-09-28
     * @time 02:17
     */
    public static void loadJar(String jarPath, ClassLoader levelClassLoader) throws Exception {
        URL url = new URL(jarPath);
        if (null != url) {
            Method method = null;
            try {
                method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            } catch (NoSuchMethodException e) {
                logger.error("get method exception, jarPath:" + jarPath, e);
                throw e;
            }

            boolean accessible = method.isAccessible();
            try {
                method.setAccessible(true);
                method.invoke(levelClassLoader, url);
            } catch (Exception e) {
                logger.error("load url to classLoader exception, jarPath:" + jarPath, e);
                throw e;
            } finally {
                if (method != null) {
                    method.setAccessible(accessible);
                }

            }
        }
    }
}
