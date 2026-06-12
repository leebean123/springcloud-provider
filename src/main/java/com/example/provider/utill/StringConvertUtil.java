package com.example.provider.utill;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.regex.Pattern;

/**
 * 字符串转换工具类
 * 提供各种字符串格式转换、编码解码等常用方法
 */
public class StringConvertUtil {

    private static final Pattern CAMEL_CASE_PATTERN = Pattern.compile("(?<=[a-z])(?=[A-Z])");
    private static final Pattern UNDERSCORE_PATTERN = Pattern.compile("_([a-z])");

    /**
     * 将字符串转换为驼峰命名（小驼峰）
     *
     * @param str 原始字符串（下划线分隔）
     * @return 驼峰命名字符串
     */
    public static String toCamelCase(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }

        String[] parts = str.split("_");
        StringBuilder result = new StringBuilder(parts[0].toLowerCase());
        
        for (int i = 1; i < parts.length; i++) {
            if (!parts[i].isEmpty()) {
                result.append(Character.toUpperCase(parts[i].charAt(0)))
                      .append(parts[i].substring(1).toLowerCase());
            }
        }
        
        return result.toString();
    }

    /**
     * 将字符串转换为帕斯卡命名（大驼峰）
     *
     * @param str 原始字符串（下划线分隔）
     * @return 帕斯卡命名字符串
     */
    public static String toPascalCase(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }

        String camelCase = toCamelCase(str);
        return Character.toUpperCase(camelCase.charAt(0)) + camelCase.substring(1);
    }

    /**
     * 将驼峰命名转换为下划线命名
     *
     * @param str 驼峰命名字符串
     * @return 下划线命名字符串
     */
    public static String toSnakeCase(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }

        return CAMEL_CASE_PATTERN.matcher(str).replaceAll("_").toLowerCase();
    }

    /**
     * 将字符串转换为大写
     *
     * @param str 原始字符串
     * @return 大写字符串
     */
    public static String toUpperCase(String str) {
        return str != null ? str.toUpperCase() : null;
    }

    /**
     * 将字符串转换为小写
     *
     * @param str 原始字符串
     * @return 小写字符串
     */
    public static String toLowerCase(String str) {
        return str != null ? str.toLowerCase() : null;
    }

    /**
     * 将字符串首字母大写
     *
     * @param str 原始字符串
     * @return 首字母大写的字符串
     */
    public static String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }

    /**
     * 将字符串首字母小写
     *
     * @param str 原始字符串
     * @return 首字母小写的字符串
     */
    public static String decapitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return Character.toLowerCase(str.charAt(0)) + str.substring(1);
    }

    /**
     * 将字符串转换为Base64编码
     *
     * @param str 原始字符串
     * @return Base64编码字符串
     */
    public static String toBase64(String str) {
        if (str == null) {
            return null;
        }
        return Base64.getEncoder().encodeToString(str.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 将Base64编码字符串解码
     *
     * @param base64Str Base64编码字符串
     * @return 解码后的字符串
     */
    public static String fromBase64(String base64Str) {
        if (base64Str == null) {
            return null;
        }
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(base64Str);
            return new String(decodedBytes, StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * 将字符串转换为URL编码（UTF-8）
     *
     * @param str 原始字符串
     * @return URL编码字符串
     */
    public static String toUrlEncode(String str) {
        if (str == null) {
            return null;
        }
        return java.net.URLEncoder.encode(str, StandardCharsets.UTF_8);
    }

    /**
     * 将URL编码字符串解码
     *
     * @param urlEncodedStr URL编码字符串
     * @return 解码后的字符串
     */
    public static String fromUrlEncode(String urlEncodedStr) {
        if (urlEncodedStr == null) {
            return null;
        }
        return java.net.URLDecoder.decode(urlEncodedStr, StandardCharsets.UTF_8);
    }

    /**
     * 将字符串转换为HTML实体编码
     *
     * @param str 原始字符串
     * @return HTML实体编码字符串
     */
    public static String toHtmlEncode(String str) {
        if (str == null) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        for (char c : str.toCharArray()) {
            switch (c) {
                case '<':
                    sb.append("&lt;");
                    break;
                case '>':
                    sb.append("&gt;");
                    break;
                case '&':
                    sb.append("&amp;");
                    break;
                case '"':
                    sb.append("&quot;");
                    break;
                case '\'':
                    sb.append("&#39;");
                    break;
                default:
                    sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * 将HTML实体编码字符串解码
     *
     * @param htmlEncodedStr HTML实体编码字符串
     * @return 解码后的字符串
     */
    public static String fromHtmlEncode(String htmlEncodedStr) {
        if (htmlEncodedStr == null) {
            return null;
        }

        return htmlEncodedStr.replace("&lt;", "<")
                            .replace("&gt;", ">")
                            .replace("&amp;", "&")
                            .replace("&quot;", "\"")
                            .replace("&#39;", "'");
    }

    /**
     * 将字符串转换为字节数组（UTF-8）
     *
     * @param str 原始字符串
     * @return 字节数组
     */
    public static byte[] toBytes(String str) {
        return str != null ? str.getBytes(StandardCharsets.UTF_8) : null;
    }

    /**
     * 将字节数组转换为字符串（UTF-8）
     *
     * @param bytes 字节数组
     * @return 字符串
     */
    public static String fromBytes(byte[] bytes) {
        return bytes != null ? new String(bytes, StandardCharsets.UTF_8) : null;
    }

    /**
     * 将字符串转换为整数
     *
     * @param str 原始字符串
     * @param defaultValue 转换失败时的默认值
     * @return 整数值
     */
    public static int toInt(String str, int defaultValue) {
        if (str == null || str.isEmpty()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(str.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * 将字符串转换为长整型
     *
     * @param str 原始字符串
     * @param defaultValue 转换失败时的默认值
     * @return 长整型值
     */
    public static long toLong(String str, long defaultValue) {
        if (str == null || str.isEmpty()) {
            return defaultValue;
        }
        try {
            return Long.parseLong(str.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * 将字符串转换为双精度浮点数
     *
     * @param str 原始字符串
     * @param defaultValue 转换失败时的默认值
     * @return 双精度浮点数值
     */
    public static double toDouble(String str, double defaultValue) {
        if (str == null || str.isEmpty()) {
            return defaultValue;
        }
        try {
            return Double.parseDouble(str.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * 将字符串转换为布尔值
     *
     * @param str 原始字符串
     * @param defaultValue 转换失败时的默认值
     * @return 布尔值
     */
    public static boolean toBoolean(String str, boolean defaultValue) {
        if (str == null || str.isEmpty()) {
            return defaultValue;
        }
        
        String lowerStr = str.trim().toLowerCase();
        if ("true".equals(lowerStr) || "1".equals(lowerStr) || "yes".equals(lowerStr) || "on".equals(lowerStr)) {
            return true;
        } else if ("false".equals(lowerStr) || "0".equals(lowerStr) || "no".equals(lowerStr) || "off".equals(lowerStr)) {
            return false;
        } else {
            return defaultValue;
        }
    }

    /**
     * 将整数转换为字符串
     *
     * @param value 整数值
     * @return 字符串
     */
    public static String fromInt(int value) {
        return String.valueOf(value);
    }

    /**
     * 将长整型转换为字符串
     *
     * @param value 长整型值
     * @return 字符串
     */
    public static String fromLong(long value) {
        return String.valueOf(value);
    }

    /**
     * 将双精度浮点数转换为字符串
     *
     * @param value 双精度浮点数值
     * @return 字符串
     */
    public static String fromDouble(double value) {
        return String.valueOf(value);
    }

    /**
     * 将布尔值转换为字符串
     *
     * @param value 布尔值
     * @return 字符串
     */
    public static String fromBoolean(boolean value) {
        return String.valueOf(value);
    }

    /**
     * 将字符串反转
     *
     * @param str 原始字符串
     * @return 反转后的字符串
     */
    public static String reverse(String str) {
        if (str == null) {
            return null;
        }
        return new StringBuilder(str).reverse().toString();
    }

    /**
     * 去除字符串两端的空白字符
     *
     * @param str 原始字符串
     * @return 去除空白后的字符串
     */
    public static String trim(String str) {
        return str != null ? str.trim() : null;
    }

    /**
     * 去除字符串中的所有空白字符
     *
     * @param str 原始字符串
     * @return 去除所有空白后的字符串
     */
    public static String removeAllWhitespace(String str) {
        if (str == null) {
            return null;
        }
        return str.replaceAll("\\s+", "");
    }

    /**
     * 将字符串用指定分隔符连接
     *
     * @param delimiter 分隔符
     * @param parts 要连接的部分
     * @return 连接后的字符串
     */
    public static String join(String delimiter, String... parts) {
        if (parts == null || parts.length == 0) {
            return "";
        }
        
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            if (parts[i] != null) {
                sb.append(parts[i]);
                if (i < parts.length - 1) {
                    sb.append(delimiter);
                }
            }
        }
        return sb.toString();
    }

    /**
     * 检查字符串是否为空或null
     *
     * @param str 要检查的字符串
     * @return 是否为空或null
     */
    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }

    /**
     * 检查字符串是否不为空且不为null
     *
     * @param str 要检查的字符串
     * @return 是否不为空且不为null
     */
    public static boolean isNotEmpty(String str) {
        return str != null && !str.isEmpty();
    }

    /**
     * 检查字符串是否为空白（null、空字符串或只包含空白字符）
     *
     * @param str 要检查的字符串
     * @return 是否为空白
     */
    public static boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }

    /**
     * 检查字符串是否不为空白
     *
     * @param str 要检查的字符串
     * @return 是否不为空白
     */
    public static boolean isNotBlank(String str) {
        return str != null && !str.trim().isEmpty();
    }
}