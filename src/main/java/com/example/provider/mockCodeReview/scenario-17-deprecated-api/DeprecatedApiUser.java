package com.example.provider.controller;

import com.example.provider.utill.StudentDataConverter;

/**
 * 使用弃用 API 的 Controller。
 *
 * 问题 1: 仍然调用了 StudentDataConverter.convertToV1Format()，
 *         这个方法已经是 @Deprecated 的了。
 *
 * 问题 2: 应该迁移到 convertToV2Format()，但 V1 和 V2 的返回字段名不同
 *         （V1: studentId/studentName/studentAge, V2: id/name/age），
 *         如果直接替换，前端接口会断裂。
 *
 * 需要跨文件查看:
 * - StudentDataConverter.java:18 — @Deprecated convertToV1Format()
 * - StudentDataConverter.java:33 — 替代方法 convertToV2Format()
 * - 对比两个方法的 JSON key 差异
 */
public class DeprecatedApiUser {

    /**
     * 获取学生信息 JSON。
     * 问题: 调用已标记为 @Deprecated 的 convertToV1Format()，
     * 编译时会触发 deprecation 警告。
     */
    public Object getStudentJson(Long id, String name, Integer age) {
        // 问题: 调用已弃用的方法，编译器会报 deprecation 警告
        return StudentDataConverter.convertToV1Format(id, name, age);
    }

    /**
     * 应该迁移到 V2 格式的示例。
     * 但如果直接替换:
     * V1 返回: {"studentId":1, "studentName":"张三", "studentAge":20}
     * V2 返回: {"id":1, "name":"张三", "age":20}
     * 前端需要同步更新字段名。
     */
    public Object getStudentJsonV2(Long id, String name, Integer age) {
        return StudentDataConverter.convertToV2Format(id, name, age);
    }
}
