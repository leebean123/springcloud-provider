package com.example.provider.utill;

import com.alibaba.fastjson2.JSONObject;

public class StudentDataConverter {

    @Deprecated
    public static JSONObject convertToV1Format(Long id, String name, Integer age) {
        JSONObject result = new JSONObject();
        result.put("studentId", id);
        result.put("studentName", name);
        result.put("studentAge", age);
        return result;
    }

    public static JSONObject convertToV2Format(Long id, String name, Integer age) {
        JSONObject result = new JSONObject();
        result.put("id", id);
        result.put("name", name);
        result.put("age", age);
        return result;
    }

    public static JSONObject[] batchConvertV1(Long[] ids, String[] names, Integer[] ages) {
        JSONObject[] results = new JSONObject[ids.length];
        for (int i = 0; i < ids.length; i++) {
            results[i] = convertToV1Format(ids[i], names[i], ages[i]);
        }
        return results;
    }
}
