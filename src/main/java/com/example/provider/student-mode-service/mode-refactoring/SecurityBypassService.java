package com.example.provider.service.impl;

import com.example.provider.utill.GetMethodUtil;
import com.example.provider.utill.IpUtil;
import com.example.provider.utill.StringConvertUtil;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * IP 安全检查 Service。
 *
 * 问题 1: checkIpAccess() 引用了 IpUtil.isInternalIp() 来判断是否内网 IP，
 *         但 IpUtil.isInternalIp() 对 case 3（192.168.x.x）的判断存在逻辑 bug：
 *         IpUtil.java:121 中 return b0 == section3 && b1 == section3Start
 *         但前面的 switch(b0) 已经匹配了 case section3，所以 b0 == section3 恒为 true，
 *         这种写法本身没错，但有潜在的不一致风险。
 *
 * 问题 2: verifyClientIp() 逻辑存在 TOCTOU（Time-of-check Time-of-use）漏洞：
 *         先用 isInternalIp() 检查，然后再进行处理。
 *         但检查和处理之间 IP 可能被篡改。
 *
 * 问题 3: 没有处理 IPv6 地址，IpUtil.isInternalIp() 只处理了 IPv6 回环地址，
 *         其他 IPv6 地址都被当作公网 IP 处理
 */
@Service
public class SecurityBypassService {

    /**
     * 检查 IP 访问权限。
     * 问题: 外网 IP 返回 false（拒绝），但内网 IP 返回 true（允许）。
     * 需要跨文件查看 IpUtil.isInternalIp() 的逻辑是否正确。
     *
     * 查看 IpUtil.java:89-125:
     * - case section1 (10.x.x.x) → 正确
     * - case section2 (172.16-31.x.x) → 正确
     * - case section3 (192.168.x.x) → 需要验证实现是否正确
     */
    public boolean checkIpAccess(HttpServletRequest request) {
        String clientIp = IpUtil.getClientIp(request);

        // 问题: 内网 IP 允许访问，但 isInternalIp() 的实现是否正确？
        boolean isInternal = IpUtil.isInternalIp(clientIp);

        if (isInternal) {
            return true; // 内网放行
        }

        // 外网 IP 拒绝
        return false;
    }

    /**
     * 验证客户端 IP。
     * 问题: TOCTOU 漏洞 — 检查 IP 后到实际连接之间，
     * IP 可能已经被修改（比如通过修改请求头）
     */
    public boolean verifyClientIp(HttpServletRequest request, int port) {
        // 步骤 1: 获取并检查 IP
        String clientIp = IpUtil.getClientIp(request);
        if (!IpUtil.isInternalIp(clientIp)) {
            // 问题: 这里直接把 IP 用于日志，没有做任何脱敏
            System.out.println("External IP access attempt: " + clientIp);
            return false;
        }

        // 步骤 2: 连接验证（TOCTOU）
        // 问题: 从步骤 1 到步骤 2 之间，request 可能被修改
        // 但这里重新获取了 IP，实际上减轻了问题
        try {
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(
                    IpUtil.getClientIp(request), port), 3000);
            // 问题: socket 没有关闭
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 日志脱敏处理。
     * 问题: 直接将 IP 记录到日志，没有做 IP 脱敏（如 192.168.*.*）
     * 违反安全审计规范
     */
    public String formatAccessLog(HttpServletRequest request) {
        // 问题: 完整 IP 写入日志，没有脱敏处理
        return "Access from: " + IpUtil.getClientIp(request);
    }
}
