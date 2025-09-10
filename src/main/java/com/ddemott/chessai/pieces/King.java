package com.ddemott.chessai.pieces;

import java.util.ArrayList;
import java.util.List;

import com.ddemott.chessai.Board;

public class King extends Piece {

    // Ensure King has a proper moved flag for castling checks
    private boolean moved = false;

    public King(String color, String position) {
        super(color, position);
    }

    @Override
    public boolean isValidMove(String newPosition, Board board) {
        int[] currentCoords = board.convertPositionToCoordinates(this.getPosition());
        int[] newCoords = board.convertPositionToCoordinates(newPosition);
        if (currentCoords == null || newCoords == null) {
            return false;
        }
        // Ensure move is within board boundaries
        if(newCoords[0] < 0 || newCoords[0] > 7 || newCoords[1] < 0 || newCoords[1] > 7) {
            return false;
        }
        int rowDiff = Math.abs(newCoords[0] - currentCoords[0]);
        int colDiff = Math.abs(newCoords[1] - currentCoords[1]);
        // Only allow single-square moves (not castling)
        if ((rowDiff <= 1 && colDiff <= 1) && (rowDiff + colDiff > 0)) {
            // Prevent moving into check
            String destSquare = newPosition;
            try {
                if (board.isSquareUnderAttack(destSquare, this.color)) {
                    return false;
                }
            } catch (NullPointerException e) {
                return false;
            }
            return true;
        }
        // Castling move: move two squares horizontally and no vertical movement, only from starting rank
        if (!this.hasMoved() && rowDiff == 0 && colDiff == 2 && (this.getPosition().equals("e1") || this.getPosition().equals("e8"))) {
            // Convert board coordinates to square notation, e.g., e1
            String kingFile = String.valueOf((char)('a' + currentCoords[1]));
            String kingRank = String.valueOf(currentCoords[0] + 1);
            // Castling is illegal if the king is in check
            try {
                if (board.isSquareUnderAttack(kingFile + kingRank, this.color)) {
                    return false;
                }
            } catch (NullPointerException e) {
                return false;
            }
            if (newCoords[1] > currentCoords[1]) { // Kingside castling
                // Compute squares between king and rook
                String square1File = String.valueOf((char)('a' + currentCoords[1] + 1));
                String square2File = String.valueOf((char)('a' + currentCoords[1] + 2));
                String rankStr = kingRank;
                String square1Pos = square1File + rankStr;
                String square2Pos = square2File + rankStr;
                // Check that the squares are empty
                if (board.getPieceAt(square1Pos) != null || board.getPieceAt(square2Pos) != null) {
                    return false;
                }
                // Replace castling validation to inline try-catch for isSquareUnderAttack:
                boolean kingsideSafe;
                try {
                    kingsideSafe = !board.isSquareUnderAttack("f" + rankStr, this.color) && !board.isSquareUnderAttack("g" + rankStr, this.color);
                } catch (NullPointerException e) {
                    kingsideSafe = false;
                }
                if (!kingsideSafe) {
                    return false;
                }
            } else { // Queenside castling
                String square1File = String.valueOf((char)('a' + currentCoords[1] - 1));
                String square2File = String.valueOf((char)('a' + currentCoords[1] - 2));
                String square3File = String.valueOf((char)('a' + currentCoords[1] - 3));
                String rankStr = kingRank;
                String square1Pos = square1File + rankStr;
                String square2Pos = square2File + rankStr;
                String square3Pos = square3File + rankStr;
                // Check that the squares between king and rook are empty
                if (board.getPieceAt(square1Pos) != null || board.getPieceAt(square2Pos) != null || board.getPieceAt(square3Pos) != null) {
                    return false;
                }
                // In castling validation, replace direct calls to board.isSquareUnderAttack with safeUnderAttack to avoid issues
                boolean queensideSafe;
                try {
                    queensideSafe = !board.isSquareUnderAttack("d" + rankStr, this.color) && !board.isSquareUnderAttack("c" + rankStr, this.color);
                } catch (NullPointerException e) {
                    queensideSafe = false;
                }
                if (!queensideSafe) {
                    return false;
                }
            }
            return true;
        }
        
        return false;
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
                String newMove = board.convertCoordinatesToPosition(row, col);
                IPiece pieceAtDestination = board.getPieceAt(newMove);
                if (pieceAtDestination == null || !pieceAtDestination.getColor().equals(getColor())) {
                    possibleMoves.add(currentPosition + " " + newMove);
                }
            }
        }

        return possibleMoves;
    }

    @Override
    public boolean hasMoved() {
        return moved;
    }

    @Override
    
    public void setHasMoved(boolean moved) {
        this.moved = moved;
    }
}
