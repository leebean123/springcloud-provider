package com.example.provider.crosstest.level1_samefile;

/**
 * Level 1: 同文件跨方法
 *
 * L1-1: P0 - process() 调 findOrder()，findOrder 返回 null，process 未判空调 toUpperCase
 * L1-2: P1 - batchProcess() 调 validate()，validate 抛异常，batch 循环中断
 * L1-3: P0 - findOrder() id 为 null 时返回 null
 *
 * 单文件方法拆分: L1-1❌ L1-2❌ L1-3✅
 * OCR: L1-1✅ L1-2✅ L1-3✅
 */
public class OrderProcessor {

    // L1-1: P0 - 调用 findOrder 未判空
    public String process(String id) {
        String order = findOrder(id);
        return order.toUpperCase();  // L1-1: NPE when order is null
    }

    // L1-2: P1 - 循环调用 validate，抛异常中断全部
    public void batchProcess(String[] ids) {
        for (String id : ids) {
            validate(id);  // L1-2: 抛异常中断循环，剩余未处理
        }
    }

    // L1-3: P0 - id 为 null 时返回 null
    public String findOrder(String id) {
        if (id == null) {
            return null;  // L1-3: 返回 null
        }
        return "Order-" + id;
    }

    private void validate(String id) {
        if (id == null) {
            throw new IllegalArgumentException("id cannot be null");  // L1-2: 抛异常
        }
    }
}
