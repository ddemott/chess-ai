package com.ddemott.chessai;

import java.util.List;
import com.ddemott.chessai.pieces.*;

import com.ddemott.chessai.ai.AIStrategy;
import com.ddemott.chessai.pieces.IPiece;

public class State {
    public AIStrategy getAIStrategy() {
        return aiStrategy;
    }
    private Board board;
    private Side currentTurn;
    private AIStrategy aiStrategy;
    private MoveHistory moveHistory;

    public State() {
        this.board = new Board();
        this.currentTurn = Side.WHITE; // Starting with white's turn
        this.moveHistory = new MoveHistory();
    }

    public Board getBoard() {
        return board;
    }

    public String getCurrentTurn() {
        return currentTurn.toString();
    }
    
    public Side getCurrentTurnSide() {
        return currentTurn;
    }

    public void setCurrentTurn(String currentTurn) {
        this.currentTurn = currentTurn.equalsIgnoreCase("White") ? Side.WHITE : Side.BLACK;
    }
    
    public void setCurrentTurn(Side side) {
        this.currentTurn = side;
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
        if (piece.getSide() != currentTurn) {
            return false;
        }
        
        // Check if the piece is pinned (can't move because it would expose the king to check)
        if (isPiecePinned(from, to)) {
            return false;
        }
        
        // Get the piece that might be captured
        IPiece capturedPiece = board.getPieceAt(to);
        
        boolean moveSuccessful = board.movePiece(from, to);
        if (moveSuccessful) {
            // Record the position before updating the half-move clock
            String positionFEN = board.toFEN().split(" ")[0]; // Only use board position part of FEN
            moveHistory.addPosition(positionFEN);
            
            // Update half-move clock
            boolean isPawnMove = piece instanceof Pawn;
            boolean isCapture = capturedPiece != null;
            moveHistory.updateHalfmoveClock(isPawnMove, isCapture);
            
            // Record the move in history
            moveHistory.addMove(from, to, piece, capturedPiece, board, currentTurn.toString());
            toggleTurn();
        }
        return moveSuccessful;
    }
    
    /**
     * Checks if moving a piece would expose the king to check (pin)
     * @param from Starting position
     * @param to Ending position
     * @return true if the move would expose the king to check, false otherwise
     */
    private boolean isPiecePinned(String from, String to) {
        return isPiecePinned(from, to, null);
    }
    
    private boolean isPiecePinned(String from, String to, String promotionPiece) {
        IPiece piece = board.getPieceAt(from);
        if (piece == null) {
            return false;
        }
        
        // Skip king moves - kings can't be pinned
        if (piece instanceof King) {
            return false;
        }
        
        // Simulate the move
        Board clonedBoard = this.board.clone();
        
        // Execute the move on the cloned board, handling promotion if needed
        if (promotionPiece != null && piece instanceof Pawn) {
            // For promotion moves, simulate it with the move
            if (!clonedBoard.movePiece(from, to, promotionPiece)) {
                // If promotion move fails, simulate regular move
                clonedBoard.setPieceAt(to, piece.clonePiece());
            }
        } else {
            clonedBoard.setPieceAt(to, piece.clonePiece());
        }
        clonedBoard.setPieceAt(from, null);
        
        // Check if the king is in check after this move
        boolean kingInCheck = clonedBoard.isKingInCheck(piece.getSide());
        
        // If the king is in check, the piece is pinned
        return kingInCheck;
    }
    
    public boolean movePiece(String from, String to, String promotionPiece) {
        // Basic validation
        IPiece piece = board.getPieceAt(from);
        if (piece == null || piece.getSide() != currentTurn) {
            return false;
        }
        
        // Check for captured piece before move
        IPiece capturedPiece = board.getPieceAt(to);
        
        // Execute move using Board class (which handles validation and pin checking)
        boolean moveSuccessful = board.movePiece(from, to, promotionPiece);
        
        // Update game state if move was successful
        if (moveSuccessful) {
            // Record position
            String positionFEN = board.toFEN().split(" ")[0];
            moveHistory.addPosition(positionFEN);
            
            // Update half-move clock
            boolean isPawnMove = piece instanceof Pawn;
            moveHistory.updateHalfmoveClock(isPawnMove, capturedPiece != null);
            
            // Record move in history with promotion info if applicable
            if (promotionPiece != null) {
                moveHistory.addMove(from, to, piece, capturedPiece, board, currentTurn.toString(), promotionPiece);
            } else {
                moveHistory.addMove(from, to, piece, capturedPiece, board, currentTurn.toString());
            }
            
            toggleTurn();
        }
        
        return moveSuccessful;
    }

