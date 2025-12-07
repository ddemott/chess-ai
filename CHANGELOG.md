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

### Improved
- **Input Validation**: Enhanced input validation across all layers.
- **Feedback**: Improved error messages and user feedback.
- **Documentation**: Standardized code documentation and comments.
