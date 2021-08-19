package com.test.service;

import com.spring.annotation.Component;

@Component("orderService")
public class OrderService {

    private String afterPostProcessorStr;

    public void test() {
        System.out.println(afterPostProcessorStr);
    }
}
