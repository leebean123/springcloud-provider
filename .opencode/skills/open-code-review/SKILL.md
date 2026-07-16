---
name: open-code-review
description: 阿里巴巴开源的 AI 代码审查工具，通过 ocr CLI 对 Git 变更进行行级精准审查，并按要求格式化输出
---

# Open Code Review (OCR)

基于阿里巴巴 open-code-review 工具的 AI 代码审查能力。它会读取 Git diff，通过 LLM Agent 生成行级精度的结构化审查意见。

## 使用方式

### 审查指定分支间的差异

从用户消息中解析 `from=` 和 `to=` 参数，然后执行：

```bash
cd <项目目录>
ocr review --from <源分支> --to <目标分支> --format json --audience agent
```

**参数说明**：
- `from=xxx`：源分支（基准分支），用户未指定时默认 `main`
- `to=xxx`：目标分支（待审查分支），用户未指定时默认当前分支（通过 `git rev-parse --abbrev-ref HEAD` 获取）

例如用户输入 `from=develop to=my-feature` → `--from develop --to my-feature`

> **说明**：`--format json` 输出结构化结果便于解析，`--audience agent` 隐藏进度条只输出最终结果。

## 输出处理规则

执行 `ocr review` 后，对 JSON 输出进行以下处理并格式化展示给用户：

### 1. 解析 JSON 输出

输出结构如下：
```json
{
  "status": "success",
  "summary": { "files_reviewed": 1, "comments": 3 },
  "comments": [
    {
      "path": "src/main/java/.../XxxService.java",
      "content": "【空指针风险】参数 id 未做 null 检查。建议：在方法开头添加 null 检查",
      "existing_code": "public String getInfo(Long id) {",
      "start_line": 15,
      "end_line": 16
    }
  ]
}
```

### 2. 提取与转换规则

对 `comments` 数组中每条记录进行字段提取：

| 目标字段 | 提取方式 |
|----------|----------|
| **问题描述** | 取 `content` 中 `【】` 之后、`建议：` 之前的部分，trim |
| **级别** | 根据 `content` 中 `【】` 内的分类标签映射（见下方映射表） |
| **问题代码片段** | 取 `existing_code` 字段 |
| **修改建议** | 取 `content` 中 `建议：` 之后的部分，trim；若无 `建议：` 则整个 `content` 作为建议 |
| **建议修复代码** | 根据 `existing_code`（原问题代码）和问题描述，自行生成修复后的代码片段。只展示与问题相关的修复部分，无关代码用 `...` 省略，保持原代码风格 |
| **起始代码行数** | 取 `start_line` 字段 |
| **文件路径** | 取 `path` 字段 |

### 3. 级别映射表

从 `content` 的 `【分类标签】` 映射到 P0/P1/P2 三级：

| 分类标签 | 映射级别 | 说明 |
|----------|----------|------|
| 空指针风险、资源泄漏、线程安全、SQL注入、XSS、鉴权绕过、敏感数据泄露 | **P0（严重）** | 可能导致系统崩溃、安全漏洞或数据丢失 |
| 异常处理不当、事务问题、并发问题、状态机异常、幂等性、数据一致性、超时、重试 | **P1（重要）** | 可能导致业务异常或数据不一致 |
| 代码规范、日志打印、命名问题、常量定义、重复代码、性能建议、设计模式、注释问题 | **P2（建议）** | 代码质量与可维护性改进建议 |

> 若 `【】` 内的标签不在上表中，按严重程度合理推断：安全/正确性相关→P0，异常/数据相关→P1，风格/规范相关→P2。

### 3.1 输出过滤规则（重要）

**只输出 P0（严重）级别的问题**，P1 和 P2 级别的直接丢弃，不在报告中展示。
即使存在 P1/P2 问题，也一律忽略，仅保留 P0。

### 3.2 行号修正（重要）

当 `start_line == 0` 且 `end_line == 0` 时，说明 OCR 未能定位到准确行号。**禁止直接输出 0-0**，必须按以下步骤自行修正：

1. **定位文件**：根据 `path` 确定文件路径
2. **搜索代码片段**：用 `existing_code` 在文件内搜索定位，优先使用 `grep -n` 或 Read 工具查找
3. **验证**：确认行号上下文的代码与问题描述吻合
4. **修正**：将真实行号填入展示结果，替换 0-0

> 若 `existing_code` 过短（如仅 `}`）无法精确匹配，读取文件附近行辅助定位。**宁可展示近似行号范围，也不要输出 0-0。**

### 4. 单个问题输出模板

每个问题包含完整信息，**不要使用 `---` 分隔**，问题之间用空行隔开：

```
**问题 N** | **级别**: P0（严重） | **行号**: L15-L16
- **文件**: src/main/java/.../XxxService.java
- **问题描述**: 参数 id 未做 null 检查，当调用方传入 null 时会抛 NPE
- **问题代码**:
  ```java
  public String getInfo(Long id) {
  ```
- **修改建议**: 在方法开头添加 null 检查
- **建议修复代码**:
  ```java
  public String getInfo(Long id) {
      if (id == null) {
          return "未知用户";
      }
      ...
  }
  ```
```

其中：
- **级别**：展示 P0/P1/P2 及中文标签（P0-严重 / P1-重要 / P2-建议）
- **文件**：问题所在文件路径，取自 `path` 字段
- **行号**：格式 `L<start_line>-L<end_line>`，若起止相同则只显示一个
- **修改建议**：文字描述如何修复
- **建议修复代码**：基于 `existing_code` 和问题描述，生成具体的修复后代码片段，仅展示与问题相关的修复部分代码，保持原有代码风格

### 5. 整体展示结构

按 **文件分组** 展示，**只输出 P0 问题**：

```
## OCR 代码审查报告

共审查 N 个文件，发现 P0 问题 X 个（P1/P2 已过滤）

---

### 📁 文件: src/main/java/.../XxxService.java

**问题 1** | **级别**: P0（严重） | **行号**: L15-L16
- **文件**: src/main/java/.../XxxService.java
- **问题描述**: 参数 id 未做 null 检查
- **问题代码**:
  ```java
  public String getInfo(Long id) {
  ```
- **修改建议**: 在方法开头添加 null 检查
- **建议修复代码**:
  ```java
  public String getInfo(Long id) {
      if (id == null) {
          return "未知用户";
      }
  ```

---

### 📁 文件: src/main/java/.../YyyService.java

...
```

### 6. 无 P0 问题时的输出

当没有任何 P0 问题时，输出：

```
审查结果：未发现 P0 级别问题。
共审查 N 个文件，耗时 Xs。
```

> 注意：即使存在 P1/P2 问题，只要没有 P0 问题，就按上述输出。P1/P2 已被过滤不展示。

## 配置

配置存储在 `~/.opencodereview/config.json`，使用 MiniMax-M2.5 模型。
