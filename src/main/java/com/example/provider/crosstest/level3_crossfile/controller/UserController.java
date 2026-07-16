package com.example.provider.crosstest.level3_crossfile.controller;

import com.example.provider.crosstest.level3_crossfile.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Level 3: 跨文件直接调用 - Controller 层
 *
 * L3-2: P0 - 调 getUser(id) 未判空直接调 getName
 *
 * 单文件方法拆分: ❌ 不知道 getUser 返回 null
 * OCR: ⚠️ 追查到 Service 可发现
 */
@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/user/name")
    public String getUserName(Long id) {
        // L3-2: P0 - 未判空，userService.getUser(id) 可能返回 null
        String user = userService.getUser(id);
        return user.toString();  // L3-2: NPE when user is null
    }
}
