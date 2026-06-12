package com.example.provider.service.impl;

import com.example.provider.utill.TimeUtil;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class AsyncLogService {

    @Async
    public CompletableFuture<String> logAccess(Long studentId, String action) {
        String log = "[" + TimeUtil.getCurrentTime() + "] Student "
                + studentId + " performed: " + action;
        System.out.println(log);

        saveLogToDatabase(studentId, action);

        return CompletableFuture.completedFuture(log);
    }

    public void logAccessAndNotify(Long studentId, String action) {
        CompletableFuture<String> future = this.logAccess(studentId, action);

        try {
            String result = future.get();
            System.out.println("Log result: " + result);
        } catch (Exception e) {
            e.printStackTrace();
        }

        sendNotification(studentId, action);
    }

    public void saveLogToDatabase(Long studentId, String action) {
        System.out.println("DB Log saved for student " + studentId);
    }

    private void sendNotification(Long studentId, String action) {
        System.out.println("Notification sent for student " + studentId);
    }

    public void batchLogAccess(Long[] studentIds, String action) {
        for (Long id : studentIds) {
            logAccess(id, action);
        }
    }
}
