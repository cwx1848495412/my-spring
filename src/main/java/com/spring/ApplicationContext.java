package com.spring;

import com.spring.annotation.Autowired;
import com.spring.annotation.Component;
import com.spring.annotation.ComponentScan;
import com.spring.annotation.Scope;

import java.io.File;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ApplicationContext {

    private Class configClass;

    /**
     * 单例池
     */
    private ConcurrentHashMap<String, Object> singletonMap = new ConcurrentHashMap<>();

    /**
     * 所有的 BeanDefinition 对象池
     */
    private ConcurrentHashMap<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();

    /**
     * 处理器列表
     */
    private List<BeanPostProcessor> beanPostProcessorList = new LinkedList<>();

    public ApplicationContext(Class configClass) throws Exception {
        this.configClass = configClass;

        scan(configClass);

        for (Map.Entry<String, BeanDefinition> entry : beanDefinitionMap.entrySet()) {
            String beanName = entry.getKey();
            BeanDefinition beanDefinition = entry.getValue();
            if (!Constant.SINGLETON.equals(beanDefinition.getScope())) {
                continue;
            }
            // 单例bean
            Object bean = createBean(beanName, beanDefinition);
            singletonMap.put(beanName, bean);
        }


    }

    /**
     * 创建bean
     *
     * @param beanDefinition
     * @return
     */
    private Object createBean(String beanName, BeanDefinition beanDefinition) throws Exception {
        Class clazz = beanDefinition.getClazz();
        Object instance = clazz.getDeclaredConstructor().newInstance();

        // 依赖注入
        for (Field declaredField : clazz.getDeclaredFields()) {
            if (declaredField.isAnnotationPresent(Autowired.class)) {
                Object bean = getBean(declaredField.getName());
                // private 属性 需要设置反射可访问
                declaredField.setAccessible(true);
                declaredField.set(instance, bean);
            }
        }

        // Aware 回调
        if (instance instanceof BeanNameAware) {
            ((BeanNameAware) instance).setBeanName(beanName);
        }

        // 扩展机制
        // BeanPostProcessor
        // 初始化前
        for (BeanPostProcessor beanPostProcessor : beanPostProcessorList) {
            instance = beanPostProcessor.postProcessBeforeInitialization(instance, beanName);
        }

        // 初始化 回调
        if (instance instanceof InitializingBean) {
            ((InitializingBean) instance).afterPropertiesSet();
        }

        // 初始化后
        for (BeanPostProcessor beanPostProcessor : beanPostProcessorList) {
            instance = beanPostProcessor.postProcessAfterInitialization(instance, beanName);
        }


        return instance;
    }

    /**
     * 扫描
     *
     * @param configClass
     * @throws ClassNotFoundException
     */
    private void scan(Class configClass) throws Exception {
        // 解析配置类
        ComponentScan componentScanAnnotation = (ComponentScan) configClass.getDeclaredAnnotation(ComponentScan.class);
        String path = componentScanAnnotation.value();

        // Bootstrap    jre/lib
        // ext          jre/ext/lib
        // app          classpath/*
        //              ;D:\WorkSpace\my-spring\target\classes

        // app classLoader
        ClassLoader classLoader = ApplicationContext.class.getClassLoader();
        URL resource = classLoader.getResource(path.replace(".", "/"));

        File file = new File(resource.getFile());
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File f : files) {
                String fileName = f.getAbsolutePath();
                if (!fileName.endsWith(".class")) {
                    continue;
                }

                String className = fileName.substring(fileName.indexOf("com"), fileName.indexOf(".class"));
                className = className.replace("\\", ".");

                Class<?> clazz = classLoader.loadClass(className);
                if (clazz.isAnnotationPresent(Component.class)) {

                    // 此处的处理器 没有使用getBean获取 方法的生命周期 此种形式不支持
                    // 可优化整体逻辑以供支持
                    if (BeanPostProcessor.class.isAssignableFrom(clazz)) {
                        BeanPostProcessor instance = (BeanPostProcessor) clazz.getDeclaredConstructor().newInstance();
                        beanPostProcessorList.add(instance);
                    }

                    // 判断 当前类是单例bean 还是 原型bean
                    // beanDefinition
                    Component componentAnnotation = clazz.getDeclaredAnnotation(Component.class);
                    String beanName = componentAnnotation.value();

                    BeanDefinition beanDefinition = new BeanDefinition();
                    beanDefinition.setClazz(clazz);
                    if (clazz.isAnnotationPresent(Scope.class)) {
                        Scope scopeAnnotation = clazz.getDeclaredAnnotation(Scope.class);
                        beanDefinition.setScope(scopeAnnotation.value());
                    } else {
                        beanDefinition.setScope(Constant.SINGLETON);
                    }

                    beanDefinitionMap.put(beanName, beanDefinition);

                }
            }
        }
    }

    /**
     * 根据bean定义确定要返回单例bean还是多例
     *
     * @param beanName
     * @return
     */
    public Object getBean(String beanName) throws Exception {
        if (!beanDefinitionMap.containsKey(beanName)) {
            throw new RuntimeException("Bean 定义不存在");
        }

        Object obj = null;
        BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
        if (Constant.SINGLETON.equals(beanDefinition.getScope())) {
            obj = singletonMap.get(beanName);
        } else {
            //创建bean
            obj = createBean(beanName, beanDefinition);
        }

        return obj;
    }
}
