package com.ddemott.chessai;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.ddemott.chessai.pieces.Bishop;
import com.ddemott.chessai.pieces.IPiece;
import com.ddemott.chessai.pieces.King;
import com.ddemott.chessai.pieces.Knight;
import com.ddemott.chessai.pieces.Pawn;
import com.ddemott.chessai.pieces.Queen;
import com.ddemott.chessai.pieces.Rook;

public class Board {
    private IPiece[][] board; // 2D array to represent the board

    public Board() {
        board = new IPiece[8][8]; // 8x8 chess board
        initializeBoard();
    }

    private void initializeBoard() {
        // Adding Pawns for both sides
        for (char col = 'a'; col <= 'h'; col++) {
            board[1][col - 'a'] = new Pawn("White", col + "2");
            board[6][col - 'a'] = new Pawn("Black", col + "7");
        }

        // Adding Rooks for both sides
        board[0][0] = new Rook("White", "a1");
        board[0][7] = new Rook("White", "h1");
        board[7][0] = new Rook("Black", "a8");
        board[7][7] = new Rook("Black", "h8");

        // Adding Knights for both sides
        board[0][1] = new Knight("White", "b1");
        board[0][6] = new Knight("White", "g1");
        board[7][1] = new Knight("Black", "b8");
        board[7][6] = new Knight("Black", "g8");

        // Adding Bishops for both sides
        board[0][2] = new Bishop("White", "c1");
        board[0][5] = new Bishop("White", "f1");
        board[7][2] = new Bishop("Black", "c8");
        board[7][5] = new Bishop("Black", "f8");

        // Adding Queens for both sides
        board[0][3] = new Queen("White", "d1");
        board[7][3] = new Queen("Black", "d8");

        // Adding Kings for both sides
        board[0][4] = new King("White", "e1");
        board[7][4] = new King("Black", "e8");
    }

    public IPiece getPieceAt(String position) {
        int[] coords = convertPositionToCoordinates(position);
        if (coords[0] < 0 || coords[0] >= 8 || coords[1] < 0 || coords[1] >= 8) {
            return null; // Out of bounds
        }
        return board[coords[0]][coords[1]];
    }

    public void setPieceAt(String position, IPiece piece) {
        int[] coords = convertPositionToCoordinates(position);
        if (coords[0] < 0 || coords[0] >= 8 || coords[1] < 0 || coords[1] >= 8) {
            return; // Out of bounds
        }
        board[coords[0]][coords[1]] = piece;
    }

    public int[] convertPositionToCoordinates(String position) {
        position = position.toLowerCase(); // Ensure position is in lowercase
        char column = position.charAt(0);
        int row = position.charAt(1) - '1';
        return new int[]{row, column - 'a'};
    }

    public String convertCoordinatesToPosition(int row, int col) {
        if (row < 0 || row >= 8 || col < 0 || col >= 8) {
            return null; // Out of bounds
        }
        char column = (char) ('a' + col);
        char rowChar = (char) ('1' + row);
        return String.valueOf(column) + rowChar;
    }

    public boolean movePiece(String from, String to) {
        int[] fromCoords = convertPositionToCoordinates(from);
        int[] toCoords = convertPositionToCoordinates(to);
        IPiece piece = getPieceAt(from);

        if (piece == null) {
            return false;
        }

        if (piece.isValidMove(to, this)) {
            setPieceAt(to, piece);
            setPieceAt(from, null);
            piece.setPosition(to);
            return true;
        } else {
            return false;
        }
    }

    public List<String> getAllPossibleMoves(String color) {
        List<String> possibleMoves = new ArrayList<>();
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                IPiece piece = board[row][col];
                if (piece != null && piece.getColor().equals(color)) {
                    List<String> moves = piece.getAllPossibleMoves(this);
                    if (moves != null && !moves.isEmpty()) {
                        possibleMoves.addAll(moves);
                    }
                }
            }
        }
        return possibleMoves;
    }

    public IPiece[][] getBoardArray() {
        return board;
    }

    @Override
    public Board clone() {
        Board newBoard = new Board();
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (this.board[row][col] != null) {
                    newBoard.board[row][col] = this.board[row][col].clonePiece();
                }
            }
        }
        return newBoard;
    }

    // Updating the printBoard method to use standard chess notation
    public void printBoard() {
        System.out.println("  a b c d e f g h");
        for (int row = 7; row >= 0; row--) {
            System.out.print((row + 1) + " ");
            for (int col = 0; col < 8; col++) {
                IPiece piece = board[row][col];
                if (piece == null) {
                    System.out.print(". ");
                } else {
                    String pieceSymbol = getPieceSymbol(piece);
                    System.out.print(pieceSymbol + " ");
                }
            }
            System.out.println((row + 1));
        }
        System.out.println("  a b c d e f g h");
    }

    private String getPieceSymbol(IPiece piece) {
        char symbol;
        if (piece instanceof Pawn) {
            symbol = 'P';
        } else if (piece instanceof Knight) {
            symbol = 'N';
        } else if (piece instanceof Bishop) {
            symbol = 'B';
        } else if (piece instanceof Rook) {
            symbol = 'R';
        } else if (piece instanceof Queen) {
            symbol = 'Q';
        } else if (piece instanceof King) {
            symbol = 'K';
        } else {
            symbol = '?'; // Unknown piece type
        }
        return piece.getColor().equals("White") ? String.valueOf(symbol) : String.valueOf(Character.toLowerCase(symbol));
    }
}
