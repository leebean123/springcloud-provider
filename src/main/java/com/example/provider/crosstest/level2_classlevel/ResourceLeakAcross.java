package com.example.provider.crosstest.level2_classlevel;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Level 2: 类级别 - L2-4 InputStream 成员变量跨方法泄漏
 *
 * 单文件方法拆分: ❌ open() 局部看 try-with-resources 以为没泄漏
 * OCR: ✅ 可看到成员变量和跨方法传递
 *
 * 问题: InputStream 是成员变量，open() 打开，close() 关闭，
 * 异常路径上 close() 不会被调用，但 open() 局部看 try-with-resources 是安全的
 */
public class ResourceLeakAcross {

    // L2-4: P1 - InputStream 跨方法传递，异常路径泄漏
    private InputStream input;

    public void open(String path) throws IOException {
        // 从局部看 try-with-resources 没问题，但实际存到了成员变量
        InputStream in = new FileInputStream(path);
        this.input = in;  // L2-4: 存储到成员变量，生命周期超出方法
    }

    public void close() throws IOException {
        if (input != null) {
            input.close();
        }
    }
}
