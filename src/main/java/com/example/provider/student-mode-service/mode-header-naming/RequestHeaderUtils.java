package com.example.provider.utill;

import com.example.provider.controller.StudentController;
import com.example.provider.controller.StudentControllerDemo;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * HTTP 请求头处理工具类。
 *
 * 问题 1: 命名风格混乱 —— 常量命名混合使用下划线和驼峰
 *         HEADER_MESH_ORIGIN（全大写下划线） vs mesh_origin_head（驼峰）
 *
 * 问题 2: getMeshHeader() 方法直接返回 headers.get("mesh_origin")
 *         但 StudentController.java:25 和 StudentControllerDemo.java:24
 *         都读取 headers.get("mesh_origin")，功能重复
 *
 * 问题 3: getProviderDataId() 方法中重复 StudentController.java:64
 *         和 StudentControllerDemo.java:60 的 request.getHeader("Mesh_Provider_Dataid")
 *         逻辑分散在三处
 *
 * 问题 4: 没有 null 安全处理，当 header 不存在时返回 null
 */
public class RequestHeaderUtils {

    // 问题: 常量命名风格不统一
    // 全大写下划线风格（标准）:
    private static final String HEADER_MESH_ORIGIN = "mesh_origin";
    private static final String HEADER_PROVIDER_DATA_ID = "Mesh_Provider_Dataid";

    // 问题: 驼峰命名风格（不标准）
    private static final String mesh_origin_head = "mesh_origin";
    private static final String providerDataIdHeader = "Mesh_Provider_Dataid";

    /**
     * 获取 Mesh 源信息。
     * 问题: 与 StudentController.java:25 和 StudentControllerDemo.java:24
     * 中的读取逻辑完全重复。
     */
    public static String getMeshOrigin(HttpServletRequest request) {
        // 问题: 没有 null 检查
        return request.getHeader(HEADER_MESH_ORIGIN);
    }

    /**
     * 从 header map 中获取 Mesh 源信息。
     * 问题: 同样与 Controller 中的逻辑重复
     */
    public static String getMeshOriginFromMap(Map<String, String> headers) {
        // 问题: 没有 null 检查
        return headers.get(mesh_origin_head);
    }

    /**
     * 获取 Provider DataId。
     * 问题: 多个地方形成相同逻辑的重复:
     * - 本方法
     * - StudentController.java:64
     * - StudentControllerDemo.java:60
     */
    public static String getProviderDataId(HttpServletRequest request) {
        // 问题: 没有 null 检查，如果 request 为 null 则 NPE
        return request.getHeader(HEADER_PROVIDER_DATA_ID);
    }

    /**
     * 从 header map 中获取 Provider DataId。
     * 问题: try-catch 吞异常
     */
    public static String getProviderDataIdFromMap(Map<String, String> headers) {
        try {
            // 问题: 与 Controller 中相同的逻辑，但没有像 Controller 那样加 null 判断
            return headers.get(HEADER_PROVIDER_DATA_ID);
        } catch (Exception e) {
            // 问题: 异常被吞没
            return null;
        }
    }

    /**
     * 检查请求是否来自 Mesh。
     * 问题: 只有在 mesh_origin 和 Mesh_Provider_Dataid 都非空时才认为来自 Mesh，
     * 但 header 名称的大小写没有标准化
     */
    public static boolean isMeshRequest(HttpServletRequest request) {
        // 问题: 直接比较可能因为 header 大小写问题导致误判
        String origin = request.getHeader("mesh_origin");
        String dataId = request.getHeader("Mesh_Provider_Dataid");
        return origin != null && !origin.isEmpty()
                && dataId != null && !dataId.isEmpty();
    }
}
