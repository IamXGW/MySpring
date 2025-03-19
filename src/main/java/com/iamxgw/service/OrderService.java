package com.iamxgw.service;

import com.spring.Component;

/**
 * @author: IamXGW
 * @create: 2025-03-18 20:28
 */
@Component
public class OrderService {

    @MyValue("MyValue")
    private String myValue;

    public void test() {
        System.out.println(myValue);
    }
}