package com.example.provider.service.impl;

import com.example.provider.utill.TimeUtil;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 学生访问计数器 Service。
 *
 * 问题 1: Spring 默认单例模式，但这个 Service 内部有可变状态
 *         (visitCount, totalVisits, visitLogs)，多线程下数据不一致
 *
 * 问题 2: visitCount 是 HashMap，非线程安全。
 *         多线程并发 put 可能导致死循环（JDK 7）或数据丢失（JDK 8+）
 *
 * 问题 3: totalVisits++ 不是原子操作，多线程下计数不准确
 *
 * 问题 4: visitLogs 是 ArrayList，并发 add 可能导致
 *         ConcurrentModificationException 或数据丢失
 *
 * 对比: StudentGetInfoImpl 是无状态的（没有实例变量），所以是线程安全的。
 *       这个类有实例变量，需要额外的同步机制。
 */
@Service
public class StudentVisitCounter {

    // 问题: 使用非线程安全的 HashMap
    private final Map<Long, Integer> visitCount = new HashMap<>();

    // 问题: 普通 long 变量，++ 不是原子操作
    private long totalVisits = 0;

    // 问题: 非线程安全的 ArrayList
    private final List<String> visitLogs = new ArrayList<>();

    // 问题: 没有使用 volatile，其他线程可能看不到更新
    private long lastVisitTime = 0;

    /**
     * 记录一次访问。
     * 问题: 没有任何同步机制，多线程并发下:
     * 1. visitCount.get/put 复合操作不是原子的
     * 2. totalVisits++ 不是原子的
     * 3. visitLogs.add 不是线程安全的
     */
    public void recordVisit(Long studentId) {
        // 问题: 复合操作，非原子
        Integer count = visitCount.get(studentId);
        if (count == null) {
            visitCount.put(studentId, 1);
        } else {
            visitCount.put(studentId, count + 1);
        }

        // 问题: 非原子操作
        totalVisits++;

        // 问题: 线程不安全
        visitLogs.add("Student " + studentId + " visited at " + TimeUtil.getCurrentTime());

        // 问题: 没有 volatile 或同步，其他线程可能看不到这个更新
        lastVisitTime = System.currentTimeMillis();
    }

    /**
     * 批量记录访问。
     * 问题: recordVisit 本身就不安全，循环调用放大问题
     */
    public void batchRecordVisit(Long[] studentIds) {
        for (Long id : studentIds) {
            recordVisit(id);
        }
    }

    /**
     * 获取访问次数。
     * 问题: 返回了内部可变集合的引用，调用者可以修改内部状态
     */
    public Map<Long, Integer> getVisitCount() {
        // 问题: 应该返回不可变副本或 Collections.unmodifiableMap()
        return visitCount;
    }

    /**
     * 获取总访问次数。
     * 问题: 读取 totalVisits 时没有同步，可能读到过期的值
     */
    public long getTotalVisits() {
        return totalVisits;
    }

    /**
     * 获取访问日志。
     * 问题: 返回了内部可变集合的直接引用
     */
    public List<String> getVisitLogs() {
        return visitLogs;
    }

    /**
     * 获取最近访问时间。
     * 问题: 没有 volatile，可能读到过期值
     */
    public long getLastVisitTime() {
        return lastVisitTime;
    }
}
