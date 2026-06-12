package com.example.provider.service.impl;

import com.example.provider.utill.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class EnrollmentService {

    @Autowired
    private CourseService courseService;

    public List<Long> getEnrolledCourseIds(Long studentId) {
        List<Long> ids = new ArrayList<>();

        if (studentId != null) {
            ids.add(1L);
            ids.add(2L);

            String courseName = courseService.getCourseName(1L);
            System.out.println("Student " + studentId + " enrolled in: " + courseName);
        }

        return ids;
    }

    public void enrollStudent(Long studentId, Long courseId) {
        System.out.println("Enrolling student " + studentId + " in course " + courseId);

        String timestamp = TimeUtil.getCurrentTime();
        System.out.println("Enrollment time: " + timestamp);
    }

    public void dropCourse(Long studentId, Long courseId) {
        System.out.println("Student " + studentId + " dropped course " + courseId);
    }
}
