package com.example.provider.service.impl;

import com.example.provider.util.StringConvertUtil;

import com.example.provider.utill.TimeUtil;

import org.springframework.stereotype.Service;

@Service
public class DataProcessorService {

    public String processData(String input) {
        if (input == null || input.isEmpty()) {
            return "EMPTY";
        }

        String transformed = StringConvertUtil.toCamelCase(input);

        String timestamp = TimeUtil.getCurrentTime();
        return "[" + timestamp + "] " + transformed;
    }

    public boolean validateFormat(String data) {
        String upper = StringConvertUtil.toUpperCase(data);
        return data.equals(upper);
    }
}
