package com.test;

import com.spring.ApplicationContext;

public class Test {
    public static void main(String[] args) throws Exception {
        ApplicationContext applicationContext = new ApplicationContext(AppConfig.class);
        System.out.println(applicationContext.getBean("userService"));
        System.out.println(applicationContext.getBean("userService"));
        System.out.println(applicationContext.getBean("userService"));
        System.out.println(applicationContext.getBean("userService"));
    }
}
