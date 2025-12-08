# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/).

## [Unreleased]

## [2025-12-07]

### Added
- **Console-Decoupled Architecture**: Separated core game logic from console interface.
    - New methods: `getBoardRepresentation()`, `getGameState()`.
    - Enables independent core testing and web interface implementation.
- **Comprehensive Testing**: Added console-free testing framework covering move validation, AI functionality, and turn management.
- **Enhanced Turn Validation**: Added player ownership validation in `State.movePiece()` to prevent moving opponent pieces.

### Fixed
- **AI Move Generation**: Fixed bug where AI suggested invalid moves (e.g., rook to occupied square) by updating `getAllPossibleMoves()` to use `isValidMove()`.
- **Error Handling**: Fixed input stream termination handling in console game to prevent crashes.
- **Memory Leaks**: Fixed memory leaks in AI move generation.

### Fixed (continued)
- **Board Piece Sync & Cloning:** `setPieceAt()` now updates the `IPiece`'s internal `position` and `Board.clone()` creates non-initialized clones with cloned pieces only. This prevents mismatched positions and infinite loop errors during clones.
- **Move Validation / King Safety:** Fixed `isCheckmate()` / `isStalemate()` / `wouldExposeKingToCheck()` to simulate moves using cloned boards and cloned pieces, preventing incorrect detection of escape moves and pinned piece logic errors.
- **King Move Logic:** `King.isValidMove()` now simulates a king move on a clone to ensure it doesn’t move into check — this fixes incorrect allowances of capture on squares that leave the king exposed.
- **Pawn Promotion & Capture Rules:** Fixed pawn promotion edge cases and removed an incorrect rule that allowed a forward capture to promotion rank. Improved detection/handling of promotion moves and promotion-with-capture scenarios.
- **Bishop Coordinates Conversion:** Corrected the bishop coordinate parsing (used `Board.convertPositionToCoordinates()` instead of `Character.getNumericValue`) to fix diagonal detection.
- **Test Correctness:** Updated several test positions that were incorrect (Arabian mate, discovered check, Legal's mate, stalemate) and corrected test assumptions where needed.

### Improved
- **Input Validation**: Enhanced input validation across all layers.
- **Feedback**: Improved error messages and user feedback.
- **Documentation**: Standardized code documentation and comments.

### Improved (continued)
- **Test suite & Debugging:** Added test-specific helpers such as `clearBoard()` and improved debug tests; removed temporary debug runners and verbose debug prints from core logic to avoid polluting test logs.
- **Safety & Robustness:** Strengthened validation in the engine (`State.movePiece()` / `Board` checks) and ensured synchronized piece positions between the `Board` array and `IPiece.position` for consistent validation logic.

### Verified
- All existing tests validated; full test suite now passes: **246/246**.

### Refactored
- **Type Safety**: Introduced `Side` enum to replace legacy String-based color handling ("White"/"Black"), improving type safety across the engine.
- **Constants**: Centralized magic numbers (piece values, board coordinates) into a new `GameConstants` class.
- **Project Structure**: Moved and expanded documentation into a dedicated `docs/` directory (`ARCHITECTURE.md`, `PLAN.md`).
