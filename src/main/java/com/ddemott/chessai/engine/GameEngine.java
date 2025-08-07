package com.ddemott.chessai.engine;

import com.ddemott.chessai.State;
import com.ddemott.chessai.MoveHistory;
import com.ddemott.chessai.Move;
import com.ddemott.chessai.ai.AIStrategy;
import com.ddemott.chessai.ai.MinMaxStrategy;

/**
 * Manages the state and logic of the chess game.
 */
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
    
    public boolean movePiece(String from, String to, String promotionPiece) {
        return state.movePiece(from, to, promotionPiece);
    }

    public String getBestMove() {
        return state.getBestMove();
    }

    public void makeAIMove() {
        String aiMove = getBestMove();
        if (aiMove != null) {
            String[] aiPositions = aiMove.split(" ");
            if (aiPositions.length == 2) {
                boolean moveSuccess = movePiece(aiPositions[0], aiPositions[1]);
                if (!moveSuccess) {
                    // Log that AI move failed - this shouldn't happen if AI is working correctly
                    System.err.println("Warning: AI suggested invalid move: " + aiMove);
                }
            }
        }
    }

    public String getBoardRepresentation() {
        return state.getBoard().getBoardRepresentation();
    }

    public State getGameState() {
        return state;
    }

    public void printBoard() {
        state.getBoard().printBoard();
    }

    // Move history related methods
    public MoveHistory getMoveHistory() {
        return state.getMoveHistory();
    }

    public String getMoveListDisplay() {
        return state.getMoveHistory().getMoveListDisplay();
    }

    public Move getLastMove() {
        return state.getMoveHistory().getLastMove();
    }

    public boolean undoLastMove() {
        return state.undoLastMove();
    }

    public boolean redoLastMove() {
        return state.redoLastMove();
    }

    public boolean canUndo() {
        return state.getMoveHistory().canUndo();
    }

    public boolean canRedo() {
        return state.getMoveHistory().canRedo();
    }

    public String exportGameToPGN(String whitePlayer, String blackPlayer, String result) {
        return state.getMoveHistory().exportToPGN(whitePlayer, blackPlayer, result);
    }

    public boolean saveGameToPGNFile(String filename, String whitePlayer, String blackPlayer, String result) {
        return state.getMoveHistory().saveToPGNFile(filename, whitePlayer, blackPlayer, result);
    }
}
