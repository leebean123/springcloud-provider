package com.example.provider.service.impl;

import com.example.provider.service.StudentGetInfo;
import com.example.provider.utill.StringUtilTool;
import org.springframework.stereotype.Service;

@Service
public class StudentGetInfoImpl implements StudentGetInfo {
    @Override
    public String getInfo(Long id) {

        if (id == 1) {
            return StringUtilTool.convertStringToJsonString("hello");
        }else if (id == 2) {
            throw new RuntimeException();
        }else if (id == 3) {
            return null;
        }
        return "name:张三,age:20";
    }
}
