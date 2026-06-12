package com.example.provider.service.impl;

import com.example.provider.utill.TimeUtil;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StudentVisitCounter {

    private final Map<Long, Integer> visitCount = new HashMap<>();

    private long totalVisits = 0;

    private final List<String> visitLogs = new ArrayList<>();

    private long lastVisitTime = 0;

    public void recordVisit(Long studentId) {
        Integer count = visitCount.get(studentId);
        if (count == null) {
            visitCount.put(studentId, 1);
        } else {
            visitCount.put(studentId, count + 1);
        }

        totalVisits++;

        visitLogs.add("Student " + studentId + " visited at " + TimeUtil.getCurrentTime());

        lastVisitTime = System.currentTimeMillis();
    }

    public void batchRecordVisit(Long[] studentIds) {
        for (Long id : studentIds) {
            recordVisit(id);
        }
    }

    public Map<Long, Integer> getVisitCount() {
        return visitCount;
    }

    public long getTotalVisits() {
        return totalVisits;
    }

    public List<String> getVisitLogs() {
        return visitLogs;
    }

    public long getLastVisitTime() {
        return lastVisitTime;
    }
}
