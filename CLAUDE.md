# CLAUDE.md - AI Assistant Guide for ChessAI

## Project Overview

Java chess game with AI opponent using Minimax with alpha-beta pruning. Console-based interface with a planned web UI. All chess rules are implemented from scratch (no chess library dependencies).

## Quick Reference

```bash
# Build
mvn clean compile

# Run all tests (primary validation command)
mvn -q test

# Run single test class
mvn -Dtest=GameEngineTest test

# Run single test method
mvn -Dtest=GameEngineTest#testInitialState test

# Build JAR
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

## Tech Stack

- **Language:** Java 21
- **Build:** Maven 3.9+
- **Testing:** JUnit 5 (Jupiter) 5.10.0
- **Formatting:** Spotless Maven Plugin (Eclipse formatter)
- **Static Analysis:** Checkstyle, SpotBugs (baseline-compared in CI)
- **CI:** GitHub Actions (`.github/workflows/ci.yml`)

## Project Structure

```
src/main/java/com/ddemott/chessai/
├── Board.java              # 8x8 board representation, move execution, special moves
├── State.java              # Game state, turn management, move history, pin detection
├── Move.java               # Move representation with algebraic notation
├── MoveHistory.java        # Move tracking, PGN export, undo/redo
├── Player.java             # Player model (side, captured pieces)
├── Evaluation.java         # Board evaluation for AI (material, position, safety)
├── GameConstants.java      # Centralized constants (piece values, board dimensions)
├── Side.java               # Enum: WHITE, BLACK
├── engine/
│   └── GameEngine.java     # Facade: orchestrates game flow, coordinates AI
├── ai/
│   ├── AIStrategy.java     # Strategy interface for pluggable AI algorithms
│   ├── MinMaxStrategy.java # Minimax with alpha-beta pruning
│   ├── AIDifficulty.java   # Enum: BEGINNER, INTERMEDIATE, ADVANCED, EXPERT
│   ├── ChessAI.java        # AI coordination
│   └── MoveResult.java     # Record: score + best move
├── pieces/
│   ├── IPiece.java         # Piece interface
│   ├── Piece.java          # Abstract base class
│   ├── Pawn.java, Rook.java, Knight.java, Bishop.java, Queen.java, King.java
├── console/
│   ├── ConsoleChessGame.java        # Main entry point
│   ├── EnhancedConsoleDisplay.java  # Color-coded board display
│   ├── MoveValidator.java          # Input validation with error feedback
│   ├── HumanPlayerController.java  # Human input handling
│   ├── AIPlayerController.java     # AI move generation
│   └── AIvsAIChessGame.java        # AI vs AI mode
├── interfaces/
│   └── IChessGameObserver.java     # Observer pattern for game events
└── web/
    └── WebChessGame.java           # Web interface (placeholder)

src/test/java/com/ddemott/chessai/  # 90+ test classes, 246+ tests
config/                              # Checkstyle & SpotBugs baselines
scripts/                             # Build, hook, and analysis scripts
docs/                                # ARCHITECTURE.md, COMMIT_PROCEDURE.md, PLAN.md
```

## Architecture

Three-layer design:

1. **View (Console):** `console/` package - UI rendering, input parsing
2. **Facade (Engine):** `GameEngine` - orchestrates game flow, delegates to domain
3. **Domain (Core):** `Board`, `State`, pieces, AI - pure game logic

Key patterns:
- **Strategy:** `AIStrategy` interface with `MinMaxStrategy` implementation
- **Observer:** `IChessGameObserver` for game event notifications
- **Facade:** `GameEngine` hides domain complexity from the UI layer
- **Deep Cloning:** `State` and `Board` are cloned for AI evaluation without side effects

## Code Conventions

### Style
- **Packages:** lowercase (`com.ddemott.chessai.*`)
- **Classes:** PascalCase (`GameEngine`, `MinMaxStrategy`)
- **Constants:** UPPER_SNAKE_CASE in `GameConstants`
- **Methods:** camelCase (`movePiece()`, `evaluateBoard()`)
- **Formatting:** Eclipse formatter via Spotless - run `mvn spotless:apply` before committing

### Type Safety
- Use `Side` enum (WHITE/BLACK), not Strings for colors
- Use `AIDifficulty` enum for difficulty levels
- Use `GameConstants` for magic numbers - don't hardcode values

### Commit Messages
Use Conventional Commits: `feat:`, `fix:`, `refactor:`, `docs:`, `test:`

Example: `fix(Board): set piece.position in setPieceAt() to keep piece board sync`

## Important Rules

### No Debug Prints in Core Code
`System.out.println`, `System.err.println`, and `.printStackTrace()` are **forbidden** outside the `console/` package. The pre-commit hook and CI will reject them. Use proper error handling instead.

### Always Run Tests Before Committing
```bash
mvn -q test
```
All 246+ tests must pass. Test timeout is 60 seconds per forked process.

### Formatting Must Pass
```bash
mvn spotless:check
```
If it fails, fix with `mvn spotless:apply`.

## CI Pipeline

Triggered on push to `main` and PRs targeting `main`. Steps:
1. Build and run tests (`mvn -q -B test`)
2. Debug print scan (`scripts/check-debug-prints.sh`)
3. Checkstyle baseline comparison
4. SpotBugs baseline comparison

## Git Hooks

Install with `bash scripts/install-hooks`. Hooks are in `.githooks/`:

- **pre-commit:** Scans staged files for debug prints; runs smoke tests if test files changed
- **pre-push:** Full test suite for `main`, smoke tests for feature branches; debug print scan

## Testing Conventions

- Test files mirror source structure in `src/test/java/com/ddemott/chessai/`
- Name test classes as `<Feature>Test.java` (e.g., `CastlingTest.java`)
- Integration tests use `<Feature>IntegrationTest.java` suffix
- Use descriptive method names: `testKingsideCastling_success()`
- Set up board positions programmatically using `Board` and `State` constructors

## Key Implementation Details

- **Board:** 8x8 `Piece[][]` array. Row 0 = rank 8 (Black's back rank), Row 7 = rank 1 (White's back rank)
- **Castling:** Full validation (king/rook haven't moved, no pieces between, king not in/through/into check)
- **En Passant:** Tracked via last move; only valid immediately after opponent's two-square pawn advance
- **Pawn Promotion:** Automatic when pawn reaches opposite rank; supports all piece types
- **AI:** Minimax with alpha-beta pruning; depth configured by `AIDifficulty` enum
- **Undo/Redo:** Full support with board integrity preservation via `MoveHistory`

## Documentation

- `README.md` - User-facing docs, features, game instructions
- `CONTRIBUTING.md` - Development workflow, PR guidelines
- `CHANGELOG.md` - Version history
- `docs/ARCHITECTURE.md` - System design and domain model
- `docs/COMMIT_PROCEDURE.md` - Branching, commit, and push procedures
- `.github/PULL_REQUEST_TEMPLATE.md` - PR template with checklist
