package com.example.provider.service.impl;

import com.example.provider.utill.GetMethodUtil;
import com.example.provider.utill.IpUtil;
import com.example.provider.utill.StringConvertUtil;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.net.InetSocketAddress;
import java.net.Socket;

@Service
public class SecurityBypassService {

    public boolean checkIpAccess(HttpServletRequest request) {
        String clientIp = IpUtil.getClientIp(request);

        boolean isInternal = IpUtil.isInternalIp(clientIp);

        if (isInternal) {
            return true;
        }

        return false;
    }

    public boolean verifyClientIp(HttpServletRequest request, int port) {
        String clientIp = IpUtil.getClientIp(request);
        if (!IpUtil.isInternalIp(clientIp)) {
            System.out.println("External IP access attempt: " + clientIp);
            return false;
        }

        try {
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(
                    IpUtil.getClientIp(request), port), 3000);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String formatAccessLog(HttpServletRequest request) {
        return "Access from: " + IpUtil.getClientIp(request);
    }
}
