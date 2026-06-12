package com.example.provider.service.impl;

import com.example.provider.service.StudentGetInfo;
import com.example.provider.utill.StringConvertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 组合多个数据源的学生信息服务。
 *
 * 调用链: Controller → 本 Service → StudentGetInfo.getInfo()
 *
 * 问题 1: formatStudentInfo() 没有对 studentGetInfo.getInfo() 的返回值做 null 检查
 *         当 id==3 时 StudentGetInfoImpl.getInfo() 返回 null
 *         null 被传递给 processResult() 方法，而 processResult() 直接调用 .toString() 会 NPE
 *
 * 问题 2: getStudentSummary() 调用 formatStudentInfo() 也没有处理异常，
 *         异常会直接抛到 Controller 层，返回 HTTP 500
 *
 * 问题 3: 与 StudentGetInfo 接口的契约约定缺失，
 *         接口没有 @Nullable 注解，调用方无法知道可能返回 null
 */
@Service
public class StudentInfoCompositeService {

    @Autowired
    private StudentGetInfo studentGetInfo;

    /**
     * 格式化学生信息。
     * 问题: 没有对 getInfo() 返回值做 null 检查。
     * 需要跨文件查看 StudentGetInfoImpl.getInfo() 才能知道 id==3 时返回 null。
     */
    public String formatStudentInfo(Long id) {
        String rawInfo = studentGetInfo.getInfo(id);
        // 问题: 当 id==3 时 rawInfo 为 null，调用 toUpperCase() 触发 NPE
        return processResult(rawInfo);
    }

    /**
     * 获取学生摘要信息。
     * 问题: 调用 formatStudentInfo() 但没有 try-catch，
     * NPE 会传播到 Controller 层。
     */
    public String getStudentSummary(Long id) {
        String prefix = "Student Summary: ";
        String formatted = null;

        if (id == null) {
            return prefix + "EMPTY";
        }

        formatted = formatStudentInfo(id);
        // 问题: formatStudentInfo 内部可能抛出 NPE，这里没有防御
        return prefix + formatted;
    }

    /**
     * 处理原始结果。
     * 问题: 没有 null 检查，直接调用 toUpperCase()
     */
    private String processResult(String result) {
        // 问题: 当上游返回 null 时，这行会 NPE
        return StringConvertUtil.toCamelCase(result.trim().toUpperCase());
    }

    /**
     * 批量查询学生信息。
     * 问题: 其中一个 id 失败会影响整个循环，没有隔离每个查询的异常
     */
    public String[] batchGetInfo(Long[] ids) {
        String[] results = new String[ids.length];
        for (int i = 0; i < ids.length; i++) {
            // 问题: 如果某个 id 的查询抛异常，整个方法中断
            results[i] = formatStudentInfo(ids[i]);
        }
        return results;
    }
}
