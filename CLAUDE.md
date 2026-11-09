# CLAUDE.md - ChessAI

Java 21 / Maven chess game with a Minimax + alpha-beta AI, console UI, web UI planned. All chess rules are written from scratch (no chess library). Rules and commands live here; the rest lives in the docs listed at the end. Generic process (git, PRs, review) is in `~/.claude/CLAUDE.md`.

## Commands

```bash
# Build
mvn clean compile

# Run all tests (primary validation command)
mvn -q test

# Tests + formatting check + debug-print check
mvn verify

# Skip the debug-print check during verify
mvn -Dskip.debug.prints=true verify

# Run single test class
mvn -Dtest=GameEngineTest test

# Run single test method
mvn -Dtest=GameEngineTest#testInitialState test

# Fully-qualified single test method
mvn -Dtest=com.ddemott.chessai.CastlingTest#testKingsideCastling_success test

# Build fat JAR
mvn clean package

# Run the game
java -cp target/classes com.ddemott.chessai.console.ConsoleChessGame

# Format code
mvn spotless:apply

# Check formatting
mvn spotless:check

# Install git hooks
bash scripts/install-hooks
```

## Hard rules

- **No debug prints in core code.** `System.out.println`, `System.err.println` and `.printStackTrace()` are forbidden outside `console/` and `examples/`; use the SLF4J wrapper `util/Log.java`. The pre-commit hook, `mvn verify` and CI reject them.
- **Tests before committing.** `mvn -q test` must pass (surefire kills a forked process after 60 s). Add tests for every behavior change.
- **Formatting.** `mvn spotless:check` must pass (CI, pre-commit and pre-push all run it); fix with `mvn spotless:apply`.
- **Type safety.** `Side` enum (not Strings) for colors, `AIDifficulty` enum for levels, `GameConstants` for magic numbers.
- **Naming.** Packages lowercase `com.ddemott.chessai.*`, classes PascalCase, methods camelCase, constants UPPER_SNAKE_CASE in `GameConstants`.
- **Board orientation.** `Board` holds `IPiece[][]`; row 0 = rank 1 (White's back rank), row 7 = rank 8 (`Coordinate`: `row = rank - 1`).
- **Commit messages.** Conventional Commits: `feat:`, `fix:`, `refactor:`, `docs:`, `test:`, `style:`, `chore:`; e.g. `fix(Board): set piece.position in setPieceAt() to keep piece board sync`.
- **Test conventions.** Tests mirror `src/main` under `src/test/java/com/ddemott/chessai/`; `<Feature>Test.java`, integration tests `<Feature>IntegrationTest.java`, method names like `testKingsideCastling_success()`; build positions programmatically with `Board` / `State`.

## Where things are

- Architecture, layers, AI, state/history: `docs/ARCHITECTURE.md`. Package/file tree: `README.md` "Project Structure" and `src/`.
- Branching, commit checklist, hooks (`.githooks/`, wrappers in `scripts/`), static-analysis baselines (`scripts/generate-baselines.sh`): `docs/COMMIT_PROCEDURE.md`. PR flow: `CONTRIBUTING.md`, `.github/PULL_REQUEST_TEMPLATE.md`.
- CI steps: `.github/workflows/ci.yml`.
- Backlog `TODO.md`; finished items `RESOLVED.md`; roadmap `docs/PLAN.md`; history `CHANGELOG.md`; move-history test plan `docs/MOVE_HISTORY_TESTS.md`; demos `examples/README.md`; user docs `README.md`.
