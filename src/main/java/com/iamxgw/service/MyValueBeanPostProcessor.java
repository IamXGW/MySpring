package com.iamxgw.service;

import com.spring.BeanPostProcessor;
import com.spring.Component;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @description:
 * @author: IamXGW
 * @create: 2025-03-19 21:39
 */
@Component
public class MyValueBeanPostProcessor implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        Field[] fields = bean.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(MyValue.class)) {
                field.setAccessible(true);
                try {
                    field.set(bean, field.getAnnotation(MyValue.class).value());
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        return bean;
    }
}