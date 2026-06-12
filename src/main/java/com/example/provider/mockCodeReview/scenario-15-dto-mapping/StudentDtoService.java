package com.example.provider.service.impl;

import com.example.provider.service.StudentGetInfo;
import com.example.provider.utill.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 学生 DTO 转换 Service。
 *
 * 问题 1: getStudentDto() 返回 StudentDto，但 StudentDto 的字段是下划线命名，
 *         而项目 Controller 返回给前端时期望驼峰命名。
 *
 * 问题 2: getStudentDtoList() 调用了 studentGetInfo.getInfo() 获取数据，
 *         但忽略了 getInfo() 在 id==3 时返回 null 的情况。
 *
 * 问题 3: 与 StudentGetInfo 接口的返回类型（String）不一致，
 *         这里创建了独立的数据模型（StudentDto），但现有 Controller
 *         直接返回 Map，造成项目中多种数据格式并存。
 */
@Service
public class StudentDtoService {

    @Autowired
    private StudentGetInfo studentGetInfo;

    /**
     * 获取学生 DTO。
     * 问题: 返回的 StudentDto 字段为下划线命名，
     *      Controller 层返回 JSON 时不知道用哪种命名风格。
     */
    public StudentDto getStudentDto(Long id) {
        StudentDto dto = new StudentDto();

        // 从 StudentGetInfo 获取数据
        String info = studentGetInfo.getInfo(id);
        // 问题: 当 id==3 时 info 为 null

        dto.setStudent_id(id);
        if (info != null) {
            // 解析 info 字符串设置名称
            dto.setStudent_name("student_" + id);
        }
        dto.setStudent_age(20);
        dto.setEnrollment_date(TimeUtil.getCurrentTime());
        dto.setExtraInfo(info);
        // 问题: extraInfo 可能为 null，但前端可能期望非 null 的空字符串

        return dto;
    }

    /**
     * 批量获取学生 DTO。
     */
    public List<StudentDto> getStudentDtoList(Long[] ids) {
        List<StudentDto> list = new ArrayList<>();
        for (Long id : ids) {
            // 问题: 没有 try-catch，某个 id 失败整个方法中断
            list.add(getStudentDto(id));
        }
        return list;
    }
}
