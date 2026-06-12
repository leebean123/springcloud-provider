package com.example.provider.utill;

import com.alibaba.fastjson2.JSONObject;

public class JsonHelperUtil {

    public static String wrapAsJson(String data) {
        try {
            JSONObject json = new JSONObject();
            if (json != null) {
                json.put("data", data);
                return json.toJSONString();
            }
            return "{}";
        } catch (RuntimeException e) {
            if (e != null) {
                e.printStackTrace();
            }
            return "{}";
        }
    }

    public static String wrapAsJsonWithKey(String key, String value) {
        JSONObject json = new JSONObject();
        json.put(key, value);
        return json.toJSONString();
    }
}
