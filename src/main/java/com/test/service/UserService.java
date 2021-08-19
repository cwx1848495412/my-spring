package com.test.service;

import com.spring.annotation.Autowired;
import com.spring.annotation.Component;

@Component("userService")
//@Scope("prototype")
public class UserService {

    @Autowired
    private OrderService orderService;

    public void test(){
        System.out.println(orderService);
    }
}
