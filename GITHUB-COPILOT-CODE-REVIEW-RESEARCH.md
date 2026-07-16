# GitHub Copilot Code Review 调研与操作手册

> 调研时间：2026 年 7 月  
> 范围：架构原理、操作流程、自定义配置、领域评审、竞品对比

---

## 目录

- [一、行业背景](#一行业背景)
- [二、GitHub Copilot Code Review 架构原理](#二github-copilot-code-review-架构原理)
- [三、操作流程](#三操作流程)
- [四、评审深度配置](#四评审深度配置)
- [五、自定义评审规则](#五自定义评审规则)
- [六、Agent Skills 支持情况](#六agent-skills-支持情况)
- [七、完整工作流示例](#七完整工作流示例)
- [八、通用评审 vs 领域业务评审](#八通用评审-vs-领域业务评审)
- [九、行业竞品对比](#九行业竞品对比)
- [十、对你当前方案的对比与建议](#十对你当前方案的对比与建议)
- [十一、官方参考链接](#十一官方参考链接)

---

## 一、行业背景

### AI 代码评审的四个分层

最有效的行业实践采用**分层防御**方式：

```
Layer 1: IDE 级评审（写代码时实时检查）
  工具: Copilot, Cursor, Sourcery
  捕获: 语法、明显逻辑错误、安全反模式

Layer 2: PR 级自动评审（每次提交）
  工具: CodeRabbit, Greptile, Copilot Review, Qodo
  捕获: Bug 模式、安全漏洞、测试覆盖、复杂度

Layer 3: 架构级跨仓库分析（定期）
  工具: Greptile, SonarQube, CodeScene
  捕获: 架构漂移、跨仓库依赖断裂、技术债

Layer 4: 人工评审（高判断力场景）
  重点: 业务逻辑违规、架构决策、产品意图对齐
```

### 核心数据

- 2026 年初，GitHub 上 **41% 的代码是 AI 辅助生成**的
- AI 生成的 PR 比人类代码多 **1.7 倍的问题**
- AI 安全漏洞检出率约 **71%** — 意味着 **29% 漏报**
- AI 业务逻辑违规检测准确率仅 **~18%**（结构性边界，非模型问题）
- Copilot Code Review 累计完成 **6000 万+ 次评审**，**71% 的评审产生 actionable 反馈**
- DoorDash 生产级 AI 评审：高严重级别发现**接受率 60.2%**

---

## 二、GitHub Copilot Code Review 架构原理

### 2.1 架构演进

**2026 年 3 月**，Copilot Code Review 从静态 pipeline **全面升级为 agentic 架构**：

```
PR 提交
  │
  ▼
┌─────────────────────────────────────┐
│ Agent 规划评审策略                    │
│   - 分析 diff 和关联 Issue/PR       │
│   - 确定需要探索的代码区域            │
└─────────────────────────────────────┘
  │
  ▼
┌─────────────────────────────────────┐
│ 工具调用探索代码库                    │
│   - grep/rg/glob/view               │
│   - 读取相关文件和目录结构            │
│   - 跨文件引用追踪                   │
└─────────────────────────────────────┘
  │
  ▼
┌─────────────────────────────────────┐
│ 生成多维度评审意见                    │
│   - 行级评论                         │
│   - 代码建议（可一键 Apply）          │
│   - 批量 autofix                    │
└─────────────────────────────────────┘
```

### 2.2 关键组件

| 组件 | 说明 |
|------|------|
| **Agentic tool-calling** | 主动探索代码库，而非被动接收 diff |
| **Cross-Agent Memory** | 评审 agent 与 coding agent 共享仓库级记忆 |
| **Low/Medium 分析档位** | 根据复杂度选择不同推理模型 |
| **Agent Skills & MCP** | 自定义技能 + 外部系统连接 |
| **GitHub Actions 基础设施** | 运行在 Actions runner 之上 |

### 2.3 评审过程四阶段

```
1. 输入处理
   diff + PR 标题/描述 + 关联 Issue + custom instructions
       →
2. LLM 分析
   经过专门调优的混合模型
       →
3. 生成回复
   自然语言评论 + 代码建议（proposed changes）
       →
4. 输出格式化
   行级评论 + 批量分组 + autofix
```

### 2.4 Cross-Agent Memory 系统

2026 年引入的跨 agent 记忆系统：

- **记忆创建**：agent 通过工具调用存储事实，附带代码位置引用
- **即时验证**：使用记忆前自动验证引用的代码仍然有效
- **仓库范围**：记忆限于仓库内，由有写权限的贡献者创建
- **自愈能力**：代码与记忆矛盾时，agent 存储修正版本

**效果数据：**
- Copilot Code Review：好评率 **+2%**（77% vs 75%）
- Copilot Coding Agent：PR 合并率 **+7%**（90% vs 83%）

---

## 三、操作流程

### 前置条件

| 条件 | 说明 |
|------|------|
| 账号 | GitHub Copilot Pro / Pro+ / Business / Enterprise |
| 权限 | 仓库或组织管理员（配置自动评审时） |
| 费用 | AI Credits（1 credit = $0.01），私有仓库额外 Actions 分钟 |

### 方式一：手动请求评审

```
仓库 → Pull Request → 右侧 Reviewer 栏 → 选择 "Copilot"
→ 等待约 30 秒，Copilot 即发表评审意见
```

### 方式二：自动评审（推荐）

#### 个人级别

```
个人头像 → Settings → Copilot settings →
  Automatic Copilot code review → Enabled
```
效果：**你自己创建**的所有 PR 自动触发。

#### 仓库级别（Branch Ruleset——最常用）

```
仓库 Settings → Rules → Rulesets → New ruleset → New branch ruleset

配置示例：
┌──────────────────────────────────────────────┐
│ 名称:            copilot-auto-review           │
│ 执行状态:        Active                        │
│ 目标分支:        Include default branch        │
│                                                │
│ ☑ Automatically request Copilot code review    │
│ ☑ Review new pushes（每次推送重新评审）         │
│ ☐ Review draft pull requests（可选）            │
└──────────────────────────────────────────────┘

→ 点击 Create 完成
```

效果：该仓库**所有 PR** 自动触发。

#### 组织级别（批量覆盖）

```
组织 Settings → Repository → Rulesets → New ruleset → New branch ruleset

额外支持 Target repositories 设置包含/排除模式
如 *service 匹配所有 service 后缀仓库
```

### 方式三：IDE 内评审

```
VS Code:
  选中代码 → 右键 → Copilot → Review and Comment
  或 Source Control 视图中对未提交变更发起评审

JetBrains:
  选中代码 → 右键 → Copilot → Review and Comment
```

### 方式四：CLI 评审

```bash
# 评审本地未提交变更
gh copilot review

# 对比 main 分支评审
gh copilot review --branch main
```

---

## 四、评审深度配置

两档分析深度，按仓库配置：

| 档次 | 适用场景 | 特点 |
|------|---------|------|
| **Low**（默认） | 文档、简单变更、小型仓库 | 快速、便宜 |
| **Medium** | 复杂逻辑、安全敏感代码、跨服务变更 | 更高推理模型，更多 actionable 建议 |

配置路径：

```
仓库 Settings → Copilot → Code review → Review effort level
  → Low 或 Medium
```

*组织级别可设默认值，各仓库可覆盖*

---

## 五、自定义评审规则

### 5.1 Custom Instructions（最成熟的方式）

#### 全局指令

在仓库根目录创建 `.github/copilot-instructions.md`：

```markdown
# Copilot Code Review 指令

## 安全
- 禁止硬编码密钥、API Token、凭据
- 所有外部输入必须做参数校验和清洗
- SQL 查询必须使用参数化查询，禁止拼接

## 代码质量
- 函数遵循单一职责原则
- 嵌套层级不超过 4 层
- 禁止魔法数字，使用命名常量
- 所有公共函数必须有关联的单元测试

## 架构约定
- Controller 层只做参数校验和路由，不包含业务逻辑
- Service 层处理业务逻辑
- Repository/Dao 层处理数据访问
```

#### 路径级指令

按文件路径匹配，不同模块应用不同规则：

创建 `.github/instructions/payment.instructions.md`：

```yaml
---
applyTo: "**/payment/**"
---

## 支付模块专用规则
- 所有金额计算使用 decimal，禁止使用 float
- 外部支付 API 调用必须有超时（<=5s）和重试（最多 3 次）
- 退款必须记录操作日志
- 涉及金额变更必须有幂等性校验
```

创建 `.github/instructions/api.instructions.md`：

```yaml
---
applyTo: "**/*Controller*, **/*Resource*"
---

## API 层规则
- 所有接口必须有 OpenAPI/Swagger 注解
- 返回值必须使用统一的 Response 包装类
- 不允许在 Controller 中直接调用 DAO/Repository
- 分页查询必须设置最大 limit（默认 100，最大 1000）
```

创建 `.github/instructions/tests.instructions.md`：

```yaml
---
applyTo: "**/*.{spec,test}.{ts,js,java,py}"
---

## 测试规则
- 遵循 Arrange-Act-Assert（AAA）模式
- 每个测试一个逻辑断言
- 测试方法名使用 given_when_then 格式
```

创建 `.github/instructions/sql.instructions.md`：

```yaml
---
applyTo: "**/*.sql"
---

## SQL 规则
- 所有查询必须指定列名，禁止 SELECT *
- JOIN 查询必须使用表别名
- WHERE 条件必须包含索引列
```

**启用自定义指令：**

```
仓库 Settings → Copilot → Code review →
  "Use custom instructions when reviewing pull requests" → ON
```

### 5.2 自定义指令格式说明

| 文件 | 范围 | 说明 |
|------|------|------|
| `.github/copilot-instructions.md` | 全仓库 | 最长 4000 字符，每次评审读取 |
| `.github/instructions/<name>.instructions.md` | 路径级 | 通过 `applyTo` 匹配文件路径 |
| `AGENTS.md` | 就近匹配（递归） | 可放在仓库任意目录 |
| `CLAUDE.md` / `GEMINI.md` | 仓库根目录 | Copilot 也能读取 |

路径级指令 YAML frontmatter 支持：

```yaml
---
applyTo: "**/*.ts, **/*.tsx"   # 必须，glob 匹配
excludeAgent: "code-review"    # 可选，排除特定 agent
---
```

---

## 六、Agent Skills 支持情况

### 6.1 当前状态

**2026 年 6 月 2 日** GitHub 宣布 Agent Skills + MCP 进入 **Public Preview**。

### 6.2 定义方式

在仓库中创建 `.github/skills/code-review/SKILL.md`：

```markdown
---
name: payment-domain-review
description: 对支付模块的 PR 进行业务规则评审
allowed-tools:
  - builtin:grep
  - builtin:glob
---

# 支付业务规则

1. 退款金额 > 1000 元需要二级审批逻辑
2. 不允许直接操作聚合表（payment_ledger）
3. 所有支付回调必须验证签名
4. 对账文件生成必须使用批量模式
```

### 6.3 Skill 目录结构

```
.github/skills/
├── code-review/        # 命名影响 Copilot 是否加载
│   ├── SKILL.md
│   └── scripts/        # 可选辅助脚本
├── security-review/
│   └── SKILL.md
└── domain-payment/
    └── SKILL.md
```

支持的位置：

| 位置 | 范围 |
|------|------|
| `.github/skills/` | 项目级 |
| `.claude/skills/` | 项目级（开放标准兼容） |
| `.agents/skills/` | 项目级（开放标准兼容） |
| `~/.copilot/skills/` | 个人级 |
| `~/.agents/skills/` | 个人级 |

### 6.4 当前限制（2026.07）

- Skills 在**自动 PR 评审中尚未完全就绪** — 在 Copilot Chat 和 Agent Mode 中工作良好
- Copilot **自动判断**技能相关性，无法手动选择加载哪些
- 没有社区 Skill 市场，需自行创建或以 [community/awesome-copilot](https://github.com/) 为参考
- 组织级/企业级 Skills 即将推出

### 6.5 MCP Server 连接

可以连接外部系统获取评审上下文：

```
Jira          → 拉取关联需求/缺陷
Confluence    → 拉取架构文档
ServiceNow    → 拉取变更记录
PagerDuty     → 拉取近期故障事件
```

---

## 七、完整工作流示例

### 支付模块 PR 评审流水线

```
开发者提交 PR（修改 payment/ 下的代码）
     │
     ▼
┌────────────────────────────────────────────────────┐
│ 1. Branch Ruleset 触发自动评审                       │
│    Copilot 自动成为 Reviewer                         │
│    读取 .github/copilot-instructions.md 全局指令     │
│    识别路径匹配 **/payment/**                        │
│    加载 payment.instructions.md 支付专用规则          │
│    Review Effort: Medium（支付模块用深度评审）         │
└────────────────────────────────────────────────────┘
     │
     ▼
┌────────────────────────────────────────────────────┐
│ 2. Copilot Agent 规划评审策略                        │
│    分析 diff + 读取关联 Issue                        │
│    使用 grep/glob 探索关联的 Service/Repository 层    │
│    检查是否有涉及金额、幂等性、日志的关键变更          │
│    确定评审重点领域                                   │
└────────────────────────────────────────────────────┘
     │
     ▼
┌────────────────────────────────────────────────────┐
│ 3. 生成评审意见                                      │
│    行级评论                                          │
│    代码建议（可一键 Apply）                           │
│    按严重程度分组（Critical/Major/Minor）             │
│    例如：发现直接用 float 处理金额 → 建议改用 decimal │
└────────────────────────────────────────────────────┘
     │
     ▼
┌────────────────────────────────────────────────────┐
│ 4. 开发者处理                                        │
│    查看 Copilot 评审意见                              │
│    接受建议 → 一键 Apply（自动生成 commit）           │
│    拒绝建议 → Resolve 并备注原因                      │
│    推送新 commit → Ruleset 触发重新评审               │
└────────────────────────────────────────────────────┘
     │
     ▼
┌────────────────────────────────────────────────────┐
│ 5. 人工最终审查                                      │
│    重点关注 Copilot 无法判断的业务逻辑                │
│    架构决策、产品意图对齐                              │
│    合并 PR                                           │
└────────────────────────────────────────────────────┘
```

### 推荐的 Ruleset 配置

```yaml
# 支付模块 PR 的 Ruleset 配置
名称: payment-auto-review
执行状态: Active
目标分支: main, release/*

☑ Automatically request Copilot code review
☑ Review new pushes
☐ Review draft pull requests（节省费用，草稿完成后再评审）
Review Effort: Medium

# 完整门控建议
前置检查通过后才能合并:
├── Copilot Review → Comment（不阻塞，但必须已执行）
├── Code Scanning → 安全漏洞 0 blocking
├── Unit Tests → 全部通过
├── Integration Tests → 全部通过
└── Human Approval → 至少一位 maintainer Approve
```

---

## 八、通用评审 vs 领域业务评审

### 对比

| 维度 | 通用评审 | 领域业务评审 |
|------|---------|-------------|
| **覆盖范围** | 代码风格、安全漏洞、性能、错误处理 | 业务规则合规、架构约定、领域逻辑 |
| **LLM 能力** | 安全漏洞 71%，bug 模式 46-82% | 仅 ~18%（结构性限制） |
| **Copilot 支持** | 开箱即用 | 需 custom instructions + 路径级规则 |
| **规则来源** | 业界通用最佳实践 | 从历史 PR、故障单、业务文档挖掘 |

### 业界三种领域评审方案

#### 方案 A：声明式业务规则（DoorDash / hrev 模式）

将业务规则写成结构化规则，每个规则独立评估：

```yaml
rules:
  - id: no-direct-aggregate-insert
    description: "不能直接写入聚合表，必须先创建源单据"
    severity: blocker
    path: "src/services/payment/"

  - id: refund-must-have-approval
    description: "退款金额 > 1000 需要二级审批"
    severity: critical

  - id: external-call-must-have-timeout
    description: "所有外部 API 调用必须有超时和重试"
    severity: major
```

**DoorDash 实践**：从 AGENTS.md、历史 PR 评审、Slack 决策、故障单中挖掘规则。效果：高/严重级别发现**接受率 60.2%**，覆盖 56 个仓库。

#### 方案 B：对抗式评审（Adversarial Review）

```
开发 agent 和评审 agent 配对
  → 评审 agent 发现问题
  → 开发 agent 尝试"反驳"
  → 只有通过交叉验证的发现才被提交

利用了 LLM 独立 agent 比自校正更可靠的特性
```

#### 方案 C：分层审查

```
1. 结构层（便宜模型）
   文件在正确的层？引用是否越界？接口匹配契约？

2. 规则层（规则引擎 + LLM）
   业务规则是否被遵守？是否符合 team convention？

3. 逻辑层（前沿模型 + 领域上下文）
   实际逻辑是否正确？边界条件是否覆盖？
```

---

## 九、行业竞品对比

### 9.1 工具对比

| 工具 | 定位 | 价格 | 优势 | 适用场景 |
|------|------|------|------|---------|
| **CodeRabbit** | 通用 PR 评审 | 免费 / $24/dev/月 | 600万+仓库，覆盖面最广 | 通用评审 |
| **Greptile** | 全代码库上下文 | $30/seat/月，50次后$1/次 | 跨文件问题检出 82% | 复杂仓库 |
| **Cursor Bugbot** | IDE 内评审 | ~$1-1.5/次 | Cursor 生态 | IDE 集成 |
| **Copilot Review** | GitHub 原生 | Business $19/月 + AI Credits | 深度平台集成 | GitHub 生态 |
| **Qodo Merge** | 多 agent 评审 | $30/user/月 | 可自托管（PR-Agent 开源） | 灵活定制 |
| **Sourcery** | 专注 Python | $12/user/月 | 噪声低，自适应学习 | Python 团队 |
| **Codacy AI** | 评审 + SAST | $15/dev/月 | 集成 SAST/覆盖度 | 全栈质量平台 |

### 9.2 检出率对比（基于真实生产 bug 的独立基准）

| 工具 | Bug 检出率 |
|------|-----------|
| **Greptile** | **82%** |
| GitHub Copilot Bugbot | 58% |
| CodeRabbit | 46% |
| Graphite Agent | 6% |

### 9.3 成本与效率对比

| 方式 | 成本/PR | 时间 | 误报率 |
|------|---------|------|--------|
| 高级工程师人工 | $100-200 | 2-3 小时 | ~5% |
| 对抗式多 agent | $0.75-4.50 | 2-6 分钟 | ~7% |
| DoorDash 生产级 | ~$3.00/PR | ~7 分钟 | 接受率 60.2% |
| Copilot Review | AI Credits + Actions | ~30秒-2分钟 | ~29% 漏报 |

---

## 十、对你当前方案的对比与建议

### 对比

| 维度 | GitHub Copilot Review | 你的方案（OpenCode + Skills） |
|------|----------------------|------------------------------|
| **架构** | GitHub Actions + Agent + MCP | OpenCode + 自定义 agent skills |
| **Skill 定义** | `.github/skills/*/SKILL.md` | Markdown 指令文件 |
| **Skill 生态** | 无社区市场，仅官方 + 自建 | 有 npm 生态（power-pack, swarm 等） |
| **Skill 控制粒度** | 自动发现，无法精确控制 | 可精确编排 pipeline |
| **领域评审** | 通过 custom instructions 间接实现 | 可设计专门的 domain-review skill |
| **计费** | AI Credits + Actions 分钟 | 自控 LLM API 费用 |
| **灵活性** | GitHub 平台内，受限 | 完全可控，可自定义 |
| **Skills 成熟度** | Public Preview，自动评审未完全就绪 | 相对成熟 |

### 建议

1. **如果主战场在 GitHub 生态内**：Copilot Review 作为快速评审入口 + 你的 skills 做深度领域评审，形成互补

2. **如果追求灵活性和领域评审能力**：当前 OpenCode + Skills 路线更有优势，尤其是可以：
   - 按模块加载不同的业务规则集
   - 编排多阶段评审流水线（通用 → 领域 → 安全）
   - 自定义评审通过/驳回的门控逻辑
   - 构建对抗式评审机制

3. **值得借鉴的 Copilot Review 做法**：
   - **Agentic 规划式评审**：先规划再执行，而不是直接丢 diff 给 LLM
   - **Cross-Agent 记忆**：评审发现沉淀复用
   - **路径级指令**：不同模块加载不同规则

---

## 十一、官方参考链接

### GitHub Docs

| 内容 | 链接 |
|------|------|
| About GitHub Copilot code review（核心概念） | https://docs.github.com/en/copilot/concepts/agents/code-review |
| Using Copilot code review on GitHub（操作指南） | https://docs.github.com/en/copilot/how-tos/copilot-on-github/use-copilot-agents/copilot-code-review |
| Configuring automatic review（自动评审配置） | https://docs.github.com/en/copilot/how-tos/copilot-on-github/set-up-copilot/configure-automatic-review |
| Customization cheat sheet（自定义速查表） | https://docs.github.com/en/copilot/reference/customization-cheat-sheet |
| Build an optimized review process（优化评审教程） | https://docs.github.com/en/copilot/tutorials/optimize-code-reviews |
| About Agent Skills（概念介绍） | https://docs.github.com/en/copilot/concepts/agents/about-agent-skills |
| Adding Agent Skills guide（操作指南） | https://docs.github.com/en/copilot/how-tos/copilot-on-github/customize-copilot/customize-cloud-agent/add-skills |
| Responsible use of Copilot code review | https://docs.github.com/en/copilot/responsible-use/code-review |
| About Copilot Cloud Agent | https://docs.github.com/en/copilot/concepts/agents/cloud-agent/about-cloud-agent |
| Copilot customization overview | https://docs.github.com/en/copilot/how-tos/copilot-on-github/customize-copilot/customize-copilot-overview |
| 中文版：为 Copilot 添加代理技能 | https://docs.github.com/zh/copilot/how-tos/copilot-on-github/customize-copilot/customize-cloud-agent/add-skills |

### GitHub Blog Changelog

| 内容 | 链接 |
|------|------|
| Agentic Architecture 升级（2026-03-05） | https://github.blog/changelog/2026-03-05-copilot-code-review-now-runs-on-an-agentic-architecture/ |
| Skills + MCP 支持（2026-06-02） | https://github.blog/changelog/2026-06-02-shape-copilot-code-review-around-your-team/ |
| 分析深度与效率更新（2026-06-25） | https://github.blog/changelog/2026-06-25-copilot-code-review-analysis-depth-and-efficiency-updates/ |
| 配置与控制更新（2026-06-12） | https://github.blog/changelog/2026-06-12-copilot-code-review-new-configurations-and-controls/ |
| 6000万次 Code Review 回顾 | https://github.blog/ai-and-ml/github-copilot/60-million-copilot-code-reviews-and-counting/ |
| Cross-Agent Memory 系统 | https://github.blog/ai-and-ml/github-copilot/building-an-agentic-memory-system-for-github-copilot/ |
| 独立规则用于自动评审（2025-09-10） | https://github.blog/changelog/2025-09-10-copilot-code-review-independent-repository-rule-for-automatic-reviews/ |

### 其他参考

| 内容 | 链接 |
|------|------|
| 官方 Skills 学习课程 | https://github.com/skills/copilot-code-review |
| Copilot SDK - Skills 开发文档 | https://github.com/github/copilot-sdk/blob/main/docs/features/skills.md |
| 社区 Skill 市场 | https://github.com/ericchansen/copilot-marketplace |
| Addy Osmani Agent Skills 合集 | https://github.com/addyosmani/agent-skills |
| Microsoft Learn 教程 | https://learn.microsoft.com/en-us/training/modules/code-reviews-pull-requests-github-copilot/4-issues-early-automated-reviews-copilot |
| DoorDash AI Code Review Agent（ZenML） | https://www.zenml.io/llmops-database/building-a-production-ai-code-review-agent-with-high-engineer-acceptance |
| 对抗式 AI 评审 | https://github.com/gaurav-yadav/adversarial-ai-review |
| 13 大 AI 评审工具对比（Sourcegraph） | https://sourcegraph.com/blog/automated-code-review-tools |
| AI Coding Agent 对比矩阵 | https://github.com/PackmindHub/coding-agents-matrix |
| 学术论文：7156 个 AI 生成 PR 分析 | https://ar5iv.labs.arxiv.org/html/2602.08915 |
