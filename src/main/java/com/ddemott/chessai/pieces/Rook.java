package com.ddemott.chessai.pieces;

import java.util.ArrayList;
import java.util.List;
import com.ddemott.chessai.Board;

public class Rook extends Piece {

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
        return pieceAtDestination == null || !pieceAtDestination.getColor().equals(getColor());
    }

    @Override
    public int getValue() {
        return 5; // Standard chess value for a rook
    }

    @Override
    public IPiece clonePiece() {
        return new Rook(getColor(), getPosition());
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
                if (isPathClear(currentCoords, board.convertPositionToCoordinates(newPosition), board)) {
                    possibleMoves.add(currentPosition + " " + newPosition);
                    if (board.getPieceAt(newPosition) != null) break;
                } else {
                    break;
                }
            }
        }

        return possibleMoves;
    }
}
