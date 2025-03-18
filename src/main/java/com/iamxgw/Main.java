package com.iamxgw;

import com.spring.MyApplicationContext;

/**
 * @author xuguangwei
 */
public class Main {
    public static void main(String[] args) {
        MyApplicationContext context = new MyApplicationContext(AppConfig.class);
        System.out.println(context.getBean("userService"));
        System.out.println(context.getBean("userService"));
        System.out.println(context.getBean("userService"));
    }
}