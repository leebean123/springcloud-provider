package com.example.provider.crosstest.level5_indirect.service;

import com.example.provider.crosstest.level5_indirect.dao.AccountDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Level 5: 间接调用链 - Payment 层
 *
 * L5-2: P1 - 远程调用缺超时设置，线程阻塞
 *
 * 单文件方法拆分: ❌ 需跨两层
 * OCR: ❌ 不追查
 */
@Service
public class PaymentService {

    @Autowired
    private AccountDao accountDao;

    // L5-2: P1 - 远程调用 HTTP/RPC 缺超时设置
    public boolean pay(String userId, String amount) {
        // 远程 HTTP 调用，没有设置超时
        boolean result = callRemotePayment(userId, amount);  // L5-2: 无超时，可能永久阻塞
        if (result) {
            accountDao.updateBalance(userId, new BigDecimal(amount));
        }
        return result;
    }

    private boolean callRemotePayment(String userId, String amount) {
        // 模拟远程调用
        return true;
    }
}
