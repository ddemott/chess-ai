# TODO — ChessAI

> Resolved bug fixes moved to [RESOLVED.md](RESOLVED.md).

## Priority 3: Code Quality

- [ ] **Deprecated String-based Side/Color parameters throughout codebase**
  Multiple files (`Board.java`, `State.java`, `Evaluation.java`) — methods accepting `String color` when `Side` enum exists. Pattern: `color.equalsIgnoreCase("White") ? Side.WHITE : Side.BLACK` repeated everywhere. Deprecate or remove String overloads.

- [ ] **Legacy String-based constructors in piece classes**
  `King.java`, `Rook.java`, `Bishop.java` etc. — backward-compatibility constructors marked for removal ("we can remove this later").

- [ ] **Resource leaks: Scanner not in try-with-resources**
  `ConsoleChessGame.java`, `ChessConsole.java`, `ChessConsoleAI.java` — `Scanner(System.in)` created but not managed with try-with-resources.

- [ ] **Overly broad exception handling in GameEngine**
  `GameEngine.java` lines 123-125, 129-132 — catches `Exception` instead of specific types.

## Priority 4: Test Gaps

- [ ] **No JUnit tests for undo/redo of special moves**
  Need tests for: undo/redo castling, undo/redo en passant, undo/redo promotion. `UndoRedoBoardIntegrityTest.java` uses `main()` not `@Test`.

- [ ] **No tests for algebraic notation disambiguation**
  `MoveHistory.getDisambiguation()` is implemented but has zero test coverage. Need tests for: multiple knights to same square, file vs rank disambiguation, etc.

- [ ] **No tests for check/checkmate symbols in notation**
  Check (+) and checkmate (#) appending is implemented but not verified by any test.

- [ ] **No tests for PGN round-trip with special moves**
  Need: export game with castling/promotion/en passant → reimport → verify board matches.

- [ ] **No tests for console commands**
  `HumanPlayerController` command handling (help, undo, redo, save, load, export, difficulty, suggest, captured) is completely untested.

- [ ] **Many important tests use main() instead of @Test**
  Not run by `mvn test` or CI: `PGNLoadingTest`, `MoveClassTest`, `UndoRedoBoardIntegrityTest`, `EnPassantTest`, `PawnPromotionTest`, `ComprehensiveMoveHistoryTest`, and 5 demo files.

- [ ] **EnhancedConsoleDisplayTest ~80% commented out**
  Only a fraction of the test class is active.

- [ ] **No dedicated tests for Coordinate class**
  New class with only indirect test coverage through board conversion tests.

- [ ] **Opening book has runtime errors**
  Test output shows `Index out of bounds` errors during book move matching. No dedicated tests for `OpeningBook.java`.

## Priority 5: Future Enhancements

- [ ] **Web interface** — REST API + frontend (per docs/PLAN.md)
- [ ] **Bitboard reimplementation** — 10x+ performance (per docs/PLAN.md)
- [ ] **Transposition table** — cache evaluated positions for AI
- [ ] **Move ordering optimization** — improve alpha-beta pruning efficiency
- [ ] **Endgame tablebase support**
