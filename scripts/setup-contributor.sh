#!/usr/bin/env bash
set -euo pipefail

echo "Installing git hooks and compiling project for the contributor..."
bash scripts/install-hooks

echo "Compiling project (skip tests)..."
mvn -q -DskipTests compile

echo "Generating baselines for Checkstyle and SpotBugs (if possible)."
bash scripts/generate-baselines.sh || true

echo "Setup completed."