    public String getBestMove() {
        return aiStrategy.calculateBestMove(this, currentTurn.toString());
    }

    private void toggleTurn() {
        currentTurn = currentTurn.flip();
    }

    public List<String> getAllPossibleMoves(String color) {
        return board.getAllPossibleMoves(color);
    }
    
    public List<String> getAllPossibleMoves(Side side) {
        return board.getAllPossibleMoves(side);
    }

    @Override
    public State clone() {
        State newState = new State();
        newState.board = this.board.clone();
        newState.currentTurn = this.currentTurn;
        newState.setAIStrategy(this.aiStrategy); // Keep the same strategy
        // Deep copy move history (including position history)
        MoveHistory oldHistory = this.moveHistory;
        MoveHistory newHistory = new MoveHistory();
        // Copy moves
        for (Move move : oldHistory.getMoves()) {
            // Moves are immutable, so shallow copy is fine
            newHistory.getMoves().add(move);
        }
        // Copy position history
        for (String pos : oldHistory.getPositionHistory()) {
            newHistory.getPositionHistory().add(pos);
        }
        // Copy halfmove clock
        try {
            java.lang.reflect.Field halfmoveClockField = MoveHistory.class.getDeclaredField("halfmoveClock");
            halfmoveClockField.setAccessible(true);
            halfmoveClockField.setInt(newHistory, oldHistory.getHalfmoveClock());
        } catch (Exception e) {
            // Ignore if reflection fails
        }
        // Copy current move index
        try {
            java.lang.reflect.Field currentMoveIndexField = MoveHistory.class.getDeclaredField("currentMoveIndex");
            currentMoveIndexField.setAccessible(true);
            currentMoveIndexField.setInt(newHistory, oldHistory.getMoves().size() - 1);
        } catch (Exception e) {
            // Ignore if reflection fails
        }
        newState.moveHistory = newHistory;
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

    /**
     * Check if the specified color is in stalemate
     * A stalemate occurs when the player to move is not in check but has no legal moves
     */
    public boolean isStalemate(String color) {
        return isStalemate(color.equalsIgnoreCase("White") ? Side.WHITE : Side.BLACK);
    }
    
    public boolean isStalemate(Side side) {
        // Delegate to Board's isStalemate method which has the corrected logic
        return board.isStalemate(side);
    }

    /**
     * Check if the current position has occurred three times
     * This uses the move history to check for repetitions
     */
    public boolean isThreefoldRepetition() {
        // Get all positions from move history
        List<String> positions = moveHistory.getPositionHistory();
        if (positions.size() < 3) { // Need at least 3 occurrences for threefold repetition
            return false;
        }

        // Get current position FEN (without move numbers and counters)
        String currentPosition = board.toFEN().split(" ")[0];

        // Count occurrences of current position
        int repetitions = 0;
        for (String position : positions) {
            if (position.split(" ")[0].equals(currentPosition)) {
                repetitions++;
            }
        }
        return repetitions >= 3;
    }

    /**
     * Check if fifty moves have been made without a pawn move or capture
     */
    public boolean isFiftyMoveRule() {
        return moveHistory.getHalfmoveClock() >= 100; // 50 moves = 100 half-moves
    }

    /**
     * Checks if the game is over due to checkmate, stalemate, draws by repetition or fifty-move rule
     * @return true if the game is over, false otherwise
     */
    public boolean isGameOver() {
        // Check for checkmate - if the current player's king is in check and they have no legal moves
        if (board.isKingInCheck(currentTurn) && isStalemate(currentTurn)) {
            return true;
        }
        
        // Check for stalemate - if the current player has no legal moves but is not in check
        if (isStalemate(currentTurn) && !board.isKingInCheck(currentTurn)) {
            return true;
        }
        
        // Check for threefold repetition
        if (isThreefoldRepetition()) {
            return true;
        }
        
        // Check for fifty-move rule
        if (isFiftyMoveRule()) {
            return true;
        }
        
        return false;
    }
}