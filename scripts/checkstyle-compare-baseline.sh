#!/usr/bin/env bash
set -euo pipefail

ROOT=$(git rev-parse --show-toplevel 2>/dev/null || echo "$(pwd)")
cd "$ROOT"

REPORT_FILE=target/checkstyle-result.xml
BASELINE_FILE=config/checkstyle/baseline-checkstyle.xml

echo "Running Checkstyle to generate report..."
mvn -q -DskipTests org.apache.maven.plugins:maven-checkstyle-plugin:3.3.1:checkstyle -Dcheckstyle.output.format=xml -Dcheckstyle.output.file=$REPORT_FILE

if [[ ! -f "$BASELINE_FILE" ]]; then
  echo "Baseline not found; creating baseline at $BASELINE_FILE"
  mkdir -p config/checkstyle
  cp "$REPORT_FILE" "$BASELINE_FILE"
  echo "Baseline created. CI will now enforce no new violations in future runs."
  exit 0
fi

echo "Comparing Checkstyle report to baseline..."

# Extract errors in a normalized form for comparison: file:line:source:message
TMP_CURR=$(mktemp)
TMP_BASE=$(mktemp)
xmlstarlet sel -t -m "//error" -v "concat(../../@name, ':', @line, ':', @source, ':', @message)" -n "$REPORT_FILE" | sort -u > $TMP_CURR || true
xmlstarlet sel -t -m "//error" -v "concat(../../@name, ':', @line, ':', @source, ':', @message)" -n "$BASELINE_FILE" | sort -u > $TMP_BASE || true

NEW=$(comm -23 $TMP_CURR $TMP_BASE | wc -l)
if [[ $NEW -gt 0 ]]; then
  echo "Found $NEW new Checkstyle violation(s) not in baseline. Please fix or update baseline if intentional."
  comm -23 $TMP_CURR $TMP_BASE
  rm -f $TMP_CURR $TMP_BASE
  exit 1
fi

echo "No new Checkstyle violations found."
rm -f $TMP_CURR $TMP_BASE
exit 0
