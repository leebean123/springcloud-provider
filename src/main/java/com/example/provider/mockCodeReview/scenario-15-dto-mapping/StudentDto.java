package com.example.provider.service.impl;

/**
 * 学生数据传输对象。
 *
 * 问题 1: 字段使用下划线命名（student_id, student_name, enrollment_date），
 *         但 Controller 层返回给前端时使用的是驼峰命名（studentId, studentName），
 *         没有使用 @JsonProperty 注解做映射，导致 JSON 序列化后字段名不一致。
 *
 * 需要跨文件查看:
 * - StudentController.java 返回 Map，key 使用 "id", "name", "age"（无下划线）
 * - StudentNewController.java 返回 JSONObject，使用 "id", "detail"
 * - 三个 Controller 对同一数据的字段命名全部不同
 *
 * 问题 2: enrollmentDate 字段是 String 类型，但实际存的是 "yyyy-MM-dd HH:mm:ss" 格式，
 *         如果前端期望的是 Date/Long 类型时间戳，则存在类型不匹配。
 */
public class StudentDto {

    // 问题: 下划线命名，但现有 Controller 使用驼峰或无下划线命名
    private Long student_id;
    private String student_name;
    private Integer student_age;
    private String enrollment_date;

    // 问题: 字段类型不明确
    private String extraInfo;  // 这里的字段又是驼峰命名，风格不统一

    public Long getStudent_id() {
        return student_id;
    }

    public void setStudent_id(Long student_id) {
        this.student_id = student_id;
    }

    public String getStudent_name() {
        return student_name;
    }

    public void setStudent_name(String student_name) {
        this.student_name = student_name;
    }

    public Integer getStudent_age() {
        return student_age;
    }

    public void setStudent_age(Integer student_age) {
        this.student_age = student_age;
    }

    public String getEnrollment_date() {
        return enrollment_date;
    }

    public void setEnrollment_date(String enrollment_date) {
        this.enrollment_date = enrollment_date;
    }

    public String getExtraInfo() {
        return extraInfo;
    }

    public void setExtraInfo(String extraInfo) {
        this.extraInfo = extraInfo;
    }
}
