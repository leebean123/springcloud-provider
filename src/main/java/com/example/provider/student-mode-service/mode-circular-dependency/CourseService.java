package com.example.provider.service.impl;

import com.example.provider.utill.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * 课程 Service。
 *
 * 问题 1: CourseService 和 EnrollmentService 互相 @Autowired 注入对方，
 *         形成循环依赖。Spring 虽然能通过三级缓存解决，
 *         但会导致:
 *         1. 其中一个 bean 的方法调用时 AOP 代理可能失效
 *         2. 启动时有 Warning 日志
 *         3. 后续维护时容易出问题
 *
 * 问题 2: 推荐的方式是通过接口隔离或 @Lazy 注解解决
 */
@Service
public class CourseService {

    @Autowired
    private EnrollmentService enrollmentService;
    // 问题: CourseService 和 EnrollmentService 互相注入

    private final List<String> courses = Arrays.asList("Math", "English", "Science");

    /**
     * 获取某学生的课程列表。
     * 调用 enrollmentService.getEnrollments() 形成循环调用
     */
    public List<String> getStudentCourses(Long studentId) {
        // 问题: 调用 enrollmentService 触发循环依赖
        List<Long> enrolledCourseIds = enrollmentService.getEnrolledCourseIds(studentId);
        System.out.println("Enrolled courses for " + studentId + ": " + enrolledCourseIds);

        return courses;
    }

    /**
     * 获取课程名称。
     */
    public String getCourseName(Long courseId) {
        if (courseId != null && courseId < courses.size()) {
            return courses.get(courseId.intValue());
        }
        return "Unknown";
    }

    /**
     * 记录课程访问日志。
     */
    public String logCourseAccess(Long courseId, Long studentId) {
        String timestamp = TimeUtil.getCurrentTime();
        return "[" + timestamp + "] Course " + courseId + " accessed by " + studentId;
    }
}
