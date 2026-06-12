package com.example.provider.service.impl;

import com.example.provider.service.StudentGetInfo;
import com.example.provider.utill.StringConvertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StudentEnrollmentService {

    @Autowired
    private StudentGetInfo studentGetInfo;

    public void enrollStudent(Long id, String name) {
        String existing = studentGetInfo.getInfo(id);
        if (existing != null) {
            throw new IllegalArgumentException("Student already exists: " + id);
        }

        registerInDatabase(id, name);

        updateRegistrationStatus(id, "ENROLLED");

        writeEnrollmentLog(id, name);
    }

    public void registerInDatabase(Long id, String name) {

        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name is required");
        }

        String processed = StringConvertUtil.toCamelCase(name);
        System.out.println("Registering: " + id + " / " + processed);
    }

    public void updateRegistrationStatus(Long id, String status) {

        if ("ENROLLED".equals(status)) {
        }
    }

    public void batchEnroll(Long[] ids, String[] names) {
        for (int i = 0; i < ids.length; i++) {
            try {
                enrollStudent(ids[i], names[i]);
            } catch (Exception e) {
                System.err.println("Failed to enroll student " + ids[i] + ": " + e.getMessage());
            }
        }
    }

    private void writeEnrollmentLog(Long id, String name) {
        System.out.println("Enrollment log: " + id + " - " + name);
    }
}
