package com.example.provider.service.impl;

import com.example.provider.service.StudentGetInfo;
import com.example.provider.utill.StringConvertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StudentInfoCompositeService {

    @Autowired
    private StudentGetInfo studentGetInfo;

    public String formatStudentInfo(Long id) {
        String rawInfo = studentGetInfo.getInfo(id);
        return processResult(rawInfo);
    }

    public String getStudentSummary(Long id) {
        String prefix = "Student Summary: ";
        String formatted = null;

        if (id == null) {
            return prefix + "EMPTY";
        }

        formatted = formatStudentInfo(id);
        return prefix + formatted;
    }

    private String processResult(String result) {
        return StringConvertUtil.toCamelCase(result.trim().toUpperCase());
    }

    public String[] batchGetInfo(Long[] ids) {
        String[] results = new String[ids.length];
        for (int i = 0; i < ids.length; i++) {
            results[i] = formatStudentInfo(ids[i]);
        }
        return results;
    }
}
