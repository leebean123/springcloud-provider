package com.example.provider.crosstest.level2_classlevel;

/**
 * Level 2: 类级别 - L2-1 DCL 单例缺 volatile
 *
 * 单文件方法拆分: ❌ 只看 getInstance() 看不到字段定义
 * OCR: ✅ 可读到字段定义
 *
 * 问题: instance 字段缺 volatile，DCL 失效，可能读到半初始化对象
 */
public class DclSingleton {

    // L2-1: P0 - 缺 volatile，DCL 模式失效
    private static DclSingleton instance;

    public static DclSingleton getInstance() {
        if (instance == null) {
            synchronized (DclSingleton.class) {
                if (instance == null) {
                    instance = new DclSingleton();
                }
            }
        }
        return instance;
    }

    public void doSomething() {
    }
}
