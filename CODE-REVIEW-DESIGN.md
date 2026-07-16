# 金融级代码评审系统设计文档

> 适用于金融行业 Spring Cloud / Java 技术栈，基于 opencode + Agent + Skills 架构的 AI 代码评审方案

---

## 目录

1. [总体架构](#1-总体架构)
2. [分层设计](#2-分层设计)
3. [Agent 定义](#3-agent-定义)
4. [Skills 定义](#4-skills-定义)
5. [Commands 定义](#5-commands-定义)
6. [文件目录结构](#6-文件目录结构)
7. [评审报告分级标准](#7-评审报告分级标准)
8. [实施路线图](#8-实施路线图)
9. [扩展指南](#9-扩展指南)

---

## 1. 总体架构

```
┌─────────────────────────────────────────────────────────────┐
│                    用户交互层 (Commands)                      │
│  ┌──────────┐ ┌──────────┐ ┌──────┐ ┌────────┐ ┌────────┐  │
│  │ cr-full  │ │cr-trans. │ │cr-sec│ │cr-qual │ │cr-quick│  │
│  │ 全量评审  │ │ 交易评审  │ │安全  │ │ 质量   │ │ 快速   │  │
│  └────┬─────┘ └────┬─────┘ └──┬───┘ └───┬────┘ └───┬────┘  │
├───────┴────────────┴──────────┴──────────┴──────────┴──────┤
│                    编排层 (Agent)                            │
│              ┌─────────────────────────────┐                │
│              │     review-master / sisyphus │                │
│              │     阶段编排 / 结果汇总/分级   │                │
│              └────┬────┬────┬────┬────┬────┘                │
├───────────────────┼────┼────┼────┼────┼────────────────────┤
│                   │    │    │    │    │                      │
│          ┌────────┴┐ ┌─┴──┐ ┌┴───┐ ┌┴───┐ ┌┴────────┐      │
│          │quality  │ │sec │ │txn │ │comp│ │data-int │      │
│          │ 通用质量 │ │安全│ │交易│ │合规│ │ 数据完整 │      │
│          └────┬────┘ └─┬──┘ └┬───┘ └┬───┘ └────┬────┘      │
├───────────────┼────────┼──────┼──────┼──────────┼──────────┤
│    Skills     │        │      │      │          │           │
│           ┌───┴───┐┌──┴──┐┌──┴──┐┌──┴──┐┌─────┴─────┐      │
│           │空指针  ││敏感  ││幂等性││反洗钱││ 事务边界  │      │
│           │资源泄漏││加密  ││资金  ││数据  ││ 补偿机制  │      │
│           │线程安全││鉴权  ││状态机││尽调  ││ 审计日志  │      │
│           │异常处理││SQL  ││风控  ││报送  ││ 对账检查  │      │
│           │性能   ││注入  ││账务  ││     ││           │      │
│           │规范   ││     ││     ││     ││           │      │
│           │日志   ││     ││     ││     ││           │      │
│           └───────┘└─────┘└─────┘└─────┘└───────────┘      │
├─────────────────────────────────────────────────────────────┤
│                    工具执行层                                 │
│          ┌──────────┐  ┌──────────┐  ┌──────────┐          │
│          │  git diff │  │ocr review│  │ 代码分析  │          │
│          └─────┬────┘  └────┬─────┘  └────┬─────┘          │
├────────────────┼────────────┼──────────────┼───────────────┤
│                └────────────┼──────────────┘                │
│                             ▼                                │
│                    ┌────────────────┐                        │
│                    │ deepseek-chat  │                        │
│                    │   LLM 推理引擎  │                        │
│                    └────────┬───────┘                        │
├─────────────────────────────┼──────────────────────────────┤
│                             ▼                                │
│                    审查结果回流到编排层                        │
│              ┌─────────────────────────────┐                │
│              │  review-master 汇总          │                │
│              │  P0(阻断) / P1(警告) / P2(建议)│                │
│              └─────────────────────────────┘                │
└─────────────────────────────────────────────────────────────┘
```

**核心数据流：**

| 方向 | 流程 |
|------|------|
| **正向（指令下发）** | 用户 → Command → 编排Agent → 领域Agent → Skills → 工具 → LLM |
| **反向（结果回流）** | LLM → 编排Agent 汇总 → P0/P1/P2分级报告 → 用户 |

---

## 2. 分层设计

### 2.1 六层架构

| 层级 | 组件 | 职责 | 维护者 |
|------|------|------|--------|
| **用户交互层** | Commands | 不同评审场景入口，`/cr-xxx` 触发 | 各团队 |
| **编排层** | review-master / sisyphus Agent | 编排多阶段审查、汇总结果、分级输出 | 架构组 |
| **领域Agent层** | 5个领域Agent | 聚合对应领域Skills，按维度执行审查 | 领域owner |
| **Skills层** | 24个Skill | 具体的审查规则，每个Skill专注一个检查点 | 规则owner |
| **工具执行层** | git / ocr / 代码分析 | 获取变更、运行审查、静态分析 | 基础设施 |
| **AI模型层** | deepseek-chat | LLM推理引擎，执行审查判断 | AI平台 |

### 2.2 评审阶段Pipeline

`/cr-full` 执行时的完整Pipeline：

```
阶段1 ─ 通用代码质量审查 (quality-review)
  ├─ 空指针防御
  ├─ 资源泄漏检测
  ├─ 线程安全审查
  ├─ 异常处理审查
  ├─ 性能坏味道审查
  ├─ 编码规范审查
  └─ 日志安全审查

阶段2 ─ 安全审查 (security-review)
  ├─ 敏感数据扫描
  ├─ 加密合规审查
  ├─ 认证鉴权审查
  └─ SQL注入检测

阶段3 ─ 交易业务审查 (transaction-review)
  ├─ 幂等性检查
  ├─ 资金一致性审查
  ├─ 状态机校验
  ├─ 限额风控审查
  └─ 账务合规审查

阶段4 ─ 合规审查 (compliance-review)
  ├─ 反洗钱审查
  ├─ 数据留存审查
  ├─ 客户尽职调查审查
  └─ 监管报送审查

阶段5 ─ 数据完整性审查 (data-integrity-review)
  ├─ 事务边界审查
  ├─ 补偿机制审查
  ├─ 审计日志检查
  └─ 对账检查
```

---

## 3. Agent 定义

### 3.1 review-master（编排总控）

**路径：** `.opencode/agent/review-master/SKILL.md`

```markdown
---
name: review-master
description: 代码评审总控 Agent，编排多阶段审查并输出聚合报告
tools:
  bash: true
permissions:
  bash: allow
---

# Role
你是代码评审总指挥，负责编排所有审查阶段，汇总结果并输出分级报告。

# Pipeline
对代码变更按以下阶段串行执行：

## 阶段1：通用代码质量
执行 7 项检查：空指针防御、资源泄漏、线程安全、异常处理、性能坏味道、编码规范、日志安全

## 阶段2：安全审查
执行 4 项检查：敏感数据扫描、加密合规、认证鉴权、SQL注入检测

## 阶段3：交易业务审查
执行 5 项检查：幂等性、资金一致性、状态机校验、限额风控、账务合规

## 阶段4：合规审查
执行 4 项检查：反洗钱、数据留存、客户尽职调查、监管报送

## 阶段5：数据完整性审查
执行 4 项检查：事务边界、补偿机制、审计日志、对账检查

# Output Format
汇总各阶段结果，按 P0/P1/P2 分级输出 Markdown 报告：
- **P0（阻断）**：资金安全、数据一致性、严重漏洞，必须修复后才能合码
- **P1（警告）**：线程安全、资源泄漏、业务逻辑缺陷，建议修复
- **P2（建议）**：编码规范、性能优化、日志规范，选择性修复
```

---

### 3.2 quality-review（通用代码质量）

**路径：** `.opencode/agent/quality-review/SKILL.md`

```markdown
---
name: quality-review
description: 通用代码质量审查专家，从空指针/资源泄漏/线程安全/异常/性能/规范/日志七个维度审查
tools:
  bash: true
permissions:
  bash: allow
---

# Role
你是一位拥有 10 年以上经验的 Java 代码质量审查专家，尤其擅长金融级应用的代码质量把关。

# Review Process
对传入的代码变更，依次执行以下 7 项审查技能，按顺序输出结果：

1. **空指针防御审查** — 检查所有可能 NPE 的场景
2. **资源泄漏审查** — 检查 IO/连接/线程池是否正确关闭
3. **线程安全审查** — 检查竞态、死锁、并发容器使用
4. **异常处理审查** — 检查异常是否被吞掉、事务回滚是否正确
5. **性能坏味道审查** — 检查循环拼接、拆装箱、无界集合等
6. **编码规范审查** — 检查 equals/hashCode、魔法值、命名等
7. **日志安全审查** — 检查敏感信息打印、日志级别、审计日志

# Output Format
每组结果输出 JSON 数组：
[
  {"dimension": "空指针防御", "level": "严重/警告/建议", "file": "xxx.java", "line": 23, "reason": "xxx", "fix": "xxx"}
]
```

---

### 3.3 security-review（安全审查）

**路径：** `.opencode/agent/security-review/SKILL.md`

```markdown
---
name: security-review
description: 安全审查专家，检查敏感数据泄露/加密合规/认证鉴权/SQL注入
tools:
  bash: true
permissions:
  bash: allow
---

# Role
你是金融行业安全审查专家，负责识别代码中的安全风险。

# Review Process
对代码变更依次执行 4 项安全检查：

1. **敏感数据扫描** — 日志/响应中是否泄漏手机号、身份证、卡号、密钥
2. **加密合规审查** — 传输加密(TLS)、存储加密(AES/SM4)、密钥管理
3. **认证鉴权审查** — 接口是否有权限校验、越权风险、会话管理
4. **SQL注入检测** — 动态SQL拼接、MyBatis ${} 注入风险

# Key Focus
- 所有涉及个人金融信息(PFI)的输出必须脱敏
- 密码/密钥禁止硬编码，必须使用配置中心或密钥管理服务
- 资金操作接口必须有二次鉴权或确认机制
```

---

### 3.4 transaction-review（交易业务审查）

**路径：** `.opencode/agent/transaction-review/SKILL.md`

```markdown
---
name: transaction-review
description: 关键交易审查专家，检查幂等性/资金一致性/状态机/限额风控/账务合规
tools:
  bash: true
permissions:
  bash: allow
---

# Role
你是金融级交易系统代码审查专家，精通交易生命周期管理、资金安全和风控逻辑。

# Review Process
对代码变更依次执行 5 项检查：

1. **幂等性审查** — 写接口是否有幂等保障，幂等key选取是否合理
2. **资金一致性审查** — 资金操作是否在事务内，金额计算是否使用BigDecimal
3. **状态机审查** — 状态流转是否合法，是否有并发覆盖风险
4. **限额风控审查** — 交易金额/频次是否有限额校验
5. **账务合规审查** — 记账是否满足借贷平衡，总分是否每日对账

# Output Format
[
  {"dimension": "幂等性", "level": "P0", "file": "OrderService.java", "line": 45, "reason": "xxx", "fix": "xxx"}
]
```

---

### 3.5 compliance-review（合规审查）

**路径：** `.opencode/agent/compliance-review/SKILL.md`

```markdown
---
name: compliance-review
description: 合规审查专家，检查反洗钱/数据留存/客户尽职调查/监管报送
tools:
  bash: true
permissions:
  bash: allow
---

# Role
你是金融行业合规审查专家，确保代码满足监管合规要求。

# Review Process
对代码变更依次执行 4 项检查：

1. **反洗钱(AML)审查** — 大额交易是否有上报机制，可疑交易是否标记
2. **数据留存审查** — 交易记录留存周期是否符合监管要求
3. **客户尽职调查(CDD)审查** — 开户/交易前是否完成身份核验
4. **监管报送审查** — 报送数据是否完整准确，报送时机是否合规
```

---

### 3.6 data-integrity-review（数据完整性审查）

**路径：** `.opencode/agent/data-integrity-review/SKILL.md`

```markdown
---
name: data-integrity-review
description: 数据完整性审查专家，检查事务边界/补偿机制/审计日志/对账
tools:
  bash: true
permissions:
  bash: allow
---

# Role
你是数据架构审查专家，确保数据的一致性和完整性。

# Review Process
对代码变更依次执行 4 项检查：

1. **事务边界审查** — 跨表/跨库操作事务是否合理，分布式事务方案是否恰当
2. **补偿机制审查** — 跨系统调用失败是否有补偿机制（TCC/ saga/本地消息表）
3. **审计日志审查** — 关键操作是否有完整的操作日志（操作人、时间、IP、变更前后）
4. **对账检查** — 内部账务是否每日对账，外部渠道是否定期对账
```

---

## 4. Skills 定义

### 4.1 质量 Skills（7个）

#### npe-defender — 空指针防御

**路径：** `.opencode/skills/npe-defender/SKILL.md`

```markdown
---
name: npe-defender
description: 空指针防御审查
---

# 审查规则
1. 方法参数未做null校验直接调用方法(.toString/.length/.equals)
2. 链式调用.getXxx().getYyy()中间任一节点可能为null
3. Map/List获取后未判空直接使用
4. 从数据库/Redis查询的对象字段直接使用未判空
5. 三目运算符自动拆箱可能引发NPE
6. 方法返回null调用方未处理
```

#### resource-leak-detector — 资源泄漏检测

**路径：** `.opencode/skills/resource-leak-detector/SKILL.md`

```markdown
---
name: resource-leak-detector
description: 资源泄漏检测
---

# 审查规则
1. FileInputStream/OutputStream/Reader未使用try-with-resources
2. JDBC Connection/Statement/ResultSet未在finally中关闭
3. HttpClient/OkHttp响应未关闭
4. 线程池ExecutorService使用后未shutdown
5. ZooKeeper/Redis连接未正确释放
6. 分页查询Cursor/Scroll未关闭
```

#### thread-safety-checker — 线程安全审查

**路径：** `.opencode/skills/thread-safety-checker/SKILL.md`

```markdown
---
name: thread-safety-checker
description: 线程安全审查
---

# 审查规则
1. 共享变量在多线程下未加锁或未使用Atomic
2. count++/i++等非原子操作
3. DCL单例未使用volatile
4. 死锁风险：不同顺序获取多个锁
5. HashMap在多线程环境下使用（应使用ConcurrentHashMap）
6. SimpleDateFormat作为共享变量在多线程下使用
7. ArrayList在多线程环境下使用（应使用CopyOnWriteArrayList）
8. @Service/@Controller默认单例，成员变量存在线程安全问题
```

#### exception-handling — 异常处理审查

**路径：** `.opencode/skills/exception-handling/SKILL.md`

```markdown
---
name: exception-handling
description: 异常处理审查
---

# 审查规则
1. catch(Exception e){}空catch块吞掉异常
2. catch后未打日志或未记录错误原因
3. 事务方法内catch异常后未手动回滚
4. 抛出checked exception但事务未回滚
5. finally块中使用return覆盖异常
6. 异常信息不完整，未保留原始cause
```

#### performance-smell — 性能坏味道审查

**路径：** `.opencode/skills/performance-smell/SKILL.md`

```markdown
---
name: performance-smell
description: 性能坏味道审查
---

# 审查规则
1. 循环中使用+拼接字符串（应使用StringBuilder）
2. 频繁自动拆装箱
3. 无界集合static List/Map持续增长OOM
4. 创建大量临时对象
5. 未使用批量操作（逐条insert/update）
6. double/float用于金额计算（应使用BigDecimal）
7. 大对象/大事务未分批处理
```

#### code-style — 编码规范审查

**路径：** `.opencode/skills/code-style/SKILL.md`

```markdown
---
name: code-style
description: 编码规范审查
---

# 审查规则
1. equals重写未重写hashCode
2. 魔法值硬编码未定义为常量
3. 命名不符合驼峰规范
4. 过长方法超过80行未拆分
5. 未使用的import/变量/方法
6. 返回空集合返回null（应返回Collections.emptyList()）
7. switch缺少break/default
```

#### log-security — 日志安全审查

**路径：** `.opencode/skills/log-security/SKILL.md`

```markdown
---
name: log-security
description: 日志安全审查
---

# 审查规则
1. 日志中打印身份证/手机号/卡号等敏感信息
2. 使用System.out.println代替Logger
3. 异常打印不完整e.printStackTrace()或e.getMessage()
4. 日志级别使用不当
5. 关键操作缺少业务日志
6. 循环体/高频路径打印日志可能造成日志风暴
```

---

### 4.2 安全 Skills（4个）

#### sensitive-data-scanner — 敏感数据扫描

```markdown
---
name: sensitive-data-scanner
description: 敏感数据扫描
---

# 审查规则
1. 日志中打印手机号/身份证/银行卡号/CVV
2. 响应报文中包含明文敏感字段
3. 密码/密钥/AK/SK硬编码在代码中
4. 敏感数据未脱敏直接存储或传输
5. 敏感字段未加密存储（数据库加密）
```

#### crypto-compliance — 加密合规审查

```markdown
---
name: crypto-compliance
description: 加密合规审查
---

# 审查规则
1. 使用已弃用的加密算法(DES/RC4/MD5用于安全场景)
2. 密钥硬编码或密钥管理不规范
3. 未使用HTTPS/TLS传输加密
4. 签名验签逻辑缺失或不完整
5. 国密(SM2/SM3/SM4)合规要求
```

#### auth-permission — 认证鉴权审查

```markdown
---
name: auth-permission
description: 认证鉴权审查
---

# 审查规则
1. 敏感接口缺少@PreAuthorize/权限注解
2. 水平越权风险：用户A可操作用户B的数据
3. Token/Session管理不安全
4. 接口未做登录校验
5. 内部接口暴露到外网无额外鉴权
```

#### sql-injection-detector — SQL注入检测

```markdown
---
name: sql-injection-detector
description: SQL注入检测
---

# 审查规则
1. 动态SQL拼接用户输入
2. MyBatis中使用${}而非#{}
3. OrderBy/表名动态拼接未做白名单校验
4. like查询未对特殊字符转义
5. 批量操作使用拼接方式执行
```

---

### 4.3 交易 Skills（5个）

#### idempotency-checker — 幂等性检查

```markdown
---
name: idempotency-checker
description: 幂等性检查
---

# 审查规则
1. 写接口（下单/转账/退款）是否有幂等保障
2. 幂等key选取是否合理（商户号+订单号+金额）
3. 幂等判断是否有数据库唯一索引兜底
4. 重复请求返回结果是否一致
5. MQ消费端是否处理重复消息
6. 幂等key是否在业务操作提交后才释放
```

#### fund-consistency — 资金一致性审查

```markdown
---
name: fund-consistency
description: 资金一致性审查
---

# 审查规则
1. 金额是否使用BigDecimal（禁止double/float）
2. BigDecimal构造使用new BigDecimal("0.1")而非new BigDecimal(0.1)
3. 除法指定精度和舍入模式
4. 资金操作是否在Spring事务内
5. 跨库/跨服务资金操作是否有分布式事务或TCC补偿
6. 每笔资金变动是否生成唯一流水号
7. 流水是否记录变更前后余额
8. 冻结/解冻是否占用和恢复可用额度
```

#### state-machine-validator — 状态机校验

```markdown
---
name: state-machine-validator
description: 状态机校验
---

# 审查规则
1. 状态是否通过枚举定义而非魔法值
2. 状态机是否集中管理而非散落各处
3. 是否存在非法跳转（已退款→再次退款）
4. 终止状态是否仍被更新
5. 状态更新是否带版本号/乐观锁
6. 更新SQL是否带状态条件：WHERE status='OLD'
7. 涉及资金的节点是否有对应的资金流水
```

#### risk-control — 限额风控审查

```markdown
---
name: risk-control
description: 限额风控审查
---

# 审查规则
1. 单笔交易金额是否校验上下限
2. 日累计/月累计交易金额是否有控制
3. 同IP/同设备短时间高频交易是否有风控拦截
4. 风控结果是否影响交易流程（阻断/审核/放行）
5. 风控规则是否可配置而非硬编码
```

#### accounting-compliance — 账务合规审查

```markdown
---
name: accounting-compliance
description: 账务合规审查
---

# 审查规则
1. 记账是否满足借贷平衡
2. 会计日期是否使用业务日期而非系统当前时间
3. 冲正/撤销是否有完整账务记录
4. 总分账务是否每日对账
5. 账务流水号是否可追溯
```

---

### 4.4 合规 Skills（4个）

#### aml-anti-money-laundering — 反洗钱审查

```markdown
---
name: aml-anti-money-laundering
description: 反洗钱审查
---

# 审查规则
1. 大额交易是否有上报机制
2. 可疑交易是否标记和上报
3. 交易监控是否覆盖所有渠道
4. 上报数据格式是否符合监管要求
5. 上报时效是否满足监管要求
```

#### data-retention — 数据留存审查

```markdown
---
name: data-retention
description: 数据留存审查
---

# 审查规则
1. 交易记录留存周期是否满足监管要求
2. 日志留存周期是否符合合规要求
3. 数据删除是否有软删除机制
4. 归档策略是否合理
```

#### customer-due-diligence — 客户尽职调查审查

```markdown
---
name: customer-due-diligence
description: 客户尽职调查审查
---

# 审查规则
1. 开户前是否完成身份核验
2. 高风险客户是否有增强尽调
3. 交易前客户状态是否有效
4. 客户信息变更是否重新尽调
```

#### regulatory-reporting — 监管报送审查

```markdown
---
name: regulatory-reporting
description: 监管报送审查
---

# 审查规则
1. 报送数据是否完整准确
2. 报送时机是否满足合规要求
3. 报送失败是否有重试和告警
4. 报送数据是否有审核流程
```

---

### 4.5 数据完整性 Skills（4个）

#### transaction-boundary — 事务边界审查

```markdown
---
name: transaction-boundary
description: 事务边界审查
---

# 审查规则
1. 跨表操作事务注解(@Transactional)是否合理
2. 事务传播行为(REQUIRED/REQUIRES_NEW/NESTED)是否正确
3. 跨库操作是否使用分布式事务或柔性事务
4. 事务内是否存在远程调用（长事务风险）
5. @Transactional 是否只在 public 方法上
6. 事务方法内部 try-catch 是否导致异常被吞、事务未回滚
```

#### compensation-mechanism — 补偿机制审查

```markdown
---
name: compensation-mechanism
description: 补偿机制审查
---

# 审查规则
1. 跨系统调用失败是否有补偿机制
2. 补偿方式选择是否合理（TCC/Saga/本地消息表/最大努力通知）
3. 补偿是否幂等
4. 补偿失败是否有告警和人工介入
5. 补偿记录是否完整可追溯
```

#### audit-log-checker — 审计日志检查

```markdown
---
name: audit-log-checker
description: 审计日志检查
---

# 审查规则
1. 关键操作（转账/下单/改密/授权）是否有审计日志
2. 审计日志是否包含：操作人、时间、IP、操作内容、变更前后
3. 审计日志是否不可篡改（append-only）
4. 查询操作是否记录（敏感数据查询）
5. 审计日志存储是否满足合规要求
```

#### reconciliation-checker — 对账检查

```markdown
---
name: reconciliation-checker
description: 对账检查
---

# 审查规则
1. 内部账务是否每日对账
2. 外部渠道（银行/支付网关）是否定期对账
3. 对账文件解析是否健壮（字段缺失/乱码/编码）
4. 不一致记录是否有处理策略（自动调账/人工介入）
5. 对账截止时间配置是否正确
6. 对账失败是否有告警
```

---

## 5. Commands 定义

### 5.1 cr-full — 全量评审

**路径：** `.opencode/commands/cr-full.md`

```markdown
---
description: 全量代码评审：编排质量/安全/交易/合规/数据五个阶段审查
agent: sisyphus
model: deepseek/deepseek-chat
---

执行全量代码评审，按 review-master 的 Pipeline 分五个阶段审查，输出 P0/P1/P2 分级报告。
```

> 如果使用项目级编排 agent，可将 `agent` 改为 `review-master`

### 5.2 cr-transaction — 交易业务评审

**路径：** `.opencode/commands/cr-transaction.md`

```markdown
---
description: 交易业务评审：仅执行交易业务审查
agent: sisyphus
model: deepseek/deepseek-chat
---

仅执行阶段3（交易业务审查），重点检查幂等性、资金一致性、状态机、限额风控、账务合规。
适用于提交涉及交易核心逻辑的变更时使用。
```

### 5.3 cr-security — 安全评审

**路径：** `.opencode/commands/cr-security.md`

```markdown
---
description: 安全评审：仅执行安全审查
agent: sisyphus
model: deepseek/deepseek-chat
---

仅执行阶段2（安全审查），检查敏感数据泄露、加密合规、认证鉴权、SQL注入。
适用于提交涉及安全敏感逻辑的变更时使用。
```

### 5.4 cr-quality — 质量评审

**路径：** `.opencode/commands/cr-quality.md`

```markdown
---
description: 通用代码质量评审
agent: quality-review
model: deepseek/deepseek-chat
---

对当前变更执行通用代码质量审查，检查空指针、资源泄漏、线程安全、异常处理、性能、编码规范、日志安全。
```

### 5.5 cr-quick — 快速评审

**路径：** `.opencode/commands/cr-quick.md`

```markdown
---
description: 快速评审：仅执行通用质量和安全检查
agent: sisyphus
model: deepseek/deepseek-chat
---

仅执行阶段1（通用质量）和阶段2（安全审查），输出 P0/P1 级别问题。
适用于日常快速自检或非核心模块变更。
```

---

## 6. 文件目录结构

```
.opencode/
├── agent/
│   ├── review-master/                  # 编排总控 agent
│   │   └── SKILL.md
│   ├── quality-review/                 # 通用代码质量
│   │   └── SKILL.md
│   ├── security-review/                # 安全审查
│   │   └── SKILL.md
│   ├── transaction-review/             # 交易业务审查
│   │   └── SKILL.md
│   ├── compliance-review/              # 合规审查
│   │   └── SKILL.md
│   └── data-integrity-review/          # 数据完整性审查
│       └── SKILL.md
├── skills/
│   │   ── quality-review 挂载 ──
│   ├── npe-defender/                   │
│   ├── resource-leak-detector/         │
│   ├── thread-safety-checker/          │
│   ├── exception-handling/             │
│   ├── performance-smell/              │
│   ├── code-style/                     │
│   └── log-security/                   │
│                                       │
│   │   ── security-review 挂载 ──      │
│   ├── sensitive-data-scanner/         │
│   ├── crypto-compliance/              │
│   ├── auth-permission/                │
│   └── sql-injection-detector/         │
│                                       │
│   │   ── transaction-review 挂载 ──   │
│   ├── idempotency-checker/            │
│   ├── fund-consistency/               │
│   ├── state-machine-validator/        │
│   ├── risk-control/                   │
│   └── accounting-compliance/          │
│                                       │
│   │   ── compliance-review 挂载 ──    │
│   ├── aml-anti-money-laundering/      │
│   ├── data-retention/                 │
│   ├── customer-due-diligence/         │
│   └── regulatory-reporting/           │
│                                       │
│   │   ── data-integrity-review 挂载 ──│
│   ├── transaction-boundary/           │
│   ├── compensation-mechanism/         │
│   ├── audit-log-checker/              │
│   └── reconciliation-checker/         │
│
└── commands/
    ├── cr-full.md                      # 全量评审
    ├── cr-quality.md                   # 仅通用代码质量
    ├── cr-security.md                  # 仅安全审查
    ├── cr-transaction.md               # 仅交易业务审查
    └── cr-quick.md                     # 快速评审
```

**共计：** 6 个 Agent + 24 个 Skill + 5 个 Command

---

## 7. 评审报告分级标准

### 7.1 问题等级

| 等级 | 标签 | 说明 | 处理要求 |
|------|------|------|----------|
| **P0** | 阻断 | 资金安全、数据一致性、严重安全漏洞 | **必须修复**后方可合码 |
| **P1** | 警告 | 线程安全、资源泄漏、业务逻辑缺陷 | **建议修复** |
| **P2** | 建议 | 编码规范、性能优化、日志规范 | **选择性修复** |

### 7.2 P0 阻断场景

| 场景 | 示例 |
|------|------|
| 资金安全 | 金额使用double/float、资金操作无事务、扣款和记账不一致 |
| 数据一致性 | 分布式事务缺失、补偿机制缺失、关键操作无幂等 |
| 严重漏洞 | SQL注入、敏感数据明文泄露、硬编码密钥、越权漏洞 |
| 合规违规 | 反洗钱上报缺失、数据留存不满足监管要求 |

---

## 8. 实施路线图

### 阶段一：基础设施搭建

```
1. 确认全局 sisyphus agent 可用
2. 创建 review-master agent（编排总控）
3. 创建 5 个领域 agent 目录骨架
4. 创建 cr-full / cr-quick command
5. 验证 sisyphus 能否正确编排多阶段审查
```

### 阶段二：通用质量上线

```
1. 创建 7 个质量 skill
2. 完善 quality-review agent
3. 创建 cr-quality command
4. 选定一个项目灰度验证
5. 收集反馈，调整规则
```

### 阶段三：安全审查上线

```
1. 创建 4 个安全 skill
2. 完善 security-review agent
3. 创建 cr-security command
4. 安全团队参与规则评审
```

### 阶段四：交易业务评审上线

```
1. 创建 5 个交易 skill
2. 完善 transaction-review agent
3. 业务架构师填充业务规则
4. 针对核心交易模块验证
```

### 阶段五：合规 + 数据完整

```
1. 创建 4 个合规 skill + 4 个数据 skill
2. 合规/法务团队参与规则评审
3. 全量 cr-full 正式上线
```

---

## 9. 扩展指南

### 9.1 新增一个审查规则

只需在对应领域下新增一个 skill 目录 + SKILL.md 文件：

```
skills/transaction-review/new-rule/
└── SKILL.md
```

Agent 会自动加载（根据 body prompt 中引用的 skill 列表）。

### 9.2 新增一个领域 Agent

```
agent/new-domain-review/
└── SKILL.md          # 定义角色 + 引用 skills

skills/new-domain/
├── rule1/
│   └── SKILL.md
└── rule2/
    └── SKILL.md
```

### 9.3 新增一个 Command

```
commands/cr-new-scenario.md
```

定义 `agent` 和 `model` 即可，body 写执行指令。

### 9.4 业务规则定制

交易和领域 skill 的规则需要根据实际业务填充。建议：

1. **业务架构师** 提供核心业务规则（退款条件、限额标准、状态机定义）
2. **合规团队** 提供合规审查清单
3. **安全团队** 提供安全审查标准
4. **架构组** 统一维护和评审 skill 定义

---

> **文档版本：** v1.0
> **适用范围：** 金融行业 Spring Cloud / Java 技术栈
> **基础平台：** opencode + sisyphus + deepseek-chat
