package com.example.provider.service.impl;

import com.example.provider.service.StudentGetInfo;
import org.springframework.stereotype.Service;

@Service
public class StudentGetInfoImpl implements StudentGetInfo {
    @Override
    public String getInfo(Long id) {
        return "name:张三,age:20";
    }
}
