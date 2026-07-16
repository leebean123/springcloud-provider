---
name: code-review-general
description: 通用代码审查 - 质量/安全/数据完整性统一规则
---

# 通用代码审查规则

## 空指针防御
- 方法参数未做 null 校验直接调用方法（.toString/.length/.equals）
- 链式调用中间节点可能为 null
- Map/List 获取后未判空直接使用
- 从数据库/Redis 查询的对象字段直接使用未判空
- 三目运算符自动拆箱可能引发 NPE

## 资源泄漏
- IO 流未使用 try-with-resources
- JDBC Connection/Statement/ResultSet 未在 finally 关闭
- 线程池 ExecutorService 使用后未 shutdown
- ZooKeeper/Redis 连接未正确释放

## 线程安全
- 共享变量多线程未加锁或未使用 Atomic
- DCL 单例未使用 volatile
- 死锁风险：不同顺序获取多个锁
- HashMap/ArrayList 多线程下使用

## 异常处理
- catch 空块吞异常
- 事务方法内 catch 后未手动回滚
- finally 块中使用 return 覆盖异常

## 性能坏味道
- 循环中使用 + 拼接字符串
- 频繁自动拆装箱
- 无界集合持续增长 OOM
- double/float 用于金额计算

## 编码规范
- equals 重写未重写 hashCode
- 魔法值硬编码未定义为常量
- 命名不符合驼峰规范
- 返回空集合返回 null

## 日志安全
- 日志中打印身份证/手机号/卡号等敏感信息
- 使用 System.out.println 代替 Logger
- 循环体/高频路径打印日志造成日志风暴

## 敏感数据扫描
- 密码/密钥/AK/SK 硬编码在代码中
- 响应报文中包含明文敏感字段
- 敏感数据未脱敏直接存储或传输

## 加密合规
- 使用已弃用的加密算法（DES/RC4）
- 密钥硬编码或密钥管理不规范
- 未使用 HTTPS/TLS 传输加密

## 认证鉴权
- 敏感接口缺少权限注解
- 水平越权风险
- 内部接口暴露到外网无额外鉴权

## SQL注入检测
- 动态 SQL 拼接用户输入
- MyBatis 中使用 ${} 而非 #{}

## 数据完整性
- 跨表操作事务注解（@Transactional）是否合理
- 跨库操作是否使用分布式事务
- 事务方法内部 try-catch 是否导致事务未回滚
- 跨系统调用失败是否有补偿机制
- 关键操作是否有审计日志
- 内外部是否定期对账
