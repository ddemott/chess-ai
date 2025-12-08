# Contributing to ChessAI

Thank you for contributing! This page explains how to set up your development environment, how to create a good change, and best practices for contributing to this repository.

## Getting Started
- Fork the repository and create a feature branch from `main`.
- Branch naming convention: `feature/<ticket>-short-desc` or `fix/<ticket>-short-desc`.

## Development workflow
1. Pull the latest `main`:
```bash
git checkout main
git pull origin main
```
2. Create your feature branch:
```bash
git checkout -b feature/<ticket>-short-desc
```
3. Make changes and run tests frequently.

## Running Tests
- Run the complete test suite:
```bash
mvn -q test
```
- Run a single class:
```bash
mvn -Dtest=com.ddemott.chessai.SpecialMovesTest test
```
- Run a single test method:
```bash
mvn -Dtest=com.ddemott.chessai.SpecialMovesTest#testPawnPromotion test
```

## Commit messages & Style
- Use Conventional Commit prefixes: `feat:`, `fix:`, `refactor:`, `docs:`, `test:`.
- Example:
```bash
git add src/main/java/com/ddemott/chessai/Board.java
git commit -m "fix(Board): set piece.position in setPieceAt() to keep piece board sync"
```

## Pull Request Guidelines
- Open a PR from your feature branch into `main`.
- Use the PR template to describe changes and testing done.
- Ensure CI (tests) pass on the PR.

## Pre-commit checks (recommended)
- Run tests and linters before committing locally.
- Optionally, you can install a git `pre-commit` hook to run quick checks (note: hooks are not pushed to the repo):
```bash
# Example: Run unit tests and fail commit on failure
cat > .git/hooks/pre-commit <<'HOOK'
#!/bin/sh
mvn -q -DskipTests=false test
RESULT=$?
if [ $RESULT -ne 0 ]; then
  echo "Tests failed, aborting commit."
  exit $RESULT
fi
HOOK
chmod +x .git/hooks/pre-commit
```

## Documentation & Changelog
- Update `CHANGELOG.md` with a brief summary under the relevant version or `Unreleased` section.
- If your change affects the architecture or public API, update `docs/ARCHITECTURE.md` and `README.md` as appropriate.

## Reporting Issues
- File a GitHub issue with steps to reproduce and any relevant environment details.

## Large Refactors
- For significant design changes (e.g., `BitBoard` migration), open a design PR first and discuss on the PR.
- Separate refactors and behavioral changes into different PRs where possible.

## Thank you
We appreciate your contributions. A thorough PR description and tests help maintainers review and merge changes quickly!
