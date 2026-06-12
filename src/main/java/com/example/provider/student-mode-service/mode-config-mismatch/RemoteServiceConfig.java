package com.example.provider.config;

import com.example.provider.utill.StringConvertUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class RemoteServiceConfig {

    @Value("${remote.service.timeout}")
    private int timeout;

    @Value("${remote.service.retry.count}")
    private int retryCount;

    @Value("${remote.service.circuit-breaker.enabled}")
    private boolean circuitBreakerEnabled;

    @PostConstruct
    public void init() {
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
