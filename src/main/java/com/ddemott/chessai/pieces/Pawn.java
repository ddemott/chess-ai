package com.ddemott.chessai.pieces;

import com.ddemott.chessai.Board;

import java.util.ArrayList;
import java.util.List;

public class Pawn extends Piece {

    public Pawn(String color, String position) {
        super(color, position);
    }

        @Override
    public boolean isValidMove(String newPosition, Board board) {
        String currentPosition = getPosition();
        int[] currentCoords = board.convertPositionToCoordinates(currentPosition);
        int[] newCoords = board.convertPositionToCoordinates(newPosition);

        int direction = color.equals("White") ? 1 : -1;
        int startRow = color.equals("White") ? 1 : 6;

        int rowDiff = newCoords[0] - currentCoords[0];
        int colDiff = newCoords[1] - currentCoords[1];

        if (colDiff == 0 && rowDiff == direction && board.getPieceAt(newPosition) == null) {
            return true;
        }

        if (currentCoords[0] == startRow && colDiff == 0 && rowDiff == 2 * direction &&
            board.getPieceAt(board.convertCoordinatesToPosition(currentCoords[0] + direction, currentCoords[1])) == null &&
            board.getPieceAt(newPosition) == null) {
            return true;
        }

        if (Math.abs(colDiff) == 1 && rowDiff == direction) {
            IPiece destinationPiece = board.getPieceAt(newPosition);
            if (destinationPiece != null && !destinationPiece.getColor().equals(color)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public List<String> getAllPossibleMoves(Board board) {
        List<String> possibleMoves = new ArrayList<>();
        String currentPosition = getPosition();
        int[] currentCoords = board.convertPositionToCoordinates(currentPosition);

        int direction = color.equals("White") ? 1 : -1;
        int startRow = color.equals("White") ? 1 : 6;

        int row = currentCoords[0] + direction;
        int col = currentCoords[1];
        if (row >= 0 && row < 8 && board.getPieceAt(board.convertCoordinatesToPosition(row, col)) == null) {
            possibleMoves.add(currentPosition + " " + board.convertCoordinatesToPosition(row, col));
        }

        row = currentCoords[0] + 2 * direction;
        if (currentCoords[0] == startRow && board.getPieceAt(board.convertCoordinatesToPosition(currentCoords[0] + direction, col)) == null &&
            board.getPieceAt(board.convertCoordinatesToPosition(row, col)) == null) {
            possibleMoves.add(currentPosition + " " + board.convertCoordinatesToPosition(row, col));
        }

        col = currentCoords[1] - 1;
        if (row - direction >= 0 && row - direction < 8 && col >= 0 && col < 8) {
            IPiece piece = board.getPieceAt(board.convertCoordinatesToPosition(row - direction, col));
            if (piece != null && !piece.getColor().equals(color)) {
                possibleMoves.add(currentPosition + " " + board.convertCoordinatesToPosition(row - direction, col));
            }
        }

        col = currentCoords[1] + 1;
        if (row - direction >= 0 && row - direction < 8 && col >= 0 && col < 8) {
            IPiece piece = board.getPieceAt(board.convertCoordinatesToPosition(row - direction, col));
            if (piece != null && !piece.getColor().equals(color)) {
                possibleMoves.add(currentPosition + " " + board.convertCoordinatesToPosition(row - direction, col));
            }
        }

        return possibleMoves;
    }

    @Override
    public int getValue() {
        return 1;
    }

    @Override
    public IPiece clonePiece() {
        Pawn cloned = new Pawn(color, position);
        cloned.setHasMoved(this.hasMoved());
        return cloned;
    }
}
