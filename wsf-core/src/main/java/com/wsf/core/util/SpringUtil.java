package com.wsf.core.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;

import java.util.Map;

public final class SpringUtil {

    private static ApplicationContext applicationContext;

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static void setApplicationContext(ApplicationContext context) throws BeansException {
        SpringUtil.applicationContext = context;
    }

    /**
     * 判断spring加载了指定名称的bean
     *
     * @param name
     * @return
     */
    public static boolean containsBean(String name) {
        return applicationContext.containsBean(name);
    }

    /**
     * 获取对象
     *
     * @param name
     * @return Object 一个以所给名字注册的bean的实例
     * @throws BeansException
     */
    @SuppressWarnings("unchecked")
    public static <T> T getBean(String name) throws BeansException {
        return (T) applicationContext.getBean(name);
    }

    public static <T> T getBean(String name, Class<T> classes) {
        try {
            return (T) applicationContext.getBean(name);
        } catch (Exception e) {
        }
        return null;
    }

    public static <T> T getBean(Class<T> classes) {
        return applicationContext.getBean(classes);
    }

    /**
     * 根据type获取Bean集合
     *
     * @param type
     * @return <name, object>集合
     */
    public static <T> Map<String, T> getBeanOfType(Class<T> type) {
        return applicationContext.getBeansOfType(type);
    }

}