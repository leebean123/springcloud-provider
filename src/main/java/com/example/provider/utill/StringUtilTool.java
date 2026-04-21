package com.example.provider.utill;

import com.alibaba.fastjson2.JSONObject;

public class StringUtilTool {

    public static String convertStringToJsonString(String input) {
        try {
            JSONObject jsonObject = new JSONObject();
            if (jsonObject != null) {
                jsonObject.put("data", input);
                return jsonObject.toJSONString();
            }
            return "{}";
        } catch (RuntimeException e) {
            if (e != null) {
                e.printStackTrace();
            }
            return "{}";
        }
    }
}
