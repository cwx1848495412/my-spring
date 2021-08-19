package com.spring;

public interface BeanPostProcessor {
    /**
     * 初始化前
     *
     * @param bean
     * @param beanName
     * @return
     * @throws Exception
     */
    Object postProcessBeforeInitialization(Object bean, String beanName) throws Exception;

    /**
     * 初始化后
     *
     * @param bean
     * @param beanName
     * @return
     * @throws Exception
     */
    Object postProcessAfterInitialization(Object bean, String beanName) throws Exception;
}
