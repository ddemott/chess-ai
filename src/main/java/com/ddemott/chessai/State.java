package com.ddemott.chessai;

import java.util.List;

import com.ddemott.chessai.ai.AIStrategy;
import com.ddemott.chessai.pieces.IPiece;

public class State {
    private Board board;
    private String currentTurn;
    private AIStrategy aiStrategy;
    private MoveHistory moveHistory;

    public State() {
        this.board = new Board();
        this.currentTurn = "White"; // Starting with white's turn
        this.moveHistory = new MoveHistory();
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
        // Check if there's a piece at the 'from' position
        IPiece piece = board.getPieceAt(from);
        if (piece == null) {
            return false;
        }
        
        // Check if the piece belongs to the current player
        if (!piece.getColor().equals(currentTurn)) {
            return false;
        }
        
        // Get the piece that might be captured
        IPiece capturedPiece = board.getPieceAt(to);
        
        boolean moveSuccessful = board.movePiece(from, to);
        if (moveSuccessful) {
            // Record the move in history
            moveHistory.addMove(from, to, piece, capturedPiece, board, currentTurn);
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
        // Note: Don't clone move history for AI simulation states
        return newState;
    }

    // Getter for move history
    public MoveHistory getMoveHistory() {
        return moveHistory;
    }

    // Undo functionality
    public boolean undoLastMove() {
        if (!moveHistory.canUndo()) {
            return false;
        }
        
        Move lastMove = moveHistory.undoMove();
        if (lastMove == null) {
            return false;
        }
        
        // Restore the board state by reversing the move
        IPiece piece = board.getPieceAt(lastMove.getTo());
        board.setPieceAt(lastMove.getFrom(), piece);
        piece.setPosition(lastMove.getFrom());
        
        // Restore captured piece if any
        if (lastMove.getCapturedPiece() != null) {
            board.setPieceAt(lastMove.getTo(), lastMove.getCapturedPiece());
        } else {
            board.setPieceAt(lastMove.getTo(), null);
        }
        
        // Switch back to the previous player
        toggleTurn();
        return true;
    }

    // Redo functionality  
    public boolean redoLastMove() {
        if (!moveHistory.canRedo()) {
            return false;
        }
        
        Move moveToRedo = moveHistory.redoMove();
        if (moveToRedo == null) {
            return false;
        }
        
        // Re-execute the move
        board.movePiece(moveToRedo.getFrom(), moveToRedo.getTo());
        toggleTurn();
        return true;
    }
}
