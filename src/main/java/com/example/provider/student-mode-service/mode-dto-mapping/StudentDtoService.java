package com.example.provider.service.impl;

import com.example.provider.service.StudentGetInfo;
import com.example.provider.utill.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class StudentDtoService {

    @Autowired
    private StudentGetInfo studentGetInfo;

    public StudentDto getStudentDto(Long id) {
        StudentDto dto = new StudentDto();

        String info = studentGetInfo.getInfo(id);

        dto.setStudent_id(id);
        if (info != null) {
            dto.setStudent_name("student_" + id);
        }
        dto.setStudent_age(20);
        dto.setEnrollment_date(TimeUtil.getCurrentTime());
        dto.setExtraInfo(info);

        return dto;
    }

    public List<StudentDto> getStudentDtoList(Long[] ids) {
        List<StudentDto> list = new ArrayList<>();
        for (Long id : ids) {
            list.add(getStudentDto(id));
        }
        return list;
    }
}
