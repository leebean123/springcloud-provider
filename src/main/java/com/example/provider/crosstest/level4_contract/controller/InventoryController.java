package com.example.provider.crosstest.level4_contract.controller;

import com.example.provider.crosstest.level4_contract.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Level 4: 契约违约 - Controller 层
 *
 * L4-2: P0 - 信任契约未判空，直接调 isEnough
 *
 * 单文件方法拆分: ❌ 必然报假阳性（看到 .contains 以为 NPE，但 reviewer 追 Service 可纠偏）
 * OCR: ⚠️ 追踪到 Service 可发现是 Service 违约，Controller 信任契约合理
 *
 * 这是关键的假阳性测试用例：
 * - 单文件工具：报 "Controller 可能 NPE" → 假阳性（实际上是 Service 违约）
 * - OCR：追到 Service 发现是 Service 的 bug → 真阳性
 */
@RestController
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    @GetMapping("/stock/check")
    public String checkStock(String skuId) {
        // L4-2: P0 - 信任 InventoryService.queryStock 契约（Javadoc says @return non-null）
        // 所以没有判空，直接使用返回值
        String stock = inventoryService.queryStock(skuId);
        if (stock.contains("available")) {  // L4-2: 信任契约，未判空
            return "OK";
        }
        return "LOW";
    }
}
