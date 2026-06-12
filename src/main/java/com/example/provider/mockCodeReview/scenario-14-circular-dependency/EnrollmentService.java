package com.example.provider.service.impl;

import com.example.provider.utill.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 选课 Service。
 *
 * 问题 1: 与 CourseService 互相 @Autowired 注入，形成循环依赖。
 *         CourseService 注入 EnrollmentService，EnrollmentService 又注入 CourseService。
 *
 * 问题 2: getEnrolledCourseIds() 调用了 courseService.getCourseName()，
 *         进一步加深了循环调用的风险。
 *
 * 问题 3: 如果后续某个 bean 需要使用 AOP（如 @Transactional），
 *         循环依赖会导致 AOP 代理无法正确创建。
 */
@Service
public class EnrollmentService {

    @Autowired
    private CourseService courseService;
    // 问题: CourseService 和 EnrollmentService 互相注入

    /**
     * 获取学生已选课程的 ID 列表。
     * 问题: 调用了 courseService.getCourseName()，增加了循环调用深度
     */
    public List<Long> getEnrolledCourseIds(Long studentId) {
        List<Long> ids = new ArrayList<>();

        // 模拟查询数据库
        if (studentId != null) {
            ids.add(1L);
            ids.add(2L);

            // 问题: 进一步调用 courseService，加深循环依赖
            String courseName = courseService.getCourseName(1L);
            System.out.println("Student " + studentId + " enrolled in: " + courseName);
        }

        return ids;
    }

    /**
     * 注册选课。
     */
    public void enrollStudent(Long studentId, Long courseId) {
        // 模拟选课操作
        System.out.println("Enrolling student " + studentId + " in course " + courseId);

        String timestamp = TimeUtil.getCurrentTime();
        System.out.println("Enrollment time: " + timestamp);
    }

    /**
     * 退课。
     */
    public void dropCourse(Long studentId, Long courseId) {
        // 模拟退课操作
        System.out.println("Student " + studentId + " dropped course " + courseId);
    }
}
