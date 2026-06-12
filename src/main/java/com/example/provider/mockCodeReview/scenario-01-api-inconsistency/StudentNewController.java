package com.example.provider.controller;

import com.alibaba.fastjson2.JSONObject;
import com.example.provider.service.StudentGetInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 新增的学生信息 Controller。
 *
 * 问题 1: 类级别的 @RequestMapping 路径风格与现有 Controller 不统一
 *         现有 StudentController 无类级别路径，StudentControllerDemo 使用 "/provider"
 *         这个 Controller 使用 "/api/v1"，引入第三种路径风格
 *
 * 问题 2: 与 StudentGetInfo 接口的调用约定不一致
 *         getInfo() 在 id==3 时返回 null，这里直接 toString() 会 NPE
 *
 * 问题 3: 返回格式不统一，现有 Controller 返回 Map<String, Object>,
 *         这里返回 JSONObject，导致 API 响应格式在项目中不统一
 */
@RestController
@RequestMapping("/api/v1")
public class StudentNewController {

    @Autowired
    private StudentGetInfo studentGetInfo;

    /**
     * 问题: 直接调用 studentGetInfo.getInfo(id) 并 toString()
     * 当 id==3 时 getInfo() 返回 null，触发 NPE
     */
    @GetMapping("/student/info/{id}")
    public JSONObject getStudentInfo(@PathVariable Long id) {
        JSONObject result = new JSONObject();
        result.put("id", id);
        result.put("detail", studentGetInfo.getInfo(id).toString());
        return result;
    }

    /**
     * 问题: 与 StudentController 的 /student/get/{id} (GET) 功能重叠
     * 但路径不同，造成 API 使用者困惑
     */
    @GetMapping("/student/detail/{id}")
    public Map<String, Object> getStudentDetail(@PathVariable Long id) {
        Map<String, Object> map = new HashMap<>();
        map.put("studentId", id);
        map.put("data", studentGetInfo.getInfo(id));
        return map;
    }

    /**
     * 问题: 与 StudentController.handlePostRequest() 完全重复
     * 相同的功能但路径不同，造成维护成本翻倍
     */
    @PostMapping("/student/echo")
    public Map<String, String> echo(@RequestBody String body) {
        Map<String, String> result = new HashMap<>();
        result.put("received", body);
        return result;
    }
}
