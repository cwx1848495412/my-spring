package com.test.service;

import com.spring.BeanPostProcessor;
import com.spring.annotation.Component;

@Component
public class MyBeanPostProcessor implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws Exception {
        System.out.printf("My postProcessBeforeInitialization %s%n", "初始化前");
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws Exception {
        System.out.printf("My postProcessAfterInitialization %s%n", "初始化后");
        return bean;
    }
}
