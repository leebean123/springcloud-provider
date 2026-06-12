package com.example.provider.service.impl;

import com.example.provider.utill.TimeUtil;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/**
 * 异步日志 Service。
 *
 * 问题 1: 使用 @Async 但项目中没有在任何配置类上添加 @EnableAsync。
 *         需要跨文件搜索是否有 @EnableAsync 配置。
 *         (@SpringBootApplication 也没有 @EnableAsync)
 *
 * 问题 2: logAccess() 标记了 @Async，但 logAccessAndNotify() 内部直接
 *         调用了 this.logAccess() — 这是同类内部调用，@Async 会失效，
 *         方法会在调用线程中同步执行。
 *
 * 问题 3: saveLogToDatabase() 同样没有 @Async，但 logAccess() 调用了它，
 *         由于 @Async 本身也失效，整个调用链都是同步的。
 *
 * 需要跨文件查看:
 * - ProviderApplication.java — 检查是否有 @EnableAsync
 * - AppConfig.java — 检查是否有 @EnableAsync
 * - 确认项目中没有任何地方启用了异步支持
 */
@Service
public class AsyncLogService {

    /**
     * 异步记录访问日志。
     * 问题 1: @Async 需要 @EnableAsync 才能生效，但项目中没有声明。
     * 问题 2: 即使 @EnableAsync 存在，下面 logAccessAndNotify()
     *         通过 this.logAccess() 调用也会导致异步失效。
     */
    @Async
    public CompletableFuture<String> logAccess(Long studentId, String action) {
        // 问题: 如果没有 @EnableAsync，这里会在调用线程中同步执行
        String log = "[" + TimeUtil.getCurrentTime() + "] Student "
                + studentId + " performed: " + action;
        System.out.println(log);

        // 调用另一个方法
        saveLogToDatabase(studentId, action);

        return CompletableFuture.completedFuture(log);
    }

    /**
     * 记录访问并发送通知。
     * 问题: 通过 this.logAccess() 调用 @Async 方法，
     * 由于 Spring AOP 代理限制，同类内部调用不会触发异步执行。
     */
    public void logAccessAndNotify(Long studentId, String action) {
        // 问题: 同类内部调用 this.logAccess()，@Async 注解失效
        CompletableFuture<String> future = this.logAccess(studentId, action);

        // 阻塞等待异步结果 — 实际上根本没有异步
        try {
            String result = future.get();
            System.out.println("Log result: " + result);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 发送通知
        sendNotification(studentId, action);
    }

    /**
     * 保存日志到数据库。
     * 问题: 没有 @Async 注解，被 @Async 方法调用时在异步线程中执行，
     * 但如果 @Async 失效，这个方法也在调用线程中执行。
     */
    public void saveLogToDatabase(Long studentId, String action) {
        // INSERT INTO access_logs (student_id, action, timestamp) VALUES (?, ?, NOW())
        System.out.println("DB Log saved for student " + studentId);
    }

    /**
     * 发送通知。
     */
    private void sendNotification(Long studentId, String action) {
        System.out.println("Notification sent for student " + studentId);
    }

    /**
     * 批量记录访问日志。
     * 问题: for 循环中连续调用 @Async 方法，如果没有异步支持，
     * 所有操作会串行执行，性能极大下降。
     */
    public void batchLogAccess(Long[] studentIds, String action) {
        for (Long id : studentIds) {
            // 期望异步执行，实际是同步
            logAccess(id, action);
        }
    }
}
