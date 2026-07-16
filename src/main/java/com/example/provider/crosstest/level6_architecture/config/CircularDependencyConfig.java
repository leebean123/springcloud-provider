package com.example.provider.crosstest.level6_architecture.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Level 6: 架构/设计 - L6-1 循环依赖
 *
 * 单文件方法拆分: ❌ 全漏。beanA() 看正常，beanB() 看正常，分开看不出循环。
 * OCR: ❌ 需要全局视图。
 */
@Configuration
public class CircularDependencyConfig {

    // L6-1: P1 - BeanA ↔ BeanB 构造期循环依赖
    // Spring 启动报 BeanCurrentlyInCreationException

    @Bean
    public BeanA beanA() {
        BeanA a = new BeanA();
        a.setBeanB(beanB());
        return a;
    }

    @Bean
    public BeanB beanB() {
        BeanB b = new BeanB();
        b.setBeanA(beanA());
        return b;
    }

    public static class BeanA {
        private BeanB beanB;
        public void setBeanB(BeanB beanB) { this.beanB = beanB; }
    }

    public static class BeanB {
        private BeanA beanA;
        public void setBeanA(BeanA beanA) { this.beanA = beanA; }
    }
}
