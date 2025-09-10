package com.ddemott.chessai.pieces;

import com.ddemott.chessai.Board;

import java.util.ArrayList;
import java.util.List;

public class Queen extends Piece {

    public Queen(String color, String position) {
        super(color, position);
    }

    @Override
    public boolean isValidMove(String newPosition, Board board) {
        String currentPosition = getPosition();
        int[] currentCoords = board.convertPositionToCoordinates(currentPosition);
        int[] newCoords = board.convertPositionToCoordinates(newPosition);
        if (currentCoords == null || newCoords == null) {
            return false;
        }
        // Valid rook move (horizontal/vertical)
        boolean isRookMove = currentCoords[0] == newCoords[0] || currentCoords[1] == newCoords[1];
        // Valid bishop move (diagonal)
        boolean isBishopMove = Math.abs(newCoords[0] - currentCoords[0]) == Math.abs(newCoords[1] - currentCoords[1]);
        // Invalid L-shape (knight move)
        int rowDiff = Math.abs(newCoords[0] - currentCoords[0]);
        int colDiff = Math.abs(newCoords[1] - currentCoords[1]);
        if ((rowDiff == 2 && colDiff == 1) || (rowDiff == 1 && colDiff == 2)) {
            return false;
        }
        if (isRookMove || isBishopMove) {
            // Path must be clear except for destination
            if (!board.isPathClear(currentPosition, newPosition)) {
                return false;
            }
            // Destination must be empty or contain opponent's piece
            IPiece destPiece = board.getPieceAt(newPosition);
            if (destPiece == null || !destPiece.getColor().equals(getColor())) {
                return true;
            }
            return false;
        }
        return false;
    }

    // Removed local isPathClear; now uses Board.isPathClear

    private boolean isDestinationValid(int[] newCoords, Board board) {
        IPiece pieceAtDestination = board.getPieceAt(board.convertCoordinatesToPosition(newCoords[0], newCoords[1]));
        return pieceAtDestination == null || !pieceAtDestination.getColor().equals(getColor());
    }

    @Override
    public List<String> getAllPossibleMoves(Board board) {
        List<String> possibleMoves = new ArrayList<>();
        String currentPosition = getPosition();
        int[] currentCoords = board.convertPositionToCoordinates(currentPosition);

        int[][] directions = {
            {1, 0}, {-1, 0}, {0, 1}, {0, -1},
            {1, 1}, {1, -1}, {-1, 1}, {-1, -1}
        };

        for (int[] direction : directions) {
            int row = currentCoords[0];
            int col = currentCoords[1];
            while (true) {
                row += direction[0];
                col += direction[1];
                if (row >= 0 && row < 8 && col >= 0 && col < 8) {
                    String newPosition = board.convertCoordinatesToPosition(row, col);
                    if (isValidMove(newPosition, board)) {
                        possibleMoves.add(currentPosition + " " + newPosition);
                    } else {
                        break;
                    }
                } else {
                    break;
                }
            }
        }

        return possibleMoves;
    }

    @Override
    public int getValue() {
        return 9;
    }

    @Override
    public IPiece clonePiece() {
        Queen cloned = new Queen(color, position);
        cloned.setHasMoved(this.hasMoved());
        return cloned;
    }
}
