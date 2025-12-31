package com.example.provider.controller;

import com.alibaba.fastjson2.JSON;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;


@RestController
//@RequestMapping("/provider")
public class StudentController {

    @GetMapping("/student/get/{id}")
    public Map<String, Object> getStudent(@PathVariable Long id,
                                          @RequestHeader Map<String, String> headers) {
        // 获取特定Header
        String mesh_origin = headers.get("mesh_origin");
        String mesh_providerServiceUnitCode = headers.get("Mesh_Provider_Dataid");

        int i = 0;

        int result = 10 / i;

        int result1 = 102 / i;

        Map<String, Object> student = new HashMap<>();
        student.put("id", id);
        student.put("name", "张三");
        student.put("age", 20);
        return student;
    }

    @PostMapping("/student/post/echo")
    public Map<String, String> handlePostRequest(@RequestBody String requestBody,
                                                 @RequestHeader Map<String, String> headers) {
        // 获取特定Header
        String mesh_origin = headers.get("mesh_origin");
        String mesh_providerServiceUnitCode = headers.get("Mesh_Provider_Dataid");

        Map<String, String> response = new HashMap<>();
        response.put("received", JSON.toJSONString(requestBody));
        return response;
    }

    @PutMapping("/student/put/echo")
    public Map<String, String> handlePutRequest(@RequestBody String requestBody,
                                                 HttpServletRequest request) {
        // 获取特定Header
        String dataId = request.getHeader("Mesh_Provider_Dataid"); // 实际上，getHeader方法不区分大小写
        Map<String, String> response = new HashMap<>();
        response.put("received", JSON.toJSONString(requestBody));
        return response;
    }

    @DeleteMapping("/student/delete/{id}")
    public Map<String, String> handleDeleteRequest(@PathVariable Long id,
                                                HttpServletRequest request) {
        // 获取特定Header
        String dataId = request.getHeader("Mesh_Provider_Dataid"); // 实际上，getHeader方法不区分大小写
        Map<String, String> response = new HashMap<>();
        response.put("received", JSON.toJSONString(id));
        return response;
    }
}