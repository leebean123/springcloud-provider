package com.example.provider.crosstest.level3_crossfile.service;

import org.springframework.stereotype.Service;

/**
 * Level 3: 跨文件直接调用 - Service 层
 *
 * L3-1: P0 - getUser() 返回 null
 *
 * 单文件方法拆分: ✅ 方法体内可见
 * OCR: ✅
 */
@Service
public class UserService {

    // L3-1: P0 - 返回 null
    public String getUser(Long id) {
        if (id == null) {
            return null;
        }
        // 模拟：用户不存在
        return null;
    }
}
