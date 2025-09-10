package com.ddemott.chessai.console;

import com.ddemott.chessai.engine.GameEngine;
import com.ddemott.chessai.Move;

/**
 * PlayerController interface for pluggable player types (human or AI)
 */
public interface PlayerController {
    /**
     * Selects a move for the current player.
     * @param gameEngine The game engine instance
     * @return The move to play (or null to resign/exit)
     */
    Move selectMove(GameEngine gameEngine);
}
