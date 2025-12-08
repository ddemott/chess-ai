#!/usr/bin/env bash
set -euo pipefail

echo "Scanning repository for System.out.println debug prints (excluding console UI package)..."

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
  if grep -nH "System\.out\.println" "$f" >/dev/null 2>&1; then
    FOUND+=("$f")
  fi
done

if [[ ${#FOUND[@]} -gt 0 ]]; then
  echo "Found System.out.println debug prints in the following files (outside console package):"
  for file in "${FOUND[@]}"; do
    grep -nH "System\.out\.println" "$file" || true
  done
  echo "\nPlease remove debug prints or use a logger wrapper in production code."
  exit 1
fi

echo "No problematic debug prints found."
exit 0
