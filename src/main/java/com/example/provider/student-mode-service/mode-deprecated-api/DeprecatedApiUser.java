package com.example.provider.controller;

import com.example.provider.utill.StudentDataConverter;

public class DeprecatedApiUser {

    public Object getStudentJson(Long id, String name, Integer age) {
        return StudentDataConverter.convertToV1Format(id, name, age);
    }

    public Object getStudentJsonV2(Long id, String name, Integer age) {
        return StudentDataConverter.convertToV2Format(id, name, age);
    }
}
