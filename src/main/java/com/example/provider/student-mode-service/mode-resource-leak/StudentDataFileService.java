package com.example.provider.service.impl;

import com.example.provider.utill.StringConvertUtil;
import com.example.provider.utill.TimeUtil;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@Service
public class StudentDataFileService {

    public String readStudentFile(String filePath) throws IOException {
        FileInputStream fis = new FileInputStream(filePath);
        byte[] buffer = new byte[1024];
        int bytesRead = fis.read(buffer);

        String content = new String(buffer, 0, bytesRead, StandardCharsets.UTF_8);

        String processed = StringConvertUtil.toCamelCase(content);
        return processed;
    }

    public String downloadStudentData(String dataUrl) throws IOException {
        URL url = new URL(dataUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));

            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            return StringConvertUtil.toCamelCase(response.toString());
        } catch (IOException e) {
            throw new RuntimeException("Download failed", e);
        }
    }

    public void writeAuditLog(String message) {
        try {
            FileWriter writer = new FileWriter("/data/audit.log", true);
            writer.write(TimeUtil.getCurrentTime() + " - " + message + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void backupStudentFile(String sourcePath, String destPath) {
        try {
            FileInputStream fis = new FileInputStream(sourcePath);
            FileOutputStream fos = new FileOutputStream(destPath);

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }

        } catch (IOException e) {
            throw new RuntimeException("Backup failed", e);
        }
    }
}
