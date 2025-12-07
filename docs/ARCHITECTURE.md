# ChessAI Architecture

This document describes the high-level architecture, design decisions, and mental model of the ChessAI system. It is intended for developers who want to understand the "why" and "how" of the system's construction.

## 1. System Metaphor & High-Level Design

The system follows a layered architecture designed to strictly separate the **Core Game Logic** from the **User Interface**. This separation ensures that the chess engine can drive a Console UI, a Web UI, or run headlessly for testing without code changes.

### The Three Layers
1.  **The View (Presentation):**
    *   Handles user input (CLI commands, mouse clicks).
    *   Renders the board state.
    *   *Examples:* `ConsoleChessGame`, `WebChessGame`.
2.  **The Facade (Application Logic):**
    *   The primary entry point for any UI.
    *   Orchestrates the game flow, manages the AI turn, and handles history.
    *   *Core Class:* `GameEngine`.
3.  **The Domain (Business Logic):**
    *   Encapsulates the rules of chess, board state, piece movement, and AI calculation.
    *   *Core Classes:* `Board`, `State`, `Piece`, `MinMaxStrategy`.

### Data Flow
1.  **Input:** User provides a command (e.g., "e2 e4").
2.  **Controller:** The UI layer validates format and calls `GameEngine.movePiece()`.
3.  **Engine:** The `GameEngine` delegates to `State` to execute the move on the `Board`.
4.  **Validation:** The `Board` checks piece rules and king safety.
5.  **State Update:** If valid, the board is updated, and the move is pushed to `MoveHistory`.
6.  **AI Trigger:** The `GameEngine` detects the turn change and invokes the `AIStrategy`.
7.  **View Refresh:** The UI queries `GameEngine.getBoardRepresentation()` to display the new state.

---

## 2. Core Domain Model

### Board Representation
*   **Decision:** The board is represented as a 2D array of objects: `IPiece[8][8]`.
*   **Trade-off:**
    *   *Pros:* High readability, intuitive Object-Oriented mapping (a "Board" holds "Pieces"), easy to debug.
    *   *Cons:* Heavier memory footprint and slower iteration compared to Bitboards (long integers).
*   **Justification:** For a Java-based project aiming for maintainability and educational value, the OOP approach is superior. The performance cost is negligible for search depths < 8.

### The Piece Hierarchy
*   **Interface:** `IPiece` defines the contract (movement, color, position).
*   **Abstract Base:** `Piece` handles common state (position tracking, color).
*   **Concrete Classes:** `Pawn`, `Rook`, `Knight`, etc., implement specific movement patterns (`isValidMove`).
*   **Polymorphism:** The board logic does not need to know specific piece types to move them; it simply asks `piece.isValidMove()`.

### Coordinate System
*   **Internal:** 0-indexed integer arrays `[row][col]` (0-7) are used for array access.
*   **External/API:** String-based Algebraic Notation (e.g., "e2", "a1") is used for all public methods.
*   **Conversion:** `Board` contains utility methods to convert between these two formats on the fly. This keeps the API user-friendly while keeping internal logic efficient.

---

## 3. The AI Subsystem

### Strategy Pattern
The AI is implemented using the **Strategy Pattern** via the `AIStrategy` interface.
*   **Goal:** Allows hot-swapping different algorithms (e.g., `RandomStrategy` vs `MinMaxStrategy`) or difficulty levels without changing the `GameEngine`.

### Search Algorithm
*   **Algorithm:** Minimax with Alpha-Beta Pruning.
*   **Logic:** The AI simulates moves recursively to build a Game Tree.
    *   *Minimax:* Assumes the opponent plays perfectly to minimize the AI's gain.
    *   *Alpha-Beta:* Prunes branches of the tree that are mathematically guaranteed to be worse than a move already found, significantly speeding up search.

### State Isolation (Immutability simulation)
*   **Problem:** The AI needs to "test" moves without messing up the actual game being played.
*   **Solution:** Deep Cloning.
    *   The `State.clone()` and `Board.clone()` methods create a completely independent copy of the game.
    *   The AI operates strictly on these clones.
    *   This ensures thread safety and data integrity.

### Evaluation Function
The "intelligence" comes from the `Evaluation` class. It scores a board state based on:
1.  **Material:** (Queen=900, Pawn=100, etc.)
2.  **Position:** Static tables (e.g., Knights are better in the center, Pawns better near promotion).
3.  **Safety:** Penalties for open kings or hanging pieces.

---

## 4. State Management & History

### Move History
*   **Structure:** A `List<Move>` records the chronological sequence of the game.
*   **Undo/Redo:**
    *   A `currentMoveIndex` pointer tracks the "present".
    *   Undoing decrements the pointer and reverses the board state.
    *   Redoing increments the pointer and re-applies the move.
    *   *Note:* Branching history (undoing and playing a *different* move) truncates the future list.

### Notation & PGN
*   **Responsibility:** `MoveHistory` is responsible for generating standard chess notation (e.g., "Nf3", "O-O").
*   **Persistence:** The system can serialize this history into PGN (Portable Game Notation) format for saving/loading.

---

## 5. Interface Decoupling (The Facade)

The `GameEngine` class acts as a **Facade**.
*   It hides the complexity of `State`, `Board`, `Evaluation`, and `AIStrategy`.
*   **API Surface:** Methods like `movePiece(from, to)`, `undo()`, `saveGame()`.
*   **Benefit:** The Console UI (and future Web UI) never interacts with a `Pawn` object directly. It interacts purely with the Engine.

---

## 6. Architectural Roadmap

*   **Opening Book:** Integration of a HashMap-based lookup for the first 5-10 moves to improve variety and speed.
*   **Bitboards:** (Long term) Migrating the internal `Board` representation to bitwise operations for performance optimization, allowing deeper AI search.
*   **Event System:** Decoupling the UI further by having the Engine emit events (`onMove`, `onCheckmate`) rather than return values.
