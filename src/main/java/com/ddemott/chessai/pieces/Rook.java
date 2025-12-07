package com.ddemott.chessai.pieces;

import java.util.ArrayList;
import java.util.List;
import com.ddemott.chessai.Board;
import com.ddemott.chessai.GameConstants;
import com.ddemott.chessai.Side;

public class Rook extends Piece {

    public Rook(Side side, String position) {
        super(side, position);
    }
    
    // Legacy constructor
    public Rook(String color, String position) {
        super(color, position);
    }

    @Override
    public boolean isValidMove(String newPosition, Board board) {
        String currentPosition = getPosition();
        int[] currentCoords = board.convertPositionToCoordinates(currentPosition);
        int[] newCoords = board.convertPositionToCoordinates(newPosition);

        return isValidRookMove(currentCoords, newCoords, board);
    }

    private boolean isValidRookMove(int[] currentCoords, int[] newCoords, Board board) {
        if (!isStraightMove(currentCoords, newCoords)) {
            return false; // Rooks move only in straight lines
        }
        return isPathClear(currentCoords, newCoords, board) && isDestinationValid(newCoords, board);
    }

    private boolean isStraightMove(int[] currentCoords, int[] newCoords) {
        // Added null check to prevent NullPointerException on out-of-bounds moves
        if(newCoords == null) {
            return false;
        }
        return currentCoords[0] == newCoords[0] || currentCoords[1] == newCoords[1];
    }

    private boolean isPathClear(int[] currentCoords, int[] newCoords, Board board) {
        int rowStep = Integer.signum(newCoords[0] - currentCoords[0]);
        int colStep = Integer.signum(newCoords[1] - currentCoords[1]);

        int currentRow = currentCoords[0] + rowStep;
        int currentCol = currentCoords[1] + colStep;
        while (currentRow != newCoords[0] || currentCol != newCoords[1]) {
            if (board.getPieceAt(board.convertCoordinatesToPosition(currentRow, currentCol)) != null) {
                return false; // Path is blocked
            }
            currentRow += rowStep;
            currentCol += colStep;
        }
        return true; // Path is clear
    }

    private boolean isDestinationValid(int[] newCoords, Board board) {
        IPiece pieceAtDestination = board.getPieceAt(board.convertCoordinatesToPosition(newCoords[0], newCoords[1]));
        return pieceAtDestination == null || pieceAtDestination.getSide() != side;
    }

    @Override
    public int getValue() {
        return GameConstants.ROOK_VALUE;
    }

    @Override
    public IPiece clonePiece() {
        Rook cloned = new Rook(side, position);
        cloned.setHasMoved(this.hasMoved());
        return cloned;
    }

    @Override
    public List<String> getAllPossibleMoves(Board board) {
        List<String> possibleMoves = new ArrayList<>();
        String currentPosition = getPosition();
        int[] currentCoords = board.convertPositionToCoordinates(currentPosition);

        int[][] directions = {
            {1, 0}, {-1, 0}, {0, 1}, {0, -1}
        };

        for (int[] direction : directions) {
            int row = currentCoords[0];
            int col = currentCoords[1];
            while (true) {
                row += direction[0];
                col += direction[1];
                if (row < 0 || row >= 8 || col < 0 || col >= 8) break;
                String newPosition = board.convertCoordinatesToPosition(row, col);
                if (isValidMove(newPosition, board)) {
                    possibleMoves.add(currentPosition + " " + newPosition);
                    // Stop if we captured a piece
                    if (board.getPieceAt(newPosition) != null) break;
                } else {
                    // Stop if path is blocked or invalid
                    break;
                }
            }
        }

        return possibleMoves;
    }
}