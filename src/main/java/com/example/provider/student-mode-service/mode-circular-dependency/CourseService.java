package com.example.provider.service.impl;

import com.example.provider.utill.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class CourseService {

    @Autowired
    private EnrollmentService enrollmentService;

    private final List<String> courses = Arrays.asList("Math", "English", "Science");

    public List<String> getStudentCourses(Long studentId) {
        List<Long> enrolledCourseIds = enrollmentService.getEnrolledCourseIds(studentId);
        System.out.println("Enrolled courses for " + studentId + ": " + enrolledCourseIds);

        return courses;
    }

    public String getCourseName(Long courseId) {
        if (courseId != null && courseId < courses.size()) {
            return courses.get(courseId.intValue());
        }
        return "Unknown";
    }

    public String logCourseAccess(Long courseId, Long studentId) {
        String timestamp = TimeUtil.getCurrentTime();
        return "[" + timestamp + "] Course " + courseId + " accessed by " + studentId;
    }
}
