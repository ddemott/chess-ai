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
set +e
mvn -q -DskipTests com.github.spotbugs:spotbugs-maven-plugin:4.7.3:spotbugs
SPOTBUGS_EXIT=$?
set -e
if [[ $SPOTBUGS_EXIT -eq 0 && -f target/spotbugsXml.xml ]]; then
  cp target/spotbugsXml.xml config/spotbugs/baseline-spotbugs.xml || true
else
  echo "SpotBugs baseline not generated - plugin may not be available in local environment. Skipping SpotBugs baseline generation."
fi

echo "Baselines created in config/checkstyle/ and config/spotbugs/"
