#!/usr/bin/env bash
set -e

DIFF_FILE=$1
TMP_DIR=$(mktemp -d)

# 1. 从 diff 中提取被修改的 Java 文件路径
FILES=$(grep '^+++ b/' "$DIFF_FILE" | sed 's|+++ b/||' | grep '\.java$')

echo "["

FIRST=true

for FILE in $FILES; do
  if [ ! -f "$FILE" ]; then
    continue
  fi

  if [ "$FIRST" = false ]; then
    echo ","
  fi
  FIRST=false

  echo "{"
  echo "\"file\": \"$FILE\","
  echo "\"methods\":"

  tree-sitter query queries/methods.scm "$FILE" \
    | jq -R -s 'split("\n") | map(select(length>0))'

  echo "}"
done

echo "]"

rm -rf "$TMP_DIR"
