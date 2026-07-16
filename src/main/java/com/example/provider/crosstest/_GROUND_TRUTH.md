# 跨文件评审能力测试集 Ground Truth

> 聚焦 **Level 1-6 跨文件场景**，对比 单文件方法拆分 vs open-code-review(OCR)

---

## Level 1：同文件跨方法（A调B，B在同文件有缺陷）

| ID | L | 文件 | 问题 | P | 单文件 | OCR | 原因 |
|----|---|------|------|---|:-----:|:---:|------|
| L1-1 | 1 | OrderProcessor.java:12 | process() 调 findOrder()，findOrder 返回 null，process 未判空调 toUpperCase | P0 | ❌ | ✅ | **单文件**：process 被拆分看不到 findOrder 实现；**OCR**：可读到 findOrder 返回 null |
| L1-2 | 1 | OrderProcessor.java:24 | batchProcess() 调 validate()，validate 抛异常，batch 循环中断 | P1 | ❌ | ✅ | **单文件**：看不到 validate 实现；**OCR**：可读到 validate 抛异常 |
| L1-3 | 1 | OrderProcessor.java:35 | findOrder() id 为 null 时返回 null | P0 | ✅ | ✅ | 方法体内直接可见 |

## Level 2：类级别（字段/注解/继承）

| ID | L | 文件 | 问题 | P | 单文件 | OCR | 原因 |
|----|---|------|------|---|:-----:|:---:|------|
| L2-1 | 2 | DclSingleton.java:5 | DCL 单例 static 字段缺 volatile | P0 | ❌ | ✅ | 只看 getInstance() 看不到字段定义 |
| L2-2 | 2 | EqualsNoHashCode.java:7 | 重写 equals 未重写 hashCode | P1 | ❌ | ✅ | 只看 equals 方法体，不知道类缺 hashCode |
| L2-3 | 2 | MemberThreadSafe.java:5 | @Service 单例 + mutable 成员变量 + 无同步 | P0 | ❌ | ✅ | 看不到 @Service 和成员变量 |
| L2-4 | 2 | ResourceLeakAcross.java:11 | InputStream 成员变量跨方法传递，异常路径泄漏 | P1 | ❌ | ✅ | open() 局部看没泄漏，结合类看才发现 |

## Level 3：跨文件直接调用

| ID | L | 文件 | 问题 | P | 单文件 | OCR | 原因 |
|----|---|------|------|---|:-----:|:---:|------|
| L3-1 | 3 | UserService.java:11 | getUser() 返回 null | P0 | ✅ | ✅ | 方法体内可见 |
| L3-2 | 3 | UserController.java:11 | 调 getUser(id) 未判空直接调 getName | P0 | ❌ | ⚠️ | **单文件**：不知道 getUser 返回 null；**OCR**：追查到 Service 可发现 |

## Level 4：契约违约（标注与实现不一致）

| ID | L | 文件 | 问题 | P | 单文件 | OCR | 原因 |
|----|---|------|------|---|:-----:|:---:|------|
| L4-1 | 4 | InventoryService.java:16 | Javadoc 说"不会为 null"，但返回了 null | P0 | ❌ | ⚠️ | 需要同时看到 Javadoc 和方法体 |
| L4-2 | 4 | InventoryController.java:12 | 信任契约未判空，直接调 isEnough | P0 | ❌ | ⚠️ | **关键用例**：单文件必然报假阳性在 Controller，OCR 追踪到 Service 可纠偏 |

## Level 5：间接调用链

| ID | L | 文件 | 问题 | P | 单文件 | OCR | 原因 |
|----|---|------|------|---|:-----:|:---:|------|
| L5-1 | 5 | OrderService.java:12 | createOrder 无 @Transactional，下游失败无法回滚 | P0 | ❌ | ❌ | 需理解事务边界 |
| L5-2 | 5 | PaymentService.java:10 | 远程调用缺超时设置，线程阻塞 | P1 | ❌ | ❌ | 需跨两层 |
| L5-3 | 5 | AccountDao.java:8 | 不同数据源，跨库无分布式事务 | P0 | ❌ | ❌ | 需跨三层 |

## Level 6：架构/设计

| ID | L | 文件 | 问题 | P | 单文件 | OCR | 原因 |
|----|---|------|------|---|:-----:|:---:|------|
| L6-1 | 6 | CircularDependencyConfig.java:8 | BeanA ↔ BeanB 构造期循环依赖 | P1 | ❌ | ❌ | 两个 @Bean 分开看没问题 |
| L6-2 | 6 | LayerViolationController.java:14 | Controller 直接操作 DataSource，跳过 Service 层 | P1 | ❌ | ❌ | 需要理解分层架构规范 |
| L6-3 | 6 | NoTransactionService.java:12 | 本类方法调用 @Transactional，AOP 代理失效 | P0 | ❌ | ❌ | 需要理解 Spring AOP 机制 |

---

## 汇总

| Level | 场景 | 用例数 | 单文件 | OCR |
|-------|------|:-----:|:-----:|:---:|
| 1 | 同文件跨方法 | 3 | 1/3 | **3/3** |
| 2 | 类级别（字段/注解） | 4 | 0/4 | **4/4** |
| 3 | 跨文件直接调用 | 2 | 1/2 | **2/2** |
| 4 | 契约违约 | 2 | 0/2 | ⚠️ 有条件 |
| 5 | 间接调用链 | 3 | 0/3 | 0/3 |
| 6 | 架构/设计 | 3 | 0/3 | 0/3 |
| **合计** | | **17** | **2/17** | **12/17** |
