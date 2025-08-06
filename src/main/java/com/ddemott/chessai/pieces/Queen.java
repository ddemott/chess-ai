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

        boolean isRookMove = currentCoords[0] == newCoords[0] || currentCoords[1] == newCoords[1];
        boolean isBishopMove = Math.abs(newCoords[0] - currentCoords[0]) == Math.abs(newCoords[1] - currentCoords[1]);

        if (isRookMove || isBishopMove) {
            return isPathClear(currentCoords, newCoords, board) && isDestinationValid(newCoords, board);
        }

        return false;
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
        return new Queen(color, position);
    }
}
