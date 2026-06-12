---
name: open-code-review
description: 阿里巴巴开源的 AI 代码审查工具，通过 ocr CLI 对 Git 变更进行行级精准审查
---

# Open Code Review (OCR)

基于阿里巴巴 open-code-review 工具的 AI 代码审查能力。它会读取 Git diff，通过 LLM Agent 生成行级精度的结构化审查意见。

## 使用方式

### 审查当前分支与主分支的差异

```bash
cd <项目目录>
ocr review --from main --to <当前分支名>
```

### 审查工作区变更（未暂存/未提交）

```bash
ocr review
```

### 审查指定提交

```bash
ocr review --commit <commit-hash>
```

## 输出说明

- 每条审查意见会标注文件名、行号范围
- 优先级标记：【高】/【中】/【低】
- 包含具体代码片段和修复建议

## 配置

配置存储在 `~/.opencodereview/config.json`，使用 MiniMax-M2.5 模型。
