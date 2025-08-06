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
        if (dx > 1 || dy > 1) {
            return false;
        }

        IPiece pieceAtDestination = board.getPieceAt(newPosition);
        if (pieceAtDestination == null || !pieceAtDestination.getColor().equals(getColor())) {
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
        return new King(getColor(), getPosition());
    }

    @Override
    public List<String> getAllPossibleMoves(Board board) {
        List<String> possibleMoves = new ArrayList<>();
        String currentPosition = getPosition();
        int[] currentCoords = board.convertPositionToCoordinates(currentPosition);

        int[][] directions = {
            {1, 0}, {-1, 0}, {0, 1}, {0, -1},
            {1, 1}, {-1, -1}, {1, -1}, {-1, 1}
        };

        for (int[] direction : directions) {
            int row = currentCoords[0] + direction[0];
            int col = currentCoords[1] + direction[1];
            if (row >= 0 && row < 8 && col >= 0 && col < 8) {
                String newPosition = board.convertCoordinatesToPosition(row, col);
                if (isValidMove(newPosition, board)) {
                    possibleMoves.add(currentPosition + " " + newPosition);
                }
            }
        }

        return possibleMoves;
    }
}
