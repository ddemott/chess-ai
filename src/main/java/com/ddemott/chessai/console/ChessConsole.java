package com.ddemott.chessai.console;

import com.ddemott.chessai.Board;
import com.ddemott.chessai.pieces.Piece;
import java.util.Scanner;

public class ChessConsole {
    private Board board;
    private Scanner scanner;

    public ChessConsole(Board board) {
        this.board = board;
        this.scanner = new Scanner(System.in);
    }

    public void startGame() {
        String currentPlayer = "White";
        while (true) {
            System.out.println("\nCurrent board:");
            System.out.println(board.getBoardRepresentation());
            if (board.isCheckmate(currentPlayer)) {
                System.out.println(currentPlayer + " is in checkmate! Game over.");
                break;
            }
            if (board.isStalemate(currentPlayer)) {
                System.out.println(currentPlayer + " is in stalemate! Game over.");
                break;
            }
            if (board.isKingInCheck(currentPlayer)) {
                System.out.println(currentPlayer + " is in check!");
            }
            System.out.println(currentPlayer + "'s turn.");
            System.out.print("Enter move (e.g., e2 e4): ");
            String input = scanner.nextLine().trim();
            if (input.equalsIgnoreCase("quit")) {
                System.out.println("Game ended by user.");
                break;
            }
            String[] parts = input.split(" ");
            if (parts.length != 2) {
                System.out.println("Invalid input. Please enter moves in the format 'from to'.");
                continue;
            }
            String from = parts[0];
            String to = parts[1];
            com.ddemott.chessai.pieces.IPiece piece = board.getPieceAt(from);
            if (piece == null || !piece.getColor().equals(currentPlayer)) {
                System.out.println("No valid piece at " + from + " for " + currentPlayer + ".");
                continue;
            }
            if (!board.movePiece(from, to)) {
                System.out.println("Invalid move. Try again.");
                continue;
            }
            // Switch player
            currentPlayer = currentPlayer.equals("White") ? "Black" : "White";
        }
    }
}
