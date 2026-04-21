package com.example.provider.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

    @Bean
    @LoadBalanced  // 支持服务名调用（如 student-service）
    public RestTemplate restTemplate() {

        //8df794d70595e569460d8fe5ffadda247cba7789
        return new RestTemplate();
    }
}
