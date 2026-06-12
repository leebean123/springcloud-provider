package com.example.provider.utill;

import com.alibaba.fastjson2.JSONObject;

/**
 * 学生数据转换器。
 *
 * 问题 1: convertToV1Format() 被标记为 @Deprecated，但并未在 Javadoc 中
 *         说明替代方案。
 *
 * 问题 2: 虽然标记了 @Deprecated，但项目中 StudentNewController.java 和
 *         StudentInfoCompositeService.java 可能仍在调用此方法。
 *         审查者需要全局搜索 convertToV1Format 的调用方。
 *
 * 问题 3: 替代方法 convertToV2Format() 的返回格式与 V1 不兼容，
 *         调用方如果从 V1 切换到 V2 会产生 JSON 结构变化。
 * @Deprecated 标记的方法如果没有调用方则应该直接删除。
 */
public class StudentDataConverter {

    /**
     * 转换为 V1 格式。
     *
     * @Deprecated 使用 convertToV2Format() 替代
     */
    @Deprecated
    public static JSONObject convertToV1Format(Long id, String name, Integer age) {
        JSONObject result = new JSONObject();
        result.put("studentId", id);
        result.put("studentName", name);
        result.put("studentAge", age);
        return result;
    }

    /**
     * 转换为 V2 格式（推荐使用）。
     * 问题: 与 V1 的 key 命名不同（V1 是 studentId, studentName, studentAge，
     * V2 是 id, name, age），调用方从 V1 切到 V2 时需要同步修改前端。
     */
    public static JSONObject convertToV2Format(Long id, String name, Integer age) {
        JSONObject result = new JSONObject();
        result.put("id", id);
        result.put("name", name);
        result.put("age", age);
        return result;
    }

    /**
     * 批量转换。
     * 问题: 仍然使用了被弃用的 convertToV1Format()
     */
    public static JSONObject[] batchConvertV1(Long[] ids, String[] names, Integer[] ages) {
        JSONObject[] results = new JSONObject[ids.length];
        for (int i = 0; i < ids.length; i++) {
            // 问题: 自身类中还在调用自己标记为 @Deprecated 的方法
            results[i] = convertToV1Format(ids[i], names[i], ages[i]);
        }
        return results;
    }
}
