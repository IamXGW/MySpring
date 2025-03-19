package com.iamxgw.service;

import com.spring.Autowired;
import com.spring.Component;
import com.spring.InitializingBean;

/**
 * @author: IamXGW
 * @create: 2025-03-18 20:28
 */
//@Scope("prototype")
@Component("userService")
public class UserService implements InitializingBean, UserInterface {

    @Autowired
    OrderService orderService;

    @Override
    public void afterPropertiesSet() {
        System.out.println("初始化");
    }

    @Override
    public void test() {
        System.out.println("UserService::test");
    }
}