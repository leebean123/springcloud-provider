package com.example.provider.crosstest.level2_classlevel;

import org.springframework.stereotype.Service;

/**
 * Level 2: 类级别 - L2-3 @Service 单例 + mutable 成员变量缺同步
 *
 * 单文件方法拆分: ❌ 只看 process() 看不到 @Service 和成员变量
 * OCR: ✅ 可看到 @Service 注解和字段定义
 *
 * 问题: @Service 默认单例，counter 是 mutable 成员变量，多线程下写丢失
 */
@Service
public class MemberThreadSafe {

    // L2-3: P0 - @Service 单例，int counter 非线程安全
    private int counter = 0;

    public int process(String input) {
        counter++;  // L2-3: 非原子操作，多线程并发写丢失++
        return counter;
    }
}
