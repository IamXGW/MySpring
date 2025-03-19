package com.iamxgw;

import com.iamxgw.service.OrderService;
import com.iamxgw.service.UserInterface;
import com.iamxgw.service.UserService;
import com.spring.MyApplicationContext;

/**
 * @author xuguangwei
 */
public class Main {
    public static void main(String[] args) {
        MyApplicationContext context = new MyApplicationContext(AppConfig.class);
        UserInterface userService = (UserInterface) context.getBean("userService");
        userService.test();

        OrderService orderService = (OrderService) context.getBean("orderService");
        orderService.test();
    }
}