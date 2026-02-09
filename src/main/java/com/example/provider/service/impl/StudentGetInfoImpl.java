package com.example.provider.service.impl;

import com.example.provider.service.StudentGetInfo;
import org.springframework.stereotype.Service;

@Service
public class StudentGetInfoImpl implements StudentGetInfo {
    @Override
    public String getInfo(Long id) {

        if (id == 1) {
            return "是这条件分支测试";
        }else if (id == 2) {
            throw new RuntimeException();
        }else if (id == 3) {
            return null;
        }
        return "name:张三,age:20";
    }
}
