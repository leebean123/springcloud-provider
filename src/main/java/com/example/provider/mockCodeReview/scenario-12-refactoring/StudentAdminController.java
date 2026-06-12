package com.example.provider.controller;

import com.alibaba.fastjson2.JSON;
import com.example.provider.service.StudentGetInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 新增的学生管理 Controller。
 *
 * 问题 1: 与 StudentController 和 StudentControllerDemo 的功能高度重叠。
 *         三个 Controller 都在处理学生资源的 CRUD。
 *
 * 问题 2: 路径使用第四种风格 "/admin/student"
 *         StudentController: 无前缀
 *         StudentControllerDemo: "/provider"
 *         StudentNewController: "/api/v1"
 *         本类: "/admin"
 *         四种路径风格让 API 使用者无所适从
 *
 * 问题 3: header 处理逻辑完全重复 StudentController 和 StudentControllerDemo
 *
 * 问题 4: getStudentV2() 同样没有处理 StudentGetInfo.getInfo() 可能返回 null 的情况
 */
@RestController
@RequestMapping("/admin")
public class StudentAdminController {

    @Autowired
    private StudentGetInfo studentGetInfo;

    /**
     * 获取学生信息（管理员版本）。
     * 问题: 与 StudentController.getStudent() 和
     * StudentControllerDemo.getStudent() 功能完全重叠
     */
    @GetMapping("/student/{id}")
    public Map<String, Object> getStudentV2(@PathVariable Long id,
                                             @RequestHeader Map<String, String> headers) {
        // 问题: header 读取逻辑与 StudentController.java:22-27 和
        // StudentControllerDemo.java:24-25 完全重复
        String mesh_origin = null;
        String mesh_providerServiceUnitCode = null;
        if (headers != null) {
            mesh_origin = headers.get("mesh_origin");
            mesh_providerServiceUnitCode = headers.get("Mesh_Provider_Dataid");
        }

        // 问题: 与 StudentNewController.getStudentInfo() 类似的 NPE 风险
        // 当 id==3 时 getInfo() 返回 null
        String info = studentGetInfo.getInfo(id);

        Map<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("info", info);  // 可能为 null
        result.put("admin", true);
        return result;
    }

    /**
     * 管理员回显。
     * 问题: 与 StudentController.handlePostRequest() 和
     * StudentControllerDemo.handlePostRequest() 完全重复
     */
    @PostMapping("/admin/echo")
    public Map<String, String> adminEcho(@RequestBody String body,
                                          @RequestHeader Map<String, String> headers) {
        // 问题: 第三处完全相同的 header 处理逻辑
        String mesh_origin = headers.get("mesh_origin");
        String mesh_providerServiceUnitCode = headers.get("Mesh_Provider_Dataid");

        Map<String, String> response = new HashMap<>();
        response.put("received", JSON.toJSONString(body));
        return response;
    }

    /**
     * 管理员列表。
     * 问题: 返回格式与 StudentController 不一致
     * StudentController 返回 Map<String, Object>
     * 这里返回 Map<String, String>
     */
    @GetMapping("/students")
    public Map<String, String> listAllStudents() {
        Map<String, String> result = new HashMap<>();
        result.put("count", "0");
        // 问题: 没有真正查询数据，只是 mock
        return result;
    }
}
