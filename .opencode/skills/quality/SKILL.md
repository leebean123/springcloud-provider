---
name: quality
description: 代码质量审查 - 空指针/资源泄漏/线程安全/异常/性能/规范/日志
---

# 审查规则

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
