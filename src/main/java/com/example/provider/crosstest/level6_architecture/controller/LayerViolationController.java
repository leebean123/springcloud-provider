package com.example.provider.crosstest.level6_architecture.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Level 6: 架构/设计 - L6-2 分层违规
 *
 * Controller 不应该直接操作数据访问层。
 * 单文件方法拆分: ❌ 漏判。getData() 方法体看起来只是一个查询。
 * OCR: ❌ 需要理解分层规范。
 */
@RestController
public class LayerViolationController {

    // L6-2: P1 - Controller 直接注入 DataSource，跳过 Service 层
    @Autowired
    private DataSource dataSource;

    @GetMapping("/order/direct")
    public Object getData(Long id) throws Exception {
        // L6-2: P1 - Controller 直接操作数据访问层
        // 跳过了 Service 层的事务管理、缓存、权限校验
        Connection conn = dataSource.getConnection();
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM orders WHERE id = ?");
        ps.setLong(1, id);
        ResultSet rs = ps.executeQuery();
        return rs.next() ? rs.getString("status") : null;
    }
}
