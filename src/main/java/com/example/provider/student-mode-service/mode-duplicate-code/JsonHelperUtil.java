package com.example.provider.utill;

import com.alibaba.fastjson2.JSONObject;

/**
 * JSON 辅助工具类。
 *
 * 问题 1: 与项目中现有的 StringUtilTool 功能完全重复
 *         StringUtilTool.convertStringToJsonString("hello") 返回 {"data":"hello"}
 *         JsonHelperUtil.wrapAsJson("hello") 返回 {"data":"hello"}
 *         两个类的逻辑一模一样
 *
 * 问题 2: 项目中已经有 StringUtilTool 了，还要再写一个 JsonHelperUtil，
 *         开发者不知道该用哪个，导致代码散落在两个工具类中
 *
 * 问题 3: 同样的 null 检查错误在 StringUtilTool 中存在，
 *         这里也犯了同样的错误 —— new JSONObject() 后检查是否为 null
 */
public class JsonHelperUtil {

    /**
     * 将字符串包装为 JSON 格式。
     * 问题: 与 StringUtilTool.convertStringToJsonString() 完全重复
     *
     * 对比 StringUtilTool.java:7-21:
     *   public static String convertStringToJsonString(String input) {
     *       try {
     *           JSONObject jsonObject = new JSONObject();
     *           if (jsonObject != null) {       // ← 同样的无意义 null 检查
     *               jsonObject.put("data", input);
     *               return jsonObject.toJSONString();
     *           }
     *           return "{}";
     *       } catch (RuntimeException e) {
     *           if (e != null) {               // ← 同样的无意义 null 检查
     *               e.printStackTrace();
     *           }
     *           return "{}";
     *       }
     *   }
     */
    public static String wrapAsJson(String data) {
        try {
            JSONObject json = new JSONObject();
            // 问题: new JSONObject() 不可能为 null，无意义的检查
            if (json != null) {
                json.put("data", data);
                return json.toJSONString();
            }
            return "{}";
        } catch (RuntimeException e) {
            // 问题: catch 到的异常不可能为 null，无意义的检查
            if (e != null) {
                e.printStackTrace();
            }
            return "{}";
        }
    }

    /**
     * 将多个键值对包装为 JSON。
     * 问题: 与 wrapAsJson() 功能重叠，且没有使用项目中已有的 StringConvertUtil
     */
    public static String wrapAsJsonWithKey(String key, String value) {
        JSONObject json = new JSONObject();
        json.put(key, value);
        return json.toJSONString();
    }
}
