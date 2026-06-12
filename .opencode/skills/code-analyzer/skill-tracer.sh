#!/bin/bash
  # ============================================================
  # skill-tracer.sh — 通用 OpenCode Skill 依赖追踪器
  # 用法:  bash skill-tracer.sh <要执行的命令...>
  # 示例:  bash skill-tracer.sh ocr review --from main --to feature
  #        bash skill-tracer.sh python3 checker.py
  #        bash skill-tracer.sh node index.js
  # ============================================================
  set -e
  LOG="skill-deps-$(date +%Y%m%d%H%M%S).log"

  echo "===== Skill 依赖追踪 =====" | tee "$LOG"
  echo "命令: $*" | tee -a "$LOG"
  echo "时间: $(date)" | tee -a "$LOG"
  echo "目录: $(pwd)" | tee -a "$LOG"
  echo "" | tee -a "$LOG"

  # ── 1. 检查命令本体 ──
  CMD_PATH=$(which "${1:-}" 2>/dev/null || echo "$1")
  echo "命令路径: $CMD_PATH" | tee -a "$LOG"
  file "$CMD_PATH" 2>/dev/null | tee -a "$LOG"
  echo "" | tee -a "$LOG"

  # ── 2. 动态库依赖（macOS 用 otool，Linux 用 ldd）──
  echo "──── 动态库依赖 ────" | tee -a "$LOG"
  if [[ "$(uname)" == "Darwin" ]]; then
      otool -L "$CMD_PATH" 2>/dev/null | tee -a "$LOG"
  else
      ldd "$CMD_PATH" 2>/dev/null | tee -a "$LOG"
  fi
  echo "" | tee -a "$LOG"

  # ── 3. 配置文件依赖 ──
  echo "──── OCR 配置文件 ────" | tee -a "$LOG"
  for f in "$HOME/.opencodereview/config.json" "$HOME/.opencodereview/rule.json"; do
      [ -f "$f" ] && echo "  $f" | tee -a "$LOG"
  done
  echo "" | tee -a "$LOG"

  # ── 4. 运行时文件追踪（strace / dtruss）──
  echo "──── 运行时文件访问 ────" | tee -a "$LOG"
  if command -v strace &>/dev/null; then
      # Linux: 追踪 openat 系统调用
      strace -f -e trace=openat,stat -s 100 \
          "$@" 2>&1 \
          | grep -E '"[^"]+"' \
          | grep -v ENOENT \
          | sort -u \
          | tee -a "$LOG"
  elif command -v dtruss &>/dev/null; then
      # macOS: 需 sudo
      echo "⚠  dtruss 需要 sudo，跳过自动追踪" | tee -a "$LOG"
      echo "   如需手动: sudo dtruss -f $* 2>&1 | grep stat64" | tee -a "$LOG"
      # 直接执行业务命令
      "$@" 2>&1 | tee -a "$LOG"
  else
      echo "⚠  未检测到 strace/dtruss，直接执行业务命令" | tee -a "$LOG"
      "$@" 2>&1 | tee -a "$LOG"
  fi

  echo "" | tee -a "$LOG"
  echo "===== 追踪完成 =====" | tee -a "$LOG"
  echo "日志: $LOG"