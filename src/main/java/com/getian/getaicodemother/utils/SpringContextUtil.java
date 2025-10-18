package com.getian.getaicodemother.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Spring上下文工具类
 * 用于在静态方法中获取Spring Bean
 */
@Component
public class SpringContextUtil implements ApplicationContextAware {
    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringContextUtil.applicationContext = applicationContext;
    }

    /**
     * 根据class类获取Bean
     * @param tClass
     * @return
     * @param <T>
     */
    public static <T> T getBean(Class<T> tClass){
        return applicationContext.getBean(tClass);
    }

    /**
     * 根据name获取Bean
     * @param name
     * @return
     */
    public static Object getBean(String name){
        return applicationContext.getBean(name);
    }

    /**
     * 根据name和class类获取Bean
     * @param name
     * @param tClass
     * @return
     * @param <T>
     */
    public static <T> T getBean(String name, Class<T> tClass){
        return applicationContext.getBean(name, tClass);
    }
}
