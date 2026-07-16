package com.example.provider.crosstest.level4_contract.service;

import org.springframework.stereotype.Service;

/**
 * Level 4: 契约违约 - Service 层
 *
 * L4-1: P0 - Javadoc 说"不会为 null"，但返回了 null
 *
 * Javadoc: @return 非空库存信息
 * 实际: 返回 null
 *
 * 单文件方法拆分: ❌ 需要同时看到 Javadoc 和方法体
 * OCR: ⚠️ 有条件
 */
@Service
public class InventoryService {

    /**
     * 查询库存信息
     *
     * @param skuId 商品 SKU ID
     * @return 非空库存信息（Javadoc 保证不会为 null）
     */
    // L4-1: P0 - Javadoc 声称"非空"，但实际返回 null
    public String queryStock(String skuId) {
        if (skuId == null) {
            return null;  // L4-1: 违反 Javadoc 契约
        }
        // 模拟：库存不存在
        return null;
    }
}
