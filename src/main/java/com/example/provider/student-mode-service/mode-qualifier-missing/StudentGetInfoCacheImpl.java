package com.example.provider.service.impl;

import com.example.provider.service.StudentGetInfo;
import org.springframework.stereotype.Service;

@Service
public class StudentGetInfoCacheImpl implements StudentGetInfo {

    private String cachedResult = null;

    @Override
    public String getInfo(Long id) {
        if (cachedResult != null) {
            return cachedResult;
        }

        String result = "cached: student_" + id;
        this.cachedResult = result;
        return result;
    }

    public void clearCache() {
        this.cachedResult = null;
    }
}
