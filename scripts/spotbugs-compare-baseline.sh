#!/usr/bin/env bash
set -euo pipefail

ROOT=$(git rev-parse --show-toplevel 2>/dev/null || echo "$(pwd)")
cd "$ROOT"

REPORT_FILE=target/spotbugsXml.xml
BASELINE_FILE=config/spotbugs/baseline-spotbugs.xml

echo "Running SpotBugs to generate report..."
# SpotBugs analyses compiled classes; compile first so we never report on stale
# bytecode when this script is run directly.
mvn -q -DskipTests compile
# Version comes from the plugin declaration in pom.xml - single source of truth.
if ! mvn -q -DskipTests spotbugs:spotbugs; then
  echo "SpotBugs execution failed. Aborting: a check that cannot run must not report success." >&2
  exit 1
fi

if [[ ! -f "$REPORT_FILE" ]]; then
  echo "SpotBugs produced no report at $REPORT_FILE. Aborting." >&2
  exit 1
fi

if [[ ! -f "$BASELINE_FILE" ]]; then
  echo "SpotBugs baseline not found; creating baseline at $BASELINE_FILE"
  mkdir -p config/spotbugs
  cp "$REPORT_FILE" "$BASELINE_FILE"
  echo "SpotBugs baseline created. CI will enforce no new violations in future runs."
  exit 0
fi

echo "Comparing SpotBugs report to baseline..."

TMP_CURR=$(mktemp)
TMP_BASE=$(mktemp)

# Identity of a bug: type, category, declaring class, and SpotBugs' own
# instanceHash (plus occurrence number to separate repeats of the same bug in
# one method). Deliberately excludes line numbers and file paths so that
# reformatting does not invalidate the whole baseline - the mistake that broke
# the Checkstyle baseline after the Spotless reformat.
#
# Note: `Class` is a CHILD of BugInstance, not its parent. The previous
# expression used parent::Class and a BugInstance/ prefix from an already-
# BugInstance context, so three of six fields were always empty and 30 distinct
# bugs collapsed into 8 keys.
KEY="concat(@type, ':', @category, ':', Class/@classname, ':', @instanceHash, ':', @instanceOccurrenceNum)"
xmlstarlet sel -t -m "//BugInstance" -v "$KEY" -n "$REPORT_FILE" | sort -u > $TMP_CURR || true
xmlstarlet sel -t -m "//BugInstance" -v "$KEY" -n "$BASELINE_FILE" | sort -u > $TMP_BASE || true

NEW=$(comm -23 $TMP_CURR $TMP_BASE | wc -l)
if [[ $NEW -gt 0 ]]; then
  echo "Found $NEW new SpotBugs issue(s) not in baseline. Please fix or update baseline if intentional."
  comm -23 $TMP_CURR $TMP_BASE
  rm -f $TMP_CURR $TMP_BASE
  exit 1
fi

echo "No new SpotBugs issues found."
rm -f $TMP_CURR $TMP_BASE
exit 0
