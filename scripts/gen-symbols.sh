#!/usr/bin/env bash
set -e

FILE=$1

tree-sitter query queries/methods.scm "$FILE" \
| jq -R -s '
  split("\n")
  | map(select(length > 0))
  | map({
      kind: "method",
      raw: .
    })
'
