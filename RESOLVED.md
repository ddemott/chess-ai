# RESOLVED

> Historical log of completed items pulled out of TODO.md and other working lists. Append-only; grouped by date.

## 2026-07-03

- **En passant captures not tracked in captured pieces list** — `Board.executeEnPassant()` now adds the captured pawn to the captured list. _resolved in this commit_
- **Promotion captures not tracked in captured pieces list** — `Board.movePiece(from, to, promotionPiece)` now records the piece captured on the promotion square. _resolved in this commit_
- **Board.clone() doesn't copy captured pieces lists** — cloned boards now copy `capturedWhitePieces`/`capturedBlackPieces`. _resolved in this commit_
- **State.clone() uses reflection to copy MoveHistory private fields** — added `MoveHistory.copy()` and replaced the reflection hack. _resolved in this commit_
- **MoveHistory.addPosition() undo/redo offset wrong** — trim positions before adding, using `currentMoveIndex + 1` instead of `+2`. _resolved in this commit_
- **King.java catches NullPointerException as control flow** — removed all 4 `catch (NullPointerException)` blocks; coordinates validated upstream. _resolved in this commit_
- **NPE risk in Board.movePiece() coordinate access** — added null checks for `convertPositionToCoordinates()` at method entry; reuse validated coords downstream. _resolved in this commit_
- **e.printStackTrace() in GameEngine.getBookMove()** — removed; the existing `Log.warn` already reports the best-effort book-lookup failure. _resolved in this commit_
- **System.err.println in GameEngine.verifyGameStateIntegrity()** — removed; redundant with the preceding `Log.error(error)`. _resolved in this commit_
