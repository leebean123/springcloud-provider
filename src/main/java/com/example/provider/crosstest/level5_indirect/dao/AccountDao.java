package com.example.provider.crosstest.level5_indirect.dao;

import java.math.BigDecimal;

/**
 * Level 5: 间接调用链 - DAO 层
 *
 * L5-3: P0 - 不同数据源，跨库无分布式事务
 *
 * 调用链：OrderCreateController → OrderService [order库] → PaymentService → AccountDao [account库]
 *
 * 单文件方法拆分: ❌ 只看 updateBalance 不知道这是另一数据源
 * OCR: ❌ 不追查
 *
 * 结果：account 余额已扣，但 order 状态写入失败 → 数据不一致
 */
public class AccountDao {

    /**
     * L5-3: P0 - 这是另一数据源的操作（account 库）
     * 与 OrderService.createOrder（order 库）不在同一个事务内
     */
    public boolean updateBalance(String userId, BigDecimal amount) {
        return true;
    }
}
