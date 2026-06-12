package com.example.provider.config;

import com.example.provider.utill.StringConvertUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * 远程服务调用配置。
 *
 * 问题 1: @Value("${remote.service.timeout}") 读取 remote.service.timeout，
 *         但 application.yml 中根本没有这个配置项。
 *         项目启动时会抛出 IllegalArgumentException: Could not resolve placeholder
 *
 * 问题 2: @Value("${remote.service.retry.count}") 同样不存在
 *
 * 问题 3: 使用了不存在的配置且没有设置默认值（如 ${remote.service.timeout:5000}），
 *         会导致启动失败
 *
 * 需要跨文件查看:
 * - src/main/resources/application.yml 确认配置项存在
 * - pom.xml 确认是否引入了 @ConfigurationProperties 的 processor
 */
@Component
public class RemoteServiceConfig {

    // 问题: application.yml 中没有 remote.service.timeout 这个配置
    @Value("${remote.service.timeout}")
    private int timeout;

    // 问题: application.yml 中没有 remote.service.retry.count 这个配置
    @Value("${remote.service.retry.count}")
    private int retryCount;

    // 问题: 这里也读取了不存在的配置
    @Value("${remote.service.circuit-breaker.enabled}")
    private boolean circuitBreakerEnabled;

    @PostConstruct
    public void init() {
        // 使用项目中的工具类处理配置值
        String configInfo = StringConvertUtil.toCamelCase(
                "remote_service_timeout_" + timeout);
        System.out.println("Config loaded: " + configInfo);
    }

    public int getTimeout() {
        return timeout;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public boolean isCircuitBreakerEnabled() {
        return circuitBreakerEnabled;
    }
}
