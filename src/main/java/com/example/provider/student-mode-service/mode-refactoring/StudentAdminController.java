package com.example.provider.controller;

import com.alibaba.fastjson2.JSON;
import com.example.provider.service.StudentGetInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/admin")
public class StudentAdminController {

    @Autowired
    private StudentGetInfo studentGetInfo;

    @GetMapping("/student/{id}")
    public Map<String, Object> getStudentV2(@PathVariable Long id,
                                             @RequestHeader Map<String, String> headers) {
        String mesh_origin = null;
        String mesh_providerServiceUnitCode = null;
        if (headers != null) {
            mesh_origin = headers.get("mesh_origin");
            mesh_providerServiceUnitCode = headers.get("Mesh_Provider_Dataid");
        }

        String info = studentGetInfo.getInfo(id);

        Map<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("info", info);
        result.put("admin", true);
        return result;
    }

    @PostMapping("/admin/echo")
    public Map<String, String> adminEcho(@RequestBody String body,
                                          @RequestHeader Map<String, String> headers) {
        String mesh_origin = headers.get("mesh_origin");
        String mesh_providerServiceUnitCode = headers.get("Mesh_Provider_Dataid");

        Map<String, String> response = new HashMap<>();
        response.put("received", JSON.toJSONString(body));
        return response;
    }

    @GetMapping("/students")
    public Map<String, String> listAllStudents() {
        Map<String, String> result = new HashMap<>();
        result.put("count", "0");
        return result;
    }
}
