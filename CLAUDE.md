# CLAUDE.md — ChessAI

## Project overview
Java chess engine with AI (minimax + alpha-beta pruning), console UI, and comprehensive test suite.
- **Java 21**, **Maven** build system
- Package: `com.ddemott.chessai`
- Main class: `com.ddemott.chessai.console.ConsoleChessGame`

## Build & test commands
```bash
mvn clean compile              # Compile
mvn -q test                    # Run full test suite (quiet output)
mvn clean package              # Build fat JAR
mvn verify                     # Tests + formatting check + debug-print check
mvn spotless:apply             # Auto-format code
mvn -Dtest=com.ddemott.chessai.CastlingTest test           # Single test class
mvn -Dtest=com.ddemott.chessai.CastlingTest#testMethod test # Single test method
mvn -Dskip.debug.prints=true verify   # Skip debug-print check
```

## Code style & conventions
- **Formatter:** Spotless (Eclipse formatter). Run `mvn spotless:apply` before committing.
- **Logging:** Use SLF4J (`Log` wrapper in `util/`). No `System.out.println` in core code — only allowed in `console/` package and `examples/`.
- **Commit messages:** Conventional Commits — `feat:`, `fix:`, `refactor:`, `docs:`, `test:`, `style:`, `chore:`. Scoped format OK: `fix(Board): description`.

## Architecture (layered)
- **View** (`console/`) → **Facade** (`engine/GameEngine`) → **Domain** (`Board`, `State`, `Piece`, `ai/`)
- AI uses Strategy pattern (`AIStrategy` interface, `MinMaxStrategy` implementation)
- `State` uses deep cloning for AI simulation without mutating game state

## Key packages
| Package | Purpose |
|---------|---------|
| `chessai/` | Core: Board, State, Move, Evaluation, GameConstants |
| `chessai/pieces/` | Piece implementations (Pawn, Rook, Knight, Bishop, Queen, King) |
| `chessai/ai/` | AI strategy, minimax, opening book |
| `chessai/engine/` | GameEngine facade |
| `chessai/console/` | Console UI, display, input handling |
| `chessai/util/` | Logging utilities |

## Pre-commit hooks
Installed via `bash scripts/install-hooks`. Checks for debug prints and runs smoke tests.

## CI
GitHub Actions on push/PR to main: debug-print check, build & test (Java 21), Checkstyle + SpotBugs baseline comparison.

## Testing
60+ test classes covering core rules, special moves (castling, en passant, promotion), draws, AI, and integration scenarios. Surefire timeout: 60s per test.
