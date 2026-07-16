package com.example.provider.crosstest.level6_architecture.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Level 6: 架构/设计 - L6-3 事务注解失效（本类调用）
 *
 * 单文件方法拆分: ❌ 漏判。process() 调 createOrder() + deduct()，流程正常。
 * OCR: ❌ 需要理解 Spring AOP 代理机制。
 *
 * 问题：@Transactional 在 createOrder() 上，但 process() 在同一类中直接调用了它。
 * Spring 声明式事务基于 AOP 代理，本类方法调用不走代理，@Transactional 不生效。
 */
@Service
public class NoTransactionService {

    // L6-3: P0 - 本类方法直接调用 @Transactional，AOP 代理失效

    public void process(String userId, String productId) {
        Long orderId = createOrder(userId, productId);  // 本类调用，AOP 不生效
        deduct(userId, orderId);
    }

    @Transactional
    public Long createOrder(String userId, String productId) {
        // L6-3: P0 - 这个 @Transactional 实际不会生效
        // 因为调用来自本类的 process()，不走 AOP 代理
        return 1L;
    }

    public void deduct(String userId, Long orderId) {
    }
}
