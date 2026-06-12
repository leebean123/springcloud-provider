package com.example.provider.config;

import com.example.provider.utill.IpUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 认证拦截器。
 *
 * 问题 1: 拦截器实现了 HandlerInterceptor，但没有在任何配置类中注册。
 *         需要 WebMvcConfigurer.addInterceptors() 注册才会生效。
 *
 * 问题 2: 项目中没有任何 @Configuration 类实现 WebMvcConfigurer，
 *         所以这个拦截器永远不会被执行。
 *
 * 问题 3: preHandle() 中调用了 IpUtil.getClientIp() 做 IP 白名单校验，
 *         但因为拦截器没注册，IP 白名单校验完全失效。
 *
 * 需要跨文件查看:
 * - 搜索所有 @Configuration 类，看看有没有 WebMvcConfigurer 实现
 * - 确认 AppConfig.java 没有注册拦截器
 * - 确认拦截器实际上不会被执行
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {

    private static final String TOKEN_HEADER = "Mesh_Provider_Dataid";

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        // 检查 IP 是否在白名单中
        String clientIp = IpUtil.getClientIp(request);
        if (IpUtil.isInternalIp(clientIp)) {
            return true; // 内网放行
        }

        // 检查 token
        String token = request.getHeader(TOKEN_HEADER);
        if (token == null || token.isEmpty()) {
            response.setStatus(401);
            response.getWriter().write("Unauthorized: missing token");
            return false;
        }

        // 校验 token 格式
        if (!token.startsWith("MESH-")) {
            response.setStatus(403);
            response.getWriter().write("Forbidden: invalid token format");
            return false;
        }

        return true;
    }

    /**
     * 问题: 这个拦截器虽然定义了，但因为没有注册到 WebMvcConfigurer 中，
     * 永远也不会被执行。项目中的 Controller 端点没有任何安全保护。
     */
}
