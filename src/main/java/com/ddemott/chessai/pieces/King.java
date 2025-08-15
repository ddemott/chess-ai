package com.ddemott.chessai.pieces;

import java.util.ArrayList;
import java.util.List;

import com.ddemott.chessai.Board;

public class King extends Piece {

    public King(String color, String position) {
        super(color, position);
    }

    @Override
    public boolean isValidMove(String newPosition, Board board) {
        String currentPosition = getPosition();
        int[] currentCoords = board.convertPositionToCoordinates(currentPosition);
        int[] newCoords = board.convertPositionToCoordinates(newPosition);

        int dx = Math.abs(newCoords[0] - currentCoords[0]);
        int dy = Math.abs(newCoords[1] - currentCoords[1]);
        
        // Normal king move (one square in any direction)
        if (dx <= 1 && dy <= 1) {
            IPiece pieceAtDestination = board.getPieceAt(newPosition);
            return pieceAtDestination == null || !pieceAtDestination.getColor().equals(getColor());
        }
        
        // Check for castling (king moves two squares horizontally)
        if (dx == 0 && dy == 2 && !hasMoved()) {
            return isValidCastling(currentPosition, newPosition, board);
        }

        return false;
    }
    
    /**
     * Validates if castling is legal for the given move
     */
    private boolean isValidCastling(String from, String to, Board board) {
        int[] fromCoords = board.convertPositionToCoordinates(from);
        int[] toCoords = board.convertPositionToCoordinates(to);
        
        // Determine if this is kingside (right) or queenside (left) castling
        boolean isKingside = toCoords[1] > fromCoords[1];
        
        // Get rook position
        String rookPosition;
        if (isKingside) {
            rookPosition = board.convertCoordinatesToPosition(fromCoords[0], 7); // h-file
        } else {
            rookPosition = board.convertCoordinatesToPosition(fromCoords[0], 0); // a-file
        }
        
        // Check if rook exists and hasn't moved
        IPiece rook = board.getPieceAt(rookPosition);
        if (rook == null || !rook.getClass().getSimpleName().equals("Rook") || 
            !rook.getColor().equals(getColor()) || rook.hasMoved()) {
            return false;
        }
        
        // Check if path between king and rook is clear
        int startCol = Math.min(fromCoords[1], board.convertPositionToCoordinates(rookPosition)[1]);
        int endCol = Math.max(fromCoords[1], board.convertPositionToCoordinates(rookPosition)[1]);
        
        for (int col = startCol + 1; col < endCol; col++) {
            String checkPosition = board.convertCoordinatesToPosition(fromCoords[0], col);
            if (board.getPieceAt(checkPosition) != null) {
                return false; // Path is blocked
            }
        }
        
        // Check if king is currently in check
        if (board.isKingInCheck(getColor())) {
            return false;
        }
        
        // Check if king passes through or lands on an attacked square
        String kingPath1 = board.convertCoordinatesToPosition(fromCoords[0], fromCoords[1] + (isKingside ? 1 : -1));
        String kingPath2 = to;
        
        if (board.isSquareUnderAttack(kingPath1, getColor()) || 
            board.isSquareUnderAttack(kingPath2, getColor())) {
            return false;
        }
        
        return true;
    }

    @Override
    public int getValue() {
        return 0; // The King is invaluable
    }

    @Override
    public IPiece clonePiece() {
        King cloned = new King(getColor(), getPosition());
        cloned.setHasMoved(this.hasMoved());
        return cloned;
    }

    @Override
    public List<String> getAllPossibleMoves(Board board) {
        List<String> possibleMoves = new ArrayList<>();
        String currentPosition = getPosition();
        int[] currentCoords = board.convertPositionToCoordinates(currentPosition);

        // Regular king moves (one square in any direction)
        int[][] directions = {
            {1, 0}, {-1, 0}, {0, 1}, {0, -1},
            {1, 1}, {-1, -1}, {1, -1}, {-1, 1}
        };

        for (int[] direction : directions) {
            int row = currentCoords[0] + direction[0];
            int col = currentCoords[1] + direction[1];
            if (row >= 0 && row < 8 && col >= 0 && col < 8) {
                String newPosition = board.convertCoordinatesToPosition(row, col);
                // Check normal king move (not castling)
                int dx = Math.abs(row - currentCoords[0]);
                int dy = Math.abs(col - currentCoords[1]);
                if (dx <= 1 && dy <= 1) {
                    IPiece pieceAtDestination = board.getPieceAt(newPosition);
                    if (pieceAtDestination == null || !pieceAtDestination.getColor().equals(getColor())) {
                        possibleMoves.add(currentPosition + " " + newPosition);
                    }
                }
            }
        }
        
        // Add castling moves if king hasn't moved (but avoid recursion by doing basic checks only)
        if (!hasMoved()) {
            // Kingside castling - basic validation without check detection
            String kingsideCastle = board.convertCoordinatesToPosition(currentCoords[0], currentCoords[1] + 2);
            if (isBasicCastlingValid(currentPosition, kingsideCastle, board)) {
                possibleMoves.add(currentPosition + " " + kingsideCastle);
            }
            
            // Queenside castling - basic validation without check detection  
            String queensideCastle = board.convertCoordinatesToPosition(currentCoords[0], currentCoords[1] - 2);
            if (isBasicCastlingValid(currentPosition, queensideCastle, board)) {
                possibleMoves.add(currentPosition + " " + queensideCastle);
            }
        }

        return possibleMoves;
    }
    
    /**
     * Basic castling validation without check detection (to avoid recursion)
     */
    private boolean isBasicCastlingValid(String from, String to, Board board) {
        // Validate input parameters
        if (from == null || to == null) {
            return false;
        }
        
        int[] fromCoords = board.convertPositionToCoordinates(from);
        int[] toCoords = board.convertPositionToCoordinates(to);
        
        // Check for invalid coordinates
        if (fromCoords == null || toCoords == null) {
            return false;
        }
        
        // Determine if this is kingside (right) or queenside (left) castling
        boolean isKingside = toCoords[1] > fromCoords[1];
        
        // Get rook position
        String rookPosition;
        if (isKingside) {
            rookPosition = board.convertCoordinatesToPosition(fromCoords[0], 7); // h-file
        } else {
            rookPosition = board.convertCoordinatesToPosition(fromCoords[0], 0); // a-file
        }
        
        // Check if rook position is valid
        if (rookPosition == null) {
            return false;
        }
        
        // Check if rook exists and hasn't moved
        IPiece rook = board.getPieceAt(rookPosition);
        if (rook == null || !rook.getClass().getSimpleName().equals("Rook") || 
            !rook.getColor().equals(getColor()) || rook.hasMoved()) {
            return false;
        }
        
        // Check if path between king and rook is clear
        int[] rookCoords = board.convertPositionToCoordinates(rookPosition);
        if (rookCoords == null) {
            return false;
        }
        
        int startCol = Math.min(fromCoords[1], rookCoords[1]);
        int endCol = Math.max(fromCoords[1], rookCoords[1]);
        
        for (int col = startCol + 1; col < endCol; col++) {
            String checkPosition = board.convertCoordinatesToPosition(fromCoords[0], col);
            if (checkPosition == null || board.getPieceAt(checkPosition) != null) {
                return false; // Path is blocked or invalid position
            }
        }
        
        return true; // Basic validation passed
    }
}
