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

        // Forward move (one square)
        if (colDiff == 0 && rowDiff == direction && board.getPieceAt(newPosition) == null) {
            return true;
        }

        // Forward move (two squares from starting position)
        if (currentCoords[0] == startRow && colDiff == 0 && rowDiff == 2 * direction &&
            board.getPieceAt(board.convertCoordinatesToPosition(currentCoords[0] + direction, currentCoords[1])) == null &&
            board.getPieceAt(newPosition) == null) {
            return true;
        }

        // Diagonal capture (regular)
        if (Math.abs(colDiff) == 1 && rowDiff == direction) {
            IPiece destinationPiece = board.getPieceAt(newPosition);
            if (destinationPiece != null && !destinationPiece.getColor().equals(color)) {
                return true;
            }
            
            // En passant capture
            if (destinationPiece == null && newPosition.equals(board.getEnPassantTarget())) {
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

        // Forward move (one square)
        int row = currentCoords[0] + direction;
        int col = currentCoords[1];
        if (row >= 0 && row < 8 && board.getPieceAt(board.convertCoordinatesToPosition(row, col)) == null) {
            possibleMoves.add(currentPosition + " " + board.convertCoordinatesToPosition(row, col));
            
            // Forward move (two squares from starting position)
            if (currentCoords[0] == startRow) {
                row = currentCoords[0] + 2 * direction;
                if (board.getPieceAt(board.convertCoordinatesToPosition(row, col)) == null) {
                    possibleMoves.add(currentPosition + " " + board.convertCoordinatesToPosition(row, col));
                }
            }
        }

        // Diagonal captures (left and right)
        int[] captureDirections = {-1, 1}; // Left and right diagonals
        for (int captureDir : captureDirections) {
            int captureRow = currentCoords[0] + direction;
            int captureCol = currentCoords[1] + captureDir;
            
            if (captureRow >= 0 && captureRow < 8 && captureCol >= 0 && captureCol < 8) {
                String capturePosition = board.convertCoordinatesToPosition(captureRow, captureCol);
                IPiece piece = board.getPieceAt(capturePosition);
                
                // Regular diagonal capture
                if (piece != null && !piece.getColor().equals(color)) {
                    possibleMoves.add(currentPosition + " " + capturePosition);
                }
                
                // En passant capture
                if (piece == null && capturePosition.equals(board.getEnPassantTarget())) {
                    possibleMoves.add(currentPosition + " " + capturePosition);
                }
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
