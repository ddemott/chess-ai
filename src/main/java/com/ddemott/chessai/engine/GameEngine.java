package com.ddemott.chessai.engine;

import com.ddemott.chessai.State;
import com.ddemott.chessai.ai.AIStrategy;
import com.ddemott.chessai.ai.MinMaxStrategy;

public class GameEngine {
    private State state;
    private AIStrategy aiStrategy;

    public GameEngine(int aiDepth) {
        this.state = new State();
        this.aiStrategy = new MinMaxStrategy(aiDepth);
        state.setAIStrategy(aiStrategy);
    }

    public String getCurrentTurn() {
        return state.getCurrentTurn();
    }

    public boolean movePiece(String from, String to) {
        return state.movePiece(from, to);
    }

    public void makeAIMove() {
        String aiMove = state.getBestMove();
        String[] aiPositions = aiMove.split(" ");
        movePiece(aiPositions[0], aiPositions[1]);
        System.out.println("AI selected move for " + state.getCurrentTurn() + ": " + aiMove);
    }

    public void printBoard() {
        state.getBoard().printBoard();
    }
}
