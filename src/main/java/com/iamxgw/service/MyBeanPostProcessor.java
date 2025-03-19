package com.iamxgw.service;

import com.spring.BeanPostProcessor;
import com.spring.Component;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @description:
 * @author: IamXGW
 * @create: 2025-03-19 21:39
 */
@Component
public class MyBeanPostProcessor implements BeanPostProcessor {
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        if ("userService".equals(beanName)) {
            Object proxyInstance = Proxy.newProxyInstance(MyBeanPostProcessor.class.getClassLoader(), bean.getClass().getInterfaces(), new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    System.out.println("切面逻辑");
                    return method.invoke(bean, args);
                }
            });
            return proxyInstance;
        }
        return bean;
    }
}