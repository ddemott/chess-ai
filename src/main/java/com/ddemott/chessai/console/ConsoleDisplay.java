package com.ddemott.chessai.console;

import com.ddemott.chessai.Board;
import com.ddemott.chessai.State;
import com.ddemott.chessai.pieces.IPiece;

public class ConsoleDisplay {
    private State state;

    public ConsoleDisplay(State state) {
        this.state = state;
    }

    public void printBoard() {
        Board board = state.getBoard();
        System.out.println("  a b c d e f g h");
        for (int row = 7; row >= 0; row--) {
            System.out.print((row + 1) + " ");
            for (int col = 0; col < 8; col++) {
                IPiece piece = board.getPieceAt("" + (char) ('a' + col) + (row + 1));
                if (piece != null) {
                    System.out.print(getPieceSymbol(piece) + " ");
                } else {
                    System.out.print(". ");
                }
            }
            System.out.println(" " + (row + 1));
        }
        System.out.println("  a b c d e f g h");
    }

    private char getPieceSymbol(IPiece piece) {
        char symbol;
        switch (piece.getClass().getSimpleName()) {
            case "King":
                symbol = 'K';
                break;
            case "Queen":
                symbol = 'Q';
                break;
            case "Rook":
                symbol = 'R';
                break;
            case "Bishop":
                symbol = 'B';
                break;
            case "Knight":
                symbol = 'N';
                break;
            case "Pawn":
                symbol = 'P';
                break;
            default:
                symbol = ' ';
                break;
        }
        return piece.getColor().equals("White") ? symbol : Character.toLowerCase(symbol);
    }
}
