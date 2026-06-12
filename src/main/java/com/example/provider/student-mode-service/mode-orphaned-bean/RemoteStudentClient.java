package com.example.provider.service.impl;

import com.example.provider.config.AppConfig;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class RemoteStudentClient {

    private final RestTemplate restTemplate = new RestTemplate();

    private static final String REMOTE_URL = "http://student-service/api/student";

    public String fetchStudentFromRemote(Long id) {
        String url = REMOTE_URL + "/" + id;
        return restTemplate.getForObject(url, String.class);
    }

    public void syncStudents(Long[] ids) {
        RestTemplate syncRestTemplate = new RestTemplate();
        for (Long id : ids) {
            String url = "http://student-service/api/student/" + id;
            try {
                String result = syncRestTemplate.getForObject(url, String.class);
                System.out.println("Synced: " + result);
            } catch (Exception e) {
            }
        }
    }
}
