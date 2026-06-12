package com.example.provider.service.impl;

import com.example.provider.config.AppConfig;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * 远程学生服务客户端。
 *
 * 问题 1: AppConfig 中已经定义了一个 @LoadBalanced RestTemplate bean，
 *         但这个类没有通过 @Autowired 注入该 bean，
 *         而是自己手动 new 了一个 RestTemplate()。
 *         new 出来的 RestTemplate 不具备负载均衡能力。
 *
 * 问题 2: 手动 new RestTemplate() 意味着 AppConfig 中配置的
 *         @LoadBalanced 功能完全没有被使用，配置成了死代码。
 *
 * 问题 3: 硬编码了完整的 URL (http://localhost:8082)，使用了服务名称 student-service
 *         但 new 出来的 RestTemplate 无法解析服务名，实际会 DNS 解析失败
 */
@Service
public class RemoteStudentClient {

    // 问题: 没有注入 AppConfig 中已定义的 @LoadBalanced RestTemplate bean
    // 应该使用: @Autowired private RestTemplate restTemplate;
    private final RestTemplate restTemplate = new RestTemplate();

    private static final String REMOTE_URL = "http://student-service/api/student";

    /**
     * 从远程服务获取学生数据。
     * 问题: 手动 new 的 RestTemplate 无法解析服务名 "student-service"，
     * 因为缺乏 @LoadBalanced 注解的支持。
     * 而 AppConfig 中定义的 @LoadBalanced RestTemplate 完全没有被用到。
     */
    public String fetchStudentFromRemote(Long id) {
        String url = REMOTE_URL + "/" + id;
        // 这里会抛出异常: java.net.UnknownHostException: student-service
        return restTemplate.getForObject(url, String.class);
    }

    /**
     * 批量同步学生数据。
     * 问题: 每次调用都 new 一个 RestTemplate 实例，浪费资源。
     */
    public void syncStudents(Long[] ids) {
        // 问题: 每次同步都 new 新的 RestTemplate
        RestTemplate syncRestTemplate = new RestTemplate();
        for (Long id : ids) {
            String url = "http://student-service/api/student/" + id;
            try {
                String result = syncRestTemplate.getForObject(url, String.class);
                System.out.println("Synced: " + result);
            } catch (Exception e) {
                // 问题: 异常被吞没，没有任何重试或告警机制
            }
        }
    }
}
