package com.example.provider.utill;

import com.alibaba.fastjson2.JSONObject;

public class StringUtilTool {

    public static String convertStringToJsonString(String input) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("data", input);
            return jsonObject.toJSONString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
