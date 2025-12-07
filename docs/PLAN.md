# Development Plan

This document outlines the strategic roadmap for the ChessAI project, focusing on immediate feature additions, web integration, and long-term architectural improvements.

## 1. AI Enhancements (Immediate Priority)

### 1.1. Opening Book Integration
**Goal:** Prevent repetitive gameplay and improve early-game strength.
- [ ] **Data Structure:** Create `OpeningBook` class using `HashMap<String, List<String>>`.
    - Key: Space-separated move history (e.g., `"e2 e4 e7 e5"`).
    - Value: List of candidate moves (e.g., `["g1 f3", "f1 c4"]`).
- [ ] **Content:** Populate with standard openings:
    - Open Games (Ruy Lopez, Italian).
    - Sicilian Defense.
    - Queen's Gambit.
    - Indian Defenses.
- [ ] **Integration:** Modify `GameEngine` or `AIStrategy`:
    1.  Check `OpeningBook` first.
    2.  If entry exists, randomly select a move.
    3.  If no entry, fall back to Minimax calculation.
- [ ] **Fallback Handling:** Ensure any deviation from book lines smoothly transitions to the calculation engine.

### 1.2. Mid-Game Heuristics
**Goal:** Improve AI "understanding" without increasing search depth cost.
- [ ] **Piece Activity:** Bonus for pieces with many valid moves.
- [ ] **Pawn Structure:** Penalties for doubled pawns or isolated pawns.
- [ ] **King Safety:** Logic to detect open files near the king.

---

## 2. Web Interface (Phase 2)

**Goal:** Transform the console-based game into a web application accessible via browser.

### 2.1. Backend API (REST/Spring Boot)
- [ ] **Project Setup:** Add Spring Boot dependencies to `pom.xml`.
- [ ] **Game State Management:** Create a `GameService` to hold active `GameEngine` instances in memory (Map<ID, Engine>).
- [ ] **Endpoints:**
    - `POST /api/games` - Start new game.
    - `POST /api/games/{id}/move` - Submit move (returns new board + AI response).
    - `GET /api/games/{id}` - Get current board state (polling).
    - `POST /api/games/{id}/undo` - Undo move.

### 2.2. Frontend
- [ ] **Technology:** Simple HTML5/JS or React.
- [ ] **Board Rendering:** Visual representation of the 8x8 grid.
- [ ] **Interaction:** Drag-and-drop or click-click movement.
- [ ] **Communication:** Fetch API calls to the backend endpoints.

---

## 3. Maintenance & Refactoring

### 3.1. Documentation
- [x] Create `docs/` directory.
- [x] Create `ARCHITECTURE.md`.
- [x] Create `PLAN.md`.
- [ ] Move `MOVE_HISTORY_TESTS.md` to `docs/` (Completed).

### 3.2. Performance
- [ ] **Move Ordering:** Optimize Alpha-Beta pruning by checking "good" moves (captures/checks) first.
- [ ] **Transposition Table:** Cache evaluated positions to avoid re-calculating the same state via different move orders.

---

## 4. Long-Term Goals

- **Bitboards:** Re-implement the `Board` class using bitwise operations for 10x+ performance gain.
- **Multiplayer:** WebSocket implementation for real-time Human vs. Human play.
- **PGN Loading:** Ability to load historical games and play from a specific position.
