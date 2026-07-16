package com.example.provider.crosstest.level5_indirect.controller;

import com.example.provider.crosstest.level5_indirect.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Level 5: 间接调用链 - 入口 Controller
 *
 * 调用链：OrderCreateController → OrderService → PaymentService → AccountDao
 *
 * 单文件方法拆分: ❌ 只看 Controller 看不出任何问题
 * OCR: ❌ 需要跨两层追踪
 */
@RestController
public class OrderCreateController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/order/create")
    public boolean createOrder(String userId, String productId) {
        return orderService.createOrder(userId, productId);
    }
}
