package com.ddemott.chessai.console;

import java.util.List;
import java.util.Set;
import java.util.HashSet;

import com.ddemott.chessai.Board;
import com.ddemott.chessai.pieces.IPiece;

/**
 * Enhanced error handling and move validation with detailed feedback
 */
public class MoveValidator {
    
    public enum MoveError {
        NO_PIECE_AT_SOURCE("No piece found at the source square"),
        WRONG_PLAYER_PIECE("You cannot move your opponent's pieces"),
        SAME_COLOR_CAPTURE("You cannot capture your own pieces"),
        INVALID_PIECE_MOVEMENT("This piece cannot move in that way"),
        PATH_BLOCKED("The path to the destination is blocked"),
        KING_IN_CHECK_AFTER_MOVE("This move would leave your king in check"),
        OUT_OF_BOUNDS("The destination square is outside the board"),
        KING_WOULD_BE_IN_CHECK("This move would put your king in check"),
        INVALID_FORMAT("Move format should be like 'e2 e4'");
        
        private final String message;
        
        MoveError(String message) {
            this.message = message;
        }
        
        public String getMessage() {
            return message;
        }
    }
    
    /**
     * Validates a move and returns detailed error information
     */
    public static MoveValidationResult validateMove(String from, String to, String currentPlayer, Board board) {
        // Check input format
        if (from == null || to == null || from.length() != 2 || to.length() != 2) {
            return new MoveValidationResult(false, MoveError.INVALID_FORMAT, null);
        }
        
        // Check bounds
        if (!isValidPosition(from) || !isValidPosition(to)) {
            return new MoveValidationResult(false, MoveError.OUT_OF_BOUNDS, null);
        }
        
        // Check if there's a piece at source
        IPiece piece = board.getPieceAt(from);
        if (piece == null) {
            return new MoveValidationResult(false, MoveError.NO_PIECE_AT_SOURCE, null);
        }
        
        // Check if piece belongs to current player
        if (!piece.getColor().equals(currentPlayer)) {
            return new MoveValidationResult(false, MoveError.WRONG_PLAYER_PIECE, null);
        }
        
        // Check if trying to capture own piece
        IPiece targetPiece = board.getPieceAt(to);
        if (targetPiece != null && targetPiece.getColor().equals(currentPlayer)) {
            return new MoveValidationResult(false, MoveError.SAME_COLOR_CAPTURE, null);
        }
        
        // Check if the piece can make this move
        if (!piece.isValidMove(to, board)) {
            return new MoveValidationResult(false, MoveError.INVALID_PIECE_MOVEMENT, null);
        }
        
        // Check if move would leave king in check
        Board clonedBoard = board.clone();
        if (clonedBoard.movePiece(from, to)) {
            if (clonedBoard.isKingInCheck(currentPlayer)) {
                return new MoveValidationResult(false, MoveError.KING_WOULD_BE_IN_CHECK, null);
            }
        }
        
        // Generate possible moves for suggestions
        Set<String> possibleMoves = generatePossibleMovesForPiece(from, board);
        
        return new MoveValidationResult(true, null, possibleMoves);
    }
    
    /**
     * Check if a position string is valid (e.g., "e4")
     */
    private static boolean isValidPosition(String position) {
        if (position.length() != 2) return false;
        char file = position.charAt(0);
        char rank = position.charAt(1);
        return file >= 'a' && file <= 'h' && rank >= '1' && rank <= '8';
    }
    
    /**
     * Generate all possible moves for a piece at a given position
     */
    private static Set<String> generatePossibleMovesForPiece(String position, Board board) {
        Set<String> moves = new HashSet<>();
        IPiece piece = board.getPieceAt(position);
        
        if (piece != null) {
            List<String> allMoves = piece.getAllPossibleMoves(board);
            for (String move : allMoves) {
                String[] parts = move.split(" ");
                if (parts.length == 2 && parts[0].equals(position)) {
                    moves.add(parts[1]);
                }
            }
        }
        
        return moves;
    }
    
    /**
     * Generate move suggestions when a move fails
     */
    public static List<String> generateMoveSuggestions(String from, Board board, String currentPlayer) {
        IPiece piece = board.getPieceAt(from);
        if (piece == null || !piece.getColor().equals(currentPlayer)) {
            return List.of(); // No suggestions if no piece or wrong player
        }
        
        List<String> moves = piece.getAllPossibleMoves(board);
        return moves.stream()
                   .filter(move -> move.startsWith(from + " "))
                   .limit(3)
                   .toList();
    }
    
    /**
     * Result of move validation
     */
    public static class MoveValidationResult {
        private final boolean isValid;
        private final MoveError error;
        private final Set<String> possibleDestinations;
        
        public MoveValidationResult(boolean isValid, MoveError error, Set<String> possibleDestinations) {
            this.isValid = isValid;
            this.error = error;
            this.possibleDestinations = possibleDestinations;
        }
        
        public boolean isValid() { return isValid; }
        public MoveError getError() { return error; }
        public Set<String> getPossibleDestinations() { return possibleDestinations; }
    }
}
