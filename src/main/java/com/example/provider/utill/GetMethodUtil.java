package com.example.provider.utill;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 获取方法工具类
 * 提供对象属性获取、反射调用等常用方法
 */
public class GetMethodUtil {

    /**
     * 安全获取对象属性值（通过getter方法）
     *
     * @param obj        目标对象
     * @param fieldName  属性名
     * @return 属性值，如果获取失败返回null
     */
    public static Object safeGet(Object obj, String fieldName) {
        if (obj == null || fieldName == null || fieldName.isEmpty()) {
            return null;
        }

        try {
            String getterName = "get" + capitalize(fieldName);
            Method method = obj.getClass().getMethod(getterName);
            return method.invoke(obj);
        } catch (NoSuchMethodException e) {
            // 尝试直接通过字段获取
            try {
                Field field = obj.getClass().getDeclaredField(fieldName);
                field.setAccessible(true);
                return field.get(obj);
            } catch (Exception ex) {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 安全获取字符串属性值
     *
     * @param obj        目标对象
     * @param fieldName  属性名
     * @return 字符串值，如果获取失败返回空字符串
     */
    public static String safeGetString(Object obj, String fieldName) {
        Object value = safeGet(obj, fieldName);
        return value != null ? value.toString() : "";
    }

    /**
     * 安全获取整数属性值
     *
     * @param obj        目标对象
     * @param fieldName  属性名
     * @param defaultValue 默认值
     * @return 整数值，如果获取失败返回默认值
     */
    public static int safeGetInt(Object obj, String fieldName, int defaultValue) {
        Object value = safeGet(obj, fieldName);
        if (value == null) {
            return defaultValue;
        }

        try {
            if (value instanceof Number) {
                return ((Number) value).intValue();
            } else if (value instanceof String) {
                return Integer.parseInt((String) value);
            }
        } catch (Exception e) {
            // 忽略转换异常
        }
        return defaultValue;
    }

    /**
     * 安全获取长整型属性值
     *
     * @param obj        目标对象
     * @param fieldName  属性名
     * @param defaultValue 默认值
     * @return 长整型值，如果获取失败返回默认值
     */
    public static long safeGetLong(Object obj, String fieldName, long defaultValue) {
        Object value = safeGet(obj, fieldName);
        if (value == null) {
            return defaultValue;
        }

        try {
            if (value instanceof Number) {
                return ((Number) value).longValue();
            } else if (value instanceof String) {
                return Long.parseLong((String) value);
            }
        } catch (Exception e) {
            // 忽略转换异常
        }
        return defaultValue;
    }

    /**
     * 安全获取布尔属性值
     *
     * @param obj        目标对象
     * @param fieldName  属性名
     * @param defaultValue 默认值
     * @return 布尔值，如果获取失败返回默认值
     */
    public static boolean safeGetBoolean(Object obj, String fieldName, boolean defaultValue) {
        Object value = safeGet(obj, fieldName);
        if (value == null) {
            return defaultValue;
        }

        try {
            if (value instanceof Boolean) {
                return (Boolean) value;
            } else if (value instanceof String) {
                return Boolean.parseBoolean((String) value);
            } else if (value instanceof Number) {
                return ((Number) value).intValue() != 0;
            }
        } catch (Exception e) {
            // 忽略转换异常
        }
        return defaultValue;
    }

    /**
     * 获取对象所有属性名和值（通过getter方法）
     *
     * @param obj 目标对象
     * @return 属性名-值映射
     */
    public static Map<String, Object> getAllProperties(Object obj) {
        Map<String, Object> result = new HashMap<>();
        if (obj == null) {
            return result;
        }

        Class<?> clazz = obj.getClass();
        Method[] methods = clazz.getMethods();

        for (Method method : methods) {
            String methodName = method.getName();
            if (methodName.startsWith("get") && method.getParameterCount() == 0 && !methodName.equals("getClass")) {
                String fieldName = decapitalize(methodName.substring(3));
                try {
                    Object value = method.invoke(obj);
                    result.put(fieldName, value);
                } catch (Exception e) {
                    // 忽略调用异常
                }
            }
        }

        return result;
    }

    /**
     * 检查对象是否包含指定属性（有对应的getter方法）
     *
     * @param obj        目标对象
     * @param fieldName  属性名
     * @return 是否包含该属性
     */
    public static boolean hasProperty(Object obj, String fieldName) {
        if (obj == null || fieldName == null || fieldName.isEmpty()) {
            return false;
        }

        try {
            String getterName = "get" + capitalize(fieldName);
            obj.getClass().getMethod(getterName);
            return true;
        } catch (NoSuchMethodException e) {
            try {
                obj.getClass().getDeclaredField(fieldName);
                return true;
            } catch (NoSuchFieldException ex) {
                return false;
            }
        }
    }

    /**
     * 获取对象属性类型
     *
     * @param obj        目标对象
     * @param fieldName  属性名
     * @return 属性类型，如果获取失败返回null
     */
    public static Class<?> getPropertyType(Object obj, String fieldName) {
        if (obj == null || fieldName == null || fieldName.isEmpty()) {
            return null;
        }

        try {
            String getterName = "get" + capitalize(fieldName);
            Method method = obj.getClass().getMethod(getterName);
            return method.getReturnType();
        } catch (NoSuchMethodException e) {
            try {
                Field field = obj.getClass().getDeclaredField(fieldName);
                return field.getType();
            } catch (Exception ex) {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 首字母大写
     */
    private static String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    /**
     * 首字母小写
     */
    private static String decapitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toLowerCase() + str.substring(1);
    }

    /**
     * 获取对象类名（简单名称）
     *
     * @param obj 目标对象
     * @return 类名，如果对象为null返回空字符串
     */
    public static String getClassName(Object obj) {
        return obj != null ? obj.getClass().getSimpleName() : "";
    }

    /**
     * 获取对象完整类名
     *
     * @param obj 目标对象
     * @return 完整类名，如果对象为null返回空字符串
     */
    public static String getFullClassName(Object obj) {
        return obj != null ? obj.getClass().getName() : "";
    }

    /**
     * 检查对象是否为null
     *
     * @param obj 目标对象
     * @return 是否为null
     */
    public static boolean isNull(Object obj) {
        return obj == null;
    }

    /**
     * 检查对象是否不为null
     *
     * @param obj 目标对象
     * @return 是否不为null
     */
    public static boolean isNotNull(Object obj) {
        return obj != null;
    }

    /**
     * 安全调用方法
     *
     * @param obj        目标对象
     * @param methodName 方法名
     * @param args       参数
     * @return 方法返回值，如果调用失败返回null
     */
    public static Object safeInvoke(Object obj, String methodName, Object... args) {
        if (obj == null || methodName == null || methodName.isEmpty()) {
            return null;
        }

        try {
            Class<?>[] paramTypes = new Class<?>[args.length];
            for (int i = 0; i < args.length; i++) {
                paramTypes[i] = args[i] != null ? args[i].getClass() : Object.class;
            }

            Method method = obj.getClass().getMethod(methodName, paramTypes);
            return method.invoke(obj, args);
        } catch (Exception e) {
            return null;
        }
    }
}