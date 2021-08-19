package com.test.service;

import com.spring.BeanNameAware;
import com.spring.InitializingBean;
import com.spring.annotation.Autowired;
import com.spring.annotation.Component;

@Component("userService")
//@Scope("prototype")
public class UserService implements BeanNameAware, InitializingBean {

    @Autowired
    private OrderService orderService;

    private String beanName;

    private String beforePostProcessorStr;

    public void test() {
        System.out.println(beanName);
        System.out.println(orderService);
    }

    @Override
    public void setBeanName(String beanName) {
        this.beanName = String.format("%s BeanNameAware Override", beanName);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("UserService afterPropertiesSet Override");
    }
}
