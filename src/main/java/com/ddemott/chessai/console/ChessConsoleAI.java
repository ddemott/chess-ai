package com.ddemott.chessai.console;

import com.ddemott.chessai.Board;
import com.ddemott.chessai.ai.ChessAI;
import com.ddemott.chessai.pieces.IPiece;
import java.util.Scanner;

public class ChessConsoleAI {
    private Board board;
    private ChessAI ai;
    private String aiColor;
    private Scanner scanner;

    public ChessConsoleAI(Board board, ChessAI ai, String aiColor) {
        this.board = board;
        this.ai = ai;
        this.aiColor = aiColor;
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
            if (currentPlayer.equals(aiColor)) {
                // AI move
                String[] move = ai.getBestMove(board, aiColor);
                if (move == null) {
                    System.out.println("AI has no valid moves. Game over.");
                    break;
                }
                System.out.println("AI moves: " + move[0] + " " + move[1]);
                board.movePiece(move[0], move[1]);
            } else {
                // Human move
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
                IPiece piece = board.getPieceAt(from);
                if (piece == null || !piece.getColor().equals(currentPlayer)) {
                    System.out.println("No valid piece at " + from + " for " + currentPlayer + ".");
                    continue;
                }
                if (!board.movePiece(from, to)) {
                    System.out.println("Invalid move. Try again.");
                    continue;
                }
            }
            // Switch player
            currentPlayer = currentPlayer.equals("White") ? "Black" : "White";
        }
    }
}
