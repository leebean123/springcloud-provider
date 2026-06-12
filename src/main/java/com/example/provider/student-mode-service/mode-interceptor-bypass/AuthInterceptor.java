package com.example.provider.config;

import com.example.provider.utill.IpUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    private static final String TOKEN_HEADER = "Mesh_Provider_Dataid";

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        String clientIp = IpUtil.getClientIp(request);
        if (IpUtil.isInternalIp(clientIp)) {
            return true;
        }

        String token = request.getHeader(TOKEN_HEADER);
        if (token == null || token.isEmpty()) {
            response.setStatus(401);
            response.getWriter().write("Unauthorized: missing token");
            return false;
        }

        if (!token.startsWith("MESH-")) {
            response.setStatus(403);
            response.getWriter().write("Forbidden: invalid token format");
            return false;
        }

        return true;
    }

}
