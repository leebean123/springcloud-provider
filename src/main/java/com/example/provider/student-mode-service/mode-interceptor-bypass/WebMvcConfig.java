package com.example.provider.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置。
 *
 * 问题 1: 实现了 WebMvcConfigurer 但没有重写 addInterceptors() 方法，
 *         导致 AuthInterceptor 没有被注册，永远不会执行。
 *
 * 问题 2: 应该添加以下代码来注册拦截器:
 *         @Autowired
 *         private AuthInterceptor authInterceptor;
 *
 *         @Override
 *         public void addInterceptors(InterceptorRegistry registry) {
 *             registry.addInterceptor(authInterceptor)
 *                     .addPathPatterns("/**")
 *                     .excludePathPatterns("/health", "/info");
 *         }
 *
 * 问题 3: 没有配置 CORS，如果前端跨域访问会被浏览器拦截。
 *
 * 问题 4: 没有配置静态资源路径映射。
 *
 * 需要跨文件查看:
 * - AuthInterceptor.java — 拦截器已定义但未被注册
 * - application.yml — 没有相关的 CORS 配置
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    // 问题: 没有注册 AuthInterceptor
    // 拦截器 AuthInterceptor 虽然写了完整的认证逻辑，
    // 但因为这里没有 addInterceptors()，所有 Controller 都没有安全保护

    // 问题: 缺少 addCorsMappings() 配置
}
