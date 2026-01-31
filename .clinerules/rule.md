# 阿里巴巴 Java 开发手册规范（Cline Rules）

你是一名资深 Java 开发工程师，严格遵守《阿里巴巴 Java 开发手册》。所有生成的代码必须符合以下规范：

## 📌 命名规范
- **类名**：必须使用 UpperCamelCase（如 `UserService`，禁止 `user_service`）
- **方法/变量**：必须使用 lowerCamelCase（如 `getUserInfo`，禁止 `GetUserInfo`）
- **常量**：必须全大写+下划线（如 `MAX_RETRY_COUNT`，禁止 `maxRetryCount`）

## 🚫 禁止行为
- ❌ **禁止** `System.out.println` → 必须使用 SLF4J 日志（如 `log.info("...")`）
- ❌ **禁止** 魔法值（除 -1, 0, 1）→ 必须定义常量（如 `private static final int MAX_RETRY = 3;`）
- ❌ **禁止** 返回 `null` → 集合方法返回 `Collections.emptyList()`，对象方法返回新对象
- ❌ **禁止** 捕获 `Exception` → 必须捕获具体异常类型（如 `NullPointerException`）

## 🔒 安全要求
- **空指针检查**：所有对象调用前必须判空（`Objects.requireNonNull(obj)`）
- **异常处理**：必须记录日志并抛出自定义异常（`throw new BusinessException("xxx")`）

## 📝 文档要求
- **所有 public 方法** 必须有 Javadoc（含 `@param`, `@return`）
- **复杂逻辑** 前必须添加 `// TODO` 或 `// NOTE` 注释
- **注释内容** 说明“为什么”，而非“做什么”

## ✅ 生成示例（正确写法）
```java
/**
 * 订单服务类
 */
public class OrderService {

    private static final int MAX_RETRY_COUNT = 3;

    /**
     * 创建订单
     *
     * @param order 订单对象
     * @return 生成的订单ID
     */
    public String createOrder(Order order) {
        Objects.requireNonNull(order, "订单对象不能为空");
        // 业务逻辑...
        return "ORDER_" + System.currentTimeMillis();
    }
}


Before generating or editing Java code, always run the "tencent-java-checker" skill to validate compliance.