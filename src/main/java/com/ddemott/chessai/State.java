package com.ddemott.chessai;

import java.util.List;

import com.ddemott.chessai.ai.AIStrategy;

public class State {
    private Board board;
    private String currentTurn;
    private AIStrategy aiStrategy;

    public State() {
        this.board = new Board();
        this.currentTurn = "White"; // Starting with white's turn
    }

    public Board getBoard() {
        return board;
    }

    public String getCurrentTurn() {
        return currentTurn;
    }

    public void setCurrentTurn(String currentTurn) {
        this.currentTurn = currentTurn;
    }

    public void setAIStrategy(AIStrategy aiStrategy) {
        this.aiStrategy = aiStrategy;
    }

    public boolean movePiece(String from, String to) {
        boolean moveSuccessful = board.movePiece(from, to);
        if (moveSuccessful) {
            toggleTurn();
        }
        return moveSuccessful;
    }

    public String getBestMove() {
        return aiStrategy.calculateBestMove(this, currentTurn);
    }

    private void toggleTurn() {
        currentTurn = currentTurn.equals("White") ? "Black" : "White";
    }

    public List<String> getAllPossibleMoves(String color) {
        return board.getAllPossibleMoves(color);
    }

    @Override
    public State clone() {
        State newState = new State();
        newState.board = this.board.clone();
        newState.currentTurn = this.currentTurn;
        newState.setAIStrategy(this.aiStrategy); // Keep the same strategy
        return newState;
    }
}
