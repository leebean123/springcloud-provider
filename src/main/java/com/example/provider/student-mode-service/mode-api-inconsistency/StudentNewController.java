package com.example.provider.controller;

import com.alibaba.fastjson2.JSONObject;
import com.example.provider.service.StudentGetInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class StudentNewController {

    @Autowired
    private StudentGetInfo studentGetInfo;

    @GetMapping("/student/info/{id}")
    public JSONObject getStudentInfo(@PathVariable Long id) {
        JSONObject result = new JSONObject();
        result.put("id", id);
        result.put("detail", studentGetInfo.getInfo(id).toString());
        return result;
    }

    @GetMapping("/student/detail/{id}")
    public Map<String, Object> getStudentDetail(@PathVariable Long id) {
        Map<String, Object> map = new HashMap<>();
        map.put("studentId", id);
        map.put("data", studentGetInfo.getInfo(id));
        return map;
    }

    @PostMapping("/student/echo")
    public Map<String, String> echo(@RequestBody String body) {
        Map<String, String> result = new HashMap<>();
        result.put("received", body);
        return result;
    }
}
