package com.example.provider.config;

import com.example.provider.controller.StudentController;
import com.example.provider.controller.StudentControllerDemo;
import com.example.provider.service.impl.StudentGetInfoImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * 全局异常处理器。
 *
 * 问题 1: 只捕获了 Exception 类型，但 StudentController 抛出的是 ArithmeticException，
 *         StudentControllerDemo 抛出的是 RuntimeException，
 *         StudentGetInfoImpl 抛出也是 RuntimeException，
 *         虽然这些异常都是 Exception 的子类，但缺少具体异常类型的细粒度处理。
 *
 * 问题 2: 没有为不同类型的异常返回不同的 HTTP 状态码，
 *         所有异常都返回 500 Internal Server Error。
 *         例如参数校验失败应该返回 400，资源不存在应该返回 404。
 *
 * 问题 3: 没有处理 StudentControllerDemo 中当 id==2 时返回 null 的情况
 *         （return null 不会触发 ExceptionHandler，但会产生 200 OK 加空响应体）
 *
 * 问题 4: 异常日志只是简单打印堆栈，没有使用 Logger 框架
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理所有异常。
     * 问题: 只捕获了最通用的 Exception，缺少细化处理。
     * 需要跨文件查看所有 Controller 和 Service 抛出的异常类型。
     *
     * StudentController.java:29 — ArithmeticException（除以零）
     * StudentControllerDemo.java:28 — RuntimeException（手动抛出）
     * StudentGetInfoImpl.java:15 — RuntimeException（手动抛出）
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleAllExceptions(Exception e) {
        // 问题: 所有异常都返回 500，但有些异常应该返回不同的状态码
        e.printStackTrace(); // 问题: 应该使用 Logger
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Internal Server Error: " + e.getMessage());
    }

    /**
     * 问题: 缺少对特定异常类型的处理
     * 应该添加:
     *
     * @ExceptionHandler(ArithmeticException.class)
     * public ResponseEntity<String> handleArithmetic(ArithmeticException e) {
     *     return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Calculation error");
     * }
     *
     * @ExceptionHandler(IllegalArgumentException.class)
     * public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException e) {
     *     return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid argument");
     * }
     */

    /**
     * 问题: 缺少对 null 返回值的处理。
     * StudentControllerDemo.getStudent() 在 id==2 时返回 null，
     * 这会导致 Spring MVC 返回空的 200 OK 响应，
     * 而不会触发任何 ExceptionHandler。
     * 可以添加 ResponseBodyAdvice 来处理 null 返回值。
     */
}
