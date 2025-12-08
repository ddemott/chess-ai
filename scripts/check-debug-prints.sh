#!/usr/bin/env bash
set -euo pipefail

echo "Scanning repository for debug prints (System.out / System.err / printStackTrace) (excluding console UI package)..."

ROOT=$(git rev-parse --show-toplevel 2>/dev/null || echo "$(pwd)")
cd "$ROOT"

# Gather all tracked Java files under src/main/java
FILES=$(git ls-files 'src/main/java/**/*.java' || true)
if [[ -z "$FILES" ]]; then
  echo "No tracked Java files detected under src/main/java"
  exit 0
fi

FOUND=()
for f in $FILES; do
  # Exclude the console package (console UI logs are allowed)
  if [[ "$f" == src/main/java/com/ddemott/chessai/console/* ]]; then
    continue
  fi
  # Exclude debug runner files in the repo root (Debug* or debug_* scripts)
  basename=$(basename "$f")
  if [[ "$basename" == Debug* || "$basename" == debug_* ]]; then
    continue
  fi
  # Use a Perl-compatible regex to avoid matching commented lines that start with //
  # For each grep match, ensure the println isn't commented out (by // or by earlier block comment marker)
  while IFS= read -r match_line; do
    if [[ -z "$match_line" ]]; then
      continue
    fi
    # match_line format: file:line:content
    file_path="$(echo "$match_line" | cut -d: -f1)"
    line_num="$(echo "$match_line" | cut -d: -f2)"
    content="$(echo "$match_line" | cut -d: -f3-)"
    # Determine which pattern matched and calculate prefix accordingly
    if [[ "$content" =~ System\.out\.println ]]; then
      pattern="System.out.println"
    elif [[ "$content" =~ System\.err\.println ]]; then
      pattern="System.err.println"
    else
      pattern=".printStackTrace("
    fi
    prefix="${content%%$pattern*}"
    # If the prefix contains '//' then the println occurs after an inline comment marker and can be ignored
    if [[ "$prefix" =~ // ]]; then
      continue
    fi
    # If prefix contains '/*' or '*' assume it's likely in a block comment - skip
    if [[ "$prefix" =~ /\* ]] || [[ "$prefix" =~ \* ]]; then
      continue
    fi
    FOUND+=("$f")
    echo "$file_path:$line_num:$content"
  done < <(grep -nH -E "System\.out\.println|System\.err\.println|\.printStackTrace\(" "$f" 2>/dev/null || true)
done

if [[ ${#FOUND[@]} -gt 0 ]]; then
  echo "Found debug prints or stack traces in the following files (outside console package):"
  for file in "${FOUND[@]}"; do
    grep -nH -E "System\.out\.println|System\.err\.println|\.printStackTrace\(" "$file" || true
  done
  echo "\nPlease remove debug prints or use a logger wrapper in production code (or move to console/demo files)."
  exit 1
fi

echo "No problematic debug prints found."
exit 0
