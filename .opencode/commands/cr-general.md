---
description: AI 代码评审 - 支持通用和领域审查维度，按 skills=xxx 参数执行
agent: ocr-cr-expert
model: deepseek/deepseek-chat
---

# 代码评审指令

## 1. 解析参数
从用户消息中解析 `skills=` 参数：
- `skills=quality` → 仅通用质量
- `skills=quality,security,payment` → 多维度组合
- `skills=all` → 全部
- 未指定 → 展示列表让用户选择

## 2. 可用维度
| 标识 | 类别 | 说明 |
|------|------|------|
| quality | 通用 | 代码质量：空指针/资源泄漏/线程安全/异常/性能/规范/日志 |
| security | 通用 | 安全审查：敏感数据/加密/鉴权/SQL注入 |
| data | 通用 | 数据完整性：事务边界/补偿/审计/对账 |
| payment | 领域 | 支付交易：幂等性/资金一致性/状态机/风控/账务 |
| settlement | 领域 | 清算：清算金额/手续费/对账 |
| aml | 领域 | 反洗钱：大额上报/可疑交易/数据留存 |

## 3. 执行
按选中的维度执行审查，输出 P0/P1/P2 分级报告。
