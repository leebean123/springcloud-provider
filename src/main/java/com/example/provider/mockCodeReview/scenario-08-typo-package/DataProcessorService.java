package com.example.provider.service.impl;

// 问题 1: import 路径使用了 com.example.provider.util（单个 l）
// 但项目中实际的包名是 com.example.provider.utill（两个 l）
// 这行会导致编译错误: package com.example.provider.util does not exist
import com.example.provider.util.StringConvertUtil;

// 问题 2: 正确的 import 应该是:
// import com.example.provider.utill.StringConvertUtil;

// 问题 3: 正确包名 utill 本身也是拼写错误（应该是 util），
// 这个文件在错误的包名上又叠了一层错误
import com.example.provider.utill.TimeUtil;

import org.springframework.stereotype.Service;

/**
 * 数据处理 Service。
 */
@Service
public class DataProcessorService {

    /**
     * 处理数据的入口方法。
     * 问题: 由于 import 了不存在的包 com.example.provider.util，
     * StringConvertUtil 无法解析，导致编译失败
     */
    public String processData(String input) {
        if (input == null || input.isEmpty()) {
            return "EMPTY";
        }

        // 问题: StringConvertUtil 引用会编译失败
        String transformed = StringConvertUtil.toCamelCase(input);

        String timestamp = TimeUtil.getCurrentTime();
        return "[" + timestamp + "] " + transformed;
    }

    /**
     * 验证数据格式。
     * 问题: 同样引用了有问题的 import
     */
    public boolean validateFormat(String data) {
        // 问题: 编译失败，StringConvertUtil 找不到
        String upper = StringConvertUtil.toUpperCase(data);
        return data.equals(upper);
    }
}
