package com.spring;

import java.beans.Introspector;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author: IamXGW
 * @create: 2025-03-18 20:27
 */
@SuppressWarnings("unchecked")
public class MyApplicationContext {
    private Class configClass;
    private Map<String, BeanDefinition> beanDefinitionMap = new HashMap<>();
    // 单例池
    private Map<String, Object> singletonObjects = new HashMap<>();

    private List<BeanPostProcessor> beanPostProcessorList = new LinkedList<>();

    public MyApplicationContext(Class configClass) {
        this.configClass = configClass;

        // 扫描，产生 beanDefinitionMap
        scan(configClass);

        // 创建单例 Bean
        for (Map.Entry<String, BeanDefinition> entry : beanDefinitionMap.entrySet()) {
            String beanName = entry.getKey();
            BeanDefinition beanDefinition = entry.getValue();
            String scope = beanDefinition.getScope();
            if (isSingletonBean(scope)) {
                Object bean = createBean(beanName, beanDefinition);
                singletonObjects.put(beanName, bean);
            }
        }
    }

    private void scan(Class configClass) {
        if (configClass.isAnnotationPresent(ComponentScan.class)) {
            ComponentScan configClassAnnotation = (ComponentScan) configClass.getAnnotation(ComponentScan.class);
            String path = configClassAnnotation.value();
            path = path.replace(".", "/");
            ClassLoader classLoader = MyApplicationContext.class.getClassLoader();
            URL resource = classLoader.getResource(path);
            File file = new File(resource.getFile());
            for (File f : file.listFiles()) {
                String absolutePath = f.getAbsolutePath();
                absolutePath = absolutePath.substring(absolutePath.indexOf("com"), absolutePath.lastIndexOf(".class"));
                absolutePath = absolutePath.replace("/", ".");
                try {
                    Class<?> clazz = classLoader.loadClass(absolutePath);
                    // 如果是 Bean
                    if (clazz.isAnnotationPresent(Component.class)) {
                        if (BeanPostProcessor.class.isAssignableFrom(clazz)) {
                            BeanPostProcessor beanPostProcessor = (BeanPostProcessor) clazz.getConstructor().newInstance();
                            beanPostProcessorList.add(beanPostProcessor);
                        }
                        Component componentAnnotation = clazz.getAnnotation(Component.class);
                        String beanName = componentAnnotation.value();
                        if ("".equals(beanName)) {
                            beanName = Introspector.decapitalize(clazz.getSimpleName());
                        }
                        BeanDefinition beanDefinition = new BeanDefinition();
                        beanDefinition.setType(clazz);
                        beanDefinition.setLazy(false);
                        // 判断 Bean 的类型
                        if (clazz.isAnnotationPresent(Scope.class)) {
                            Scope scopeAnnotation = clazz.getAnnotation(Scope.class);
                            String scope = scopeAnnotation.value();
                            beanDefinition.setScope(scope);
                        } else {
                            beanDefinition.setScope(BeanScopeEnum.SINGLETON.getValue());
                        }

                        beanDefinitionMap.put(beanName, beanDefinition);
                    }
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                } catch (InvocationTargetException e) {
                    throw new RuntimeException(e);
                } catch (InstantiationException e) {
                    throw new RuntimeException(e);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                } catch (NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }
            }



        }
    }

    public Object getBean(String beanName) {
        if (!beanDefinitionMap.containsKey(beanName)) {
            throw new NullPointerException();
        }
        BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
        String scope = beanDefinition.getScope();
        if (isSingletonBean(scope)) {
            // 单例 Bean
            Object singletonBean = singletonObjects.get(beanName);
            if (singletonBean == null) {
                singletonBean = createBean(beanName, beanDefinition);
                singletonObjects.put(beanName, singletonBean);
            }
            return singletonBean;
        } else {
            // 原型 Bean
            Object prototypeBean = createBean(beanName, beanDefinition);
            return prototypeBean;
        }
    }

    private Object createBean(String beanName, BeanDefinition beanDefinition) {
        Class clazz = beanDefinition.getType();
        Object instance = null;
        try {
            instance = clazz.getConstructor().newInstance();
            // 依赖注入
            for (Field field : instance.getClass().getDeclaredFields()) {
                if (field.isAnnotationPresent(Autowired.class)) {
                    field.setAccessible(true);
                    field.set(instance, getBean(field.getName()));
                }
            }

            // Bean 前处理器
            for (BeanPostProcessor beanPostProcessor : beanPostProcessorList) {
                instance = beanPostProcessor.postProcessBeforeInitialization(instance, beanName);
            }

            // 初始化
            if (instance instanceof InitializingBean) {
                ((InitializingBean) instance).afterPropertiesSet();
            }

            // Bean 后处理器
            for (BeanPostProcessor beanPostProcessor : beanPostProcessorList) {
                instance = beanPostProcessor.postProcessAfterInitialization(instance, beanName);
            }
        } catch (InstantiationException | IllegalAccessException
                 | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        return instance;
    }

    private boolean isSingletonBean(String scope) {
        return scope.equals(BeanScopeEnum.SINGLETON.getValue()) || scope.isEmpty();
    }

}

