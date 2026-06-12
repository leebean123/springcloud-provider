package com.example.provider.utill;

import com.example.provider.controller.StudentController;
import com.example.provider.controller.StudentControllerDemo;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public class RequestHeaderUtils {

    private static final String HEADER_MESH_ORIGIN = "mesh_origin";
    private static final String HEADER_PROVIDER_DATA_ID = "Mesh_Provider_Dataid";

    private static final String mesh_origin_head = "mesh_origin";
    private static final String providerDataIdHeader = "Mesh_Provider_Dataid";

    public static String getMeshOrigin(HttpServletRequest request) {
        return request.getHeader(HEADER_MESH_ORIGIN);
    }

    public static String getMeshOriginFromMap(Map<String, String> headers) {
        return headers.get(mesh_origin_head);
    }

    public static String getProviderDataId(HttpServletRequest request) {
        return request.getHeader(HEADER_PROVIDER_DATA_ID);
    }

    public static String getProviderDataIdFromMap(Map<String, String> headers) {
        try {
            return headers.get(HEADER_PROVIDER_DATA_ID);
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean isMeshRequest(HttpServletRequest request) {
        String origin = request.getHeader("mesh_origin");
        String dataId = request.getHeader("Mesh_Provider_Dataid");
        return origin != null && !origin.isEmpty()
                && dataId != null && !dataId.isEmpty();
    }
}
