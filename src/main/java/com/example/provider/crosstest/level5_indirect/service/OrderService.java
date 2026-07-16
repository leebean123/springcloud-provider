package com.example.provider.crosstest.level5_indirect.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Level 5: 间接调用链 - Service 层
 *
 * L5-1: P0 - createOrder 无 @Transactional，下游失败无法回滚
 *
 * 调用链：createOrder → paymentService.pay() → AccountDao.updateBalance()
 *
 * 单文件方法拆分: ❌ 需理解事务边界
 * OCR: ❌ 不追查
 */
@Service
public class OrderService {

    @Autowired
    private PaymentService paymentService;

    // L5-1: P0 - 缺少 @Transactional，pay() 失败后数据不一致
    public boolean createOrder(String userId, String productId) {
        // 保存订单...
        boolean payResult = paymentService.pay(userId, "100");
        return payResult;
    }
}
