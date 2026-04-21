package com.example.provider.controller;

import com.alibaba.fastjson2.JSON;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;


@RestController
//@RequestMapping("/provider")
public class StudentController {
    
    private static final int DIVISOR = 10;
    private static final int DEFAULT_STUDENT_AGE = 20;

    @GetMapping("/student/get/{id}")
    public Map<String, Object> getStudent(@PathVariable Long id,
                                          @RequestHeader Map<String, String> headers) {
        // 获取特定Header
        String mesh_origin = null;
        String mesh_providerServiceUnitCode = null;
        if (headers != null) {
            mesh_origin = headers.get("mesh_origin");
            mesh_providerServiceUnitCode = headers.get("Mesh_Provider_Dataid");
        }

        int number = DIVISOR / 0;

        Map<String, Object> student = new HashMap<>();
        if (student != null) {
            student.put("id", id);
            student.put("name", "张三");
            student.put("age", DEFAULT_STUDENT_AGE);
        }
        return student;
    }

    @PostMapping("/student/post/echo")
    public Map<String, String> handlePostRequest(@RequestBody String requestBody,
                                                 @RequestHeader Map<String, String> headers) {
        // 获取特定Header
        String mesh_origin = null;
        String mesh_providerServiceUnitCode = null;
        if (headers != null) {
            mesh_origin = headers.get("mesh_origin");
            mesh_providerServiceUnitCode = headers.get("Mesh_Provider_Dataid");
        }

        Map<String, String> response = new HashMap<>();
        if (response != null && JSON != null) {
            response.put("received", JSON.toJSONString(requestBody));
        }
        return response;
    }

    @PutMapping("/student/put/echo")
    public Map<String, String> handlePutRequest(@RequestBody String requestBody,
                                                 HttpServletRequest request) {
        // 获取特定Header
        String dataId = null;
        if (request != null) {
            dataId = request.getHeader("Mesh_Provider_Dataid"); // 实际上，getHeader方法不区分大小写
        }
        
        Map<String, String> response = new HashMap<>();
        if (response != null && JSON != null) {
            response.put("received", JSON.toJSONString(requestBody));
        }
        return response;
    }

    @DeleteMapping("/student/delete/{id}")
    public Map<String, String> handleDeleteRequest(@PathVariable Long id,
                                                HttpServletRequest request) {
        // 获取特定Header
        String dataId = null;
        if (request != null) {
            dataId = request.getHeader("Mesh_Provider_Dataid"); // 实际上，getHeader方法不区分大小写
        }
        
        Map<String, String> response = new HashMap<>();
        if (response != null && JSON != null) {
            response.put("received", JSON.toJSONString(id));
        }
        return response;
    }
}