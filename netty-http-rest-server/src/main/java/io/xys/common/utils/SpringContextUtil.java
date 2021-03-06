package com.ancun.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.NoSuchMessageException;

/**
 * 读取配置项工具类
 *
 * @Created on 2015-02-11
 * @author xys
 * @version 1.0
 * @Copyright:杭州安存网络科技有限公司 Copyright (c) 2015
 */
public class SpringContextUtil implements ApplicationContextAware {

    private static final Logger logger = LoggerFactory.getLogger(SpringContextUtil.class);

    /** Spring应用上下文环�?*/
    private static ApplicationContext applicationContext;

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        SpringContextUtil.applicationContext = applicationContext;
    }

    /**
     * 从静态变量applicationContext中取得Bean, 自动转型为所赋�?对象的类�?
     *
     * @param beanName
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T>T getBean(String beanName, Class<T> clazz){
        return applicationContext.getBean(beanName, clazz);
    }

    /**
     * 从静态变量applicationContext中取得Bean
     *
     * @param beanName
     * @return
     */
    public static Object getBean(String beanName){
        return applicationContext.getBean(beanName);
    }

    /**
     * 根据key，取得配置文件里相应的信�?
     *
     * @param key
     * @return
     */
    public static String getProperty(String key) {
        return SpringContextUtil.getMessage(key, new Object[0]);
    }

    /**
     * 根据key和相应参数取得配置文件里相应的信�?
     *
     * @param key
     * @param objects
     * @return
     */
    public static String getMessage(String key, Object...objects) {
        try {
            String m = applicationContext.getMessage(key, objects, null);
            return m;
        } catch (NoSuchMessageException nme) {
            nme.printStackTrace();
            logger.warn(nme.getMessage());
            return "";
        }
    }
}
