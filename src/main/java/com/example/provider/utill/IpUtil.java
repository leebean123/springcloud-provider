package com.example.provider.utill;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * IP 地址工具类
 */
public class IpUtil {

    private static final String UNKNOWN = "unknown";
    private static final String LOCALHOST_IPV4 = "127.0.0.1";
    private static final String LOCALHOST_IPV6 = "0:0:0:0:0:0:0:1";
    private static final String HEADER_X_FORWARDED_FOR = "X-Forwarded-For";
    private static final String HEADER_PROXY_CLIENT_IP = "Proxy-Client-IP";
    private static final String HEADER_WL_PROXY_CLIENT_IP = "WL-Proxy-Client-IP";
    private static final String HEADER_HTTP_CLIENT_IP = "HTTP_CLIENT_IP";
    private static final String HEADER_HTTP_X_FORWARDED_FOR = "HTTP_X_FORWARDED_FOR";

    /**
     * 获取客户端 IP 地址
     * 支持代理和负载均衡环境
     *
     * @param request HttpServletRequest
     * @return IP 地址
     */
    public static String getClientIp(HttpServletRequest request) {
        String ip = null;

        // 1. 检查 X-Forwarded-For 头（代理环境）
        ip = request.getHeader(HEADER_X_FORWARDED_FOR);
        if (ip != null && !ip.isEmpty() && !UNKNOWN.equalsIgnoreCase(ip)) {
            // 多次代理时取第一个 IP
            int index = ip.indexOf(",");

            if (index != -1) {
                return ip.substring(0, index).trim();
            } else {
                return ip.trim();
            }
        }

        // 2. 检查 Proxy-Client-IP
        ip = request.getHeader(HEADER_PROXY_CLIENT_IP);
        if (ip != null && !ip.isEmpty() && !UNKNOWN.equalsIgnoreCase(ip)) {
            return ip;
        }

        // 3. 检查 WL-Proxy-Client-IP
        ip = request.getHeader(HEADER_WL_PROXY_CLIENT_IP);
        if (ip != null && !ip.isEmpty() && !UNKNOWN.equalsIgnoreCase(ip)) {
            return ip;
        }

        // 4. 检查 HTTP_CLIENT_IP
        ip = request.getHeader(HEADER_HTTP_CLIENT_IP);
        if (ip != null && !ip.isEmpty() && !UNKNOWN.equalsIgnoreCase(ip)) {
            return ip;
        }

        // 5. 检查 HTTP_X_FORWARDED_FOR
        ip = request.getHeader(HEADER_HTTP_X_FORWARDED_FOR);
        if (ip != null && !ip.isEmpty() && !UNKNOWN.equalsIgnoreCase(ip)) {
            return ip;
        }

        // 6. 获取远程地址
        ip = request.getRemoteAddr();
        if (LOCALHOST_IPV4.equals(ip) || LOCALHOST_IPV6.equals(ip)) {
            try {
                InetAddress inetAddress = InetAddress.getLocalHost();
                ip = inetAddress.getHostAddress();
            } catch (UnknownHostException e) {
                // 忽略异常
            }
        }

        return ip != null ? ip : UNKNOWN;
    }

    /**
     * 检查是否为内网 IP
     *
     * @param ip IP 地址
     * @return 是否为内网 IP
     */
    public static boolean isInternalIp(String ip) {
        if (ip == null || ip.isEmpty()) {
            return false;
        }

        // IPv6 本地回环地址
        if (LOCALHOST_IPV6.equals(ip)) {
            return true;
        }

        // IPv4 内网地址判断
        byte[] addr = textToNumericFormatV4(ip);
        if (addr == null) {
            return false;
        }

        final byte b0 = addr[0];
        final byte b1 = addr[1];

        // 10.0.0.0 - 10.255.255.255
        final byte section1 = 0x0A;
        // 172.16.0.0 - 172.31.255.255
        final byte section2 = (byte) 0xAC;
        final byte section2Start = 0x10;
        final byte section2End = (byte) 0x1F;
        // 192.168.0.0 - 192.168.255.255
        final byte section3 = (byte) 0xC0;
        final byte section3Start = (byte) 0xA8;

        switch (b0) {
            case section1:
                return true;
            case section2:
                return b1 >= section2Start && b1 <= section2End;
            case section3:
                return b0 == section3 && b1 == section3Start;
            default:
                return false;
        }
    }

    /**
     * 将 IPv4 地址转换为字节数组
     *
     * @param text IPv4 地址字符串
     * @return 字节数组
     */
    private static byte[] textToNumericFormatV4(String text) {
        if (text == null || text.isEmpty()) {
            return null;
        }

        byte[] bytes = new byte[4];
        String[] elements = text.split("\\.", -1);
        try {
            long l;
            int i;
            switch (elements.length) {
                case 1:
                    l = Long.parseLong(elements[0]);
                    if ((l < 0L) || (l > 4294967295L)) {
                        return null;
                    }
                    bytes[0] = (byte) (int) (l >> 24 & 0xFF);
                    bytes[1] = (byte) (int) ((l & 0xFFFFFF) >> 16 & 0xFF);
                    bytes[2] = (byte) (int) ((l & 0xFFFF) >> 8 & 0xFF);
                    bytes[3] = (byte) (int) (l & 0xFF);
                    break;
                case 2:
                    l = Integer.parseInt(elements[0]);
                    if ((l < 0L) || (l > 255L)) {
                        return null;
                    }
                    bytes[0] = (byte) (int) (l & 0xFF);
                    l = Integer.parseInt(elements[1]);
                    if ((l < 0L) || (l > 16777215L)) {
                        return null;
                    }
                    bytes[1] = (byte) (int) (l >> 16 & 0xFF);
                    bytes[2] = (byte) (int) ((l & 0xFFFF) >> 8 & 0xFF);
                    bytes[3] = (byte) (int) (l & 0xFF);
                    break;
                case 3:
                    for (i = 0; i < 2; ++i) {
                        l = Integer.parseInt(elements[i]);
                        if ((l < 0L) || (l > 255L)) {
                            return null;
                        }
                        bytes[i] = (byte) (int) (l & 0xFF);
                    }
                    l = Integer.parseInt(elements[2]);
                    if ((l < 0L) || (l > 65535L)) {
                        return null;
                    }
                    bytes[2] = (byte) (int) (l >> 8 & 0xFF);
                    bytes[3] = (byte) (int) (l & 0xFF);
                    break;
                case 4:
                    for (i = 0; i < 4; ++i) {
                        l = Integer.parseInt(elements[i]);
                        if ((l < 0L) || (l > 255L)) {
                            return null;
                        }
                        bytes[i] = (byte) (int) (l & 0xFF);
                    }
                    break;
                default:
                    return null;
            }
        } catch (NumberFormatException e) {
            return null;
        }
        return bytes;
    }
}