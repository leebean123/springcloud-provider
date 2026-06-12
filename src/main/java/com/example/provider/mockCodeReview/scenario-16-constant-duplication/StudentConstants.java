package com.example.provider.service.impl;

/**
 * 学生相关常量定义。
 *
 * 问题 1: DEFAULT_PAGE_SIZE = 20，但 StudentController.java:16 中
 *         定义了 DEFAULT_STUDENT_AGE = 20，值巧合相同但语义完全不同，
 *         如果需求变更需要修改页面大小，开发者可能误以为是年龄。
 *
 * 问题 2: MAX_RETRY_TIMES = 3，而 RemoteStudentClient 中硬编码了重试逻辑，
 *         但没有使用这个常量，导致重试次数不一致。
 *
 * 问题 3: TIMEOUT_MS = 5000，但与 SecurityBypassService 中硬编码的 3000 不一致。
 *
 * 需要跨文件对比:
 * - StudentController.java:16 — DEFAULT_STUDENT_AGE = 20
 * - SecurityBypassService.java:27 — TIMEOUT_MS = 3000
 * - RemoteStudentClient.java — 没有使用重试常量
 */
public class StudentConstants {

    // 问题: 与 StudentController.DEFAULT_STUDENT_AGE 的值相同（20），
    // 但语义完全不同。
    public static final int DEFAULT_PAGE_SIZE = 20;

    // 问题: 定义了重试次数但没有被 RemoteStudentClient 使用
    public static final int MAX_RETRY_TIMES = 3;

    // 问题: 超时时间 5000ms，但 SecurityBypassService 中硬编码了 3000ms
    public static final int TIMEOUT_MS = 5000;

    // 问题: 定义了缓存过期时间，但项目中没有任何缓存机制
    public static final int CACHE_EXPIRE_MINUTES = 30;

    // 问题: 定义一个叫 STUDENT_NOT_FOUND_CODE 但值为负数的错误码，
    // 与 StudentControllerDemo 中直接抛 RuntimeException 的方式不一致
    public static final int STUDENT_NOT_FOUND_CODE = -1001;
    public static final String STUDENT_NOT_FOUND_MSG = "Student not found";

    // 问题: 定义了这些常量但没有任何类引用它们，属于死代码
    public static final String DEFAULT_AVATAR = "default.png";
    public static final int MAX_NAME_LENGTH = 50;
}
