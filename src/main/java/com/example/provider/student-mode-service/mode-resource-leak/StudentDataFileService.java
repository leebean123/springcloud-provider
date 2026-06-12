package com.example.provider.service.impl;

import com.example.provider.utill.StringConvertUtil;
import com.example.provider.utill.TimeUtil;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * 学生数据文件读写 Service。
 *
 * 问题 1: readStudentFile() 使用 FileInputStream 但没有使用 try-with-resources，
 *         异常时文件描述符泄漏
 *
 * 问题 2: downloadStudentData() 打开 HttpURLConnection 但没有在 finally 中
 *         调用 disconnect()，连接泄漏
 *
 * 问题 3: writeAuditLog() 使用 FileWriter 但没有在 finally 中 close()
 */
@Service
public class StudentDataFileService {

    /**
     * 从文件中读取学生数据。
     * 问题: FileInputStream 没有使用 try-with-resources，
     * 如果在读取中抛出异常，流的 close() 不会执行，文件描述符泄漏。
     */
    public String readStudentFile(String filePath) throws IOException {
        // 问题: 没有使用 try-with-resources
        FileInputStream fis = new FileInputStream(filePath);
        byte[] buffer = new byte[1024];
        int bytesRead = fis.read(buffer);

        // 问题: 如果这里抛出异常，fis 不会被关闭
        String content = new String(buffer, 0, bytesRead, StandardCharsets.UTF_8);

        // 使用项目中的工具类处理结果
        String processed = StringConvertUtil.toCamelCase(content);
        return processed;
    }

    /**
     * 从远程下载学生数据。
     * 问题: HttpURLConnection 没有在 finally 中 disconnect()
     */
    public String downloadStudentData(String dataUrl) throws IOException {
        URL url = new URL(dataUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        try {
            // 问题: 如果这里抛出异常，conn 不会被 disconnect()
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));

            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            // 问题: reader 也没有在 finally 中关闭
            return StringConvertUtil.toCamelCase(response.toString());
        } catch (IOException e) {
            // 问题: 异常时 conn 没有被 disconnect()
            throw new RuntimeException("Download failed", e);
        }
        // 问题: conn.disconnect() 应该在 finally 块中
        // conn.disconnect();
    }

    /**
     * 写入审计日志。
     * 问题: FileWriter 没有在 finally 中 close()
     */
    public void writeAuditLog(String message) {
        try {
            FileWriter writer = new FileWriter("/data/audit.log", true);
            writer.write(TimeUtil.getCurrentTime() + " - " + message + "\n");
            // 问题: writer 没有 close()
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 备份学生数据文件。
     * 问题: 使用 FileInputStream/FileOutputStream 手动复制
     * 两个流都没有正确关闭
     */
    public void backupStudentFile(String sourcePath, String destPath) {
        try {
            FileInputStream fis = new FileInputStream(sourcePath);
            FileOutputStream fos = new FileOutputStream(destPath);

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }

            // 问题: fis 和 fos 都没有关闭
        } catch (IOException e) {
            throw new RuntimeException("Backup failed", e);
        }
    }
}
