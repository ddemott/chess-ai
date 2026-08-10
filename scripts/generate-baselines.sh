#!/usr/bin/env bash
set -euo pipefail

ROOT=$(git rev-parse --show-toplevel 2>/dev/null || echo "$(pwd)")
cd "$ROOT"

mkdir -p config/checkstyle
mkdir -p config/spotbugs

echo "Generating Checkstyle report (target/checkstyle-result.xml)..."
mvn -q -DskipTests org.apache.maven.plugins:maven-checkstyle-plugin:3.3.1:checkstyle -Dcheckstyle.output.format=xml -Dcheckstyle.output.file=target/checkstyle-result.xml
if [[ -f target/checkstyle-result.xml ]]; then
  cp target/checkstyle-result.xml config/checkstyle/baseline-checkstyle.xml || true
fi

echo "Generating SpotBugs report (target/spotbugsXml.xml)..."
# SpotBugs analyses compiled classes, so make sure they exist and are current.
mvn -q -DskipTests compile
# Version comes from the plugin declaration in pom.xml - single source of truth.
if ! mvn -q -DskipTests spotbugs:spotbugs; then
  echo "SpotBugs execution failed; cannot generate a baseline." >&2
  exit 1
fi
if [[ ! -f target/spotbugsXml.xml ]]; then
  echo "SpotBugs produced no report at target/spotbugsXml.xml; cannot generate a baseline." >&2
  exit 1
fi
cp target/spotbugsXml.xml config/spotbugs/baseline-spotbugs.xml

echo "Baselines created in config/checkstyle/ and config/spotbugs/"
