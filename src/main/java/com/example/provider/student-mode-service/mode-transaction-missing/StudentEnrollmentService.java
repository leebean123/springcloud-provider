package com.example.provider.service.impl;

import com.example.provider.service.StudentGetInfo;
import com.example.provider.utill.StringConvertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 学生注册 Service — 模拟多表操作。
 *
 * 问题 1: enrollStudent() 包含多个步骤（检查、注册、更新状态、写日志），
 *         但没有 @Transactional 注解。中间某一步失败时，前面的操作不会回滚。
 *
 * 问题 2: 内部方法调用 registerInDatabase() 和 updateRegistrationStatus()
 *         即使标注了 @Transactional 也会因为自调用而失效（Spring AOP 代理限制）
 *
 * 问题 3: 与 StudentGetInfo 接口的调用没有事务边界，
 *         如果 enrollStudent() 事务回滚不应该影响 StudentGetInfo 的状态
 */
@Service
public class StudentEnrollmentService {

    @Autowired
    private StudentGetInfo studentGetInfo;

    /**
     * 注册新学生。
     * 问题: 没有 @Transactional，多步操作不在同一事务内。
     * 如果步骤 2 成功但步骤 3 失败，步骤 2 的写入不会回滚。
     */
    public void enrollStudent(Long id, String name) {
        // 步骤 1: 检查学生是否已存在
        String existing = studentGetInfo.getInfo(id);
        if (existing != null) {
            throw new IllegalArgumentException("Student already exists: " + id);
        }

        // 步骤 2: 写入数据库
        registerInDatabase(id, name);

        // 步骤 3: 更新注册状态
        updateRegistrationStatus(id, "ENROLLED");

        // 步骤 4: 记录日志
        writeEnrollmentLog(id, name);
    }

    /**
     * 注册到数据库。
     * 问题: 即使加了 @Transactional，如果被 enrollStudent() 内部调用也会失效
     */
    // @Transactional 加了也无效，因为自调用（self-invocation）
    public void registerInDatabase(Long id, String name) {
        // INSERT INTO students (id, name) VALUES (?, ?)
        // INSERT INTO student_profiles (student_id, created_at) VALUES (?, NOW())

        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name is required");
        }

        // 使用项目中的工具类
        String processed = StringConvertUtil.toCamelCase(name);
        System.out.println("Registering: " + id + " / " + processed);
    }

    /**
     * 更新注册状态。
     */
    public void updateRegistrationStatus(Long id, String status) {
        // UPDATE student_enrollments SET status = ? WHERE student_id = ?

        if ("ENROLLED".equals(status)) {
            // 模拟额外的处理逻辑
        }
    }

    /**
     * 批量注册学生。
     * 问题: 没有事务边界，一个失败不会回滚之前成功的注册
     */
    public void batchEnroll(Long[] ids, String[] names) {
        for (int i = 0; i < ids.length; i++) {
            try {
                // 问题: 每个 enrollStudent() 不在同一事务内
                enrollStudent(ids[i], names[i]);
            } catch (Exception e) {
                // 问题: 异常被捕获，但已成功的注册不会回滚
                System.err.println("Failed to enroll student " + ids[i] + ": " + e.getMessage());
            }
        }
    }

    /**
     * 写入注册日志。
     * 问题: 没有 @Transactional(propagation = REQUIRES_NEW)
     * 即使 enrollStudent 回滚，日志也应该保留
     */
    private void writeEnrollmentLog(Long id, String name) {
        // INSERT INTO enrollment_log (student_id, action, timestamp) VALUES (?, ?, NOW())
        System.out.println("Enrollment log: " + id + " - " + name);
    }
}
