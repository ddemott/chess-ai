package com.ddemott.chessai;

import com.ddemott.chessai.pieces.Pawn;

/**
 * Simple demonstration of en passant rule
 */
public class SimpleEnPassantDemo {
    
    public static void main(String[] args) {
        System.out.println("=== Simple En Passant Demonstration ===");
        
        Board board = new Board();
        
        // Clear some initial pieces
        board.setPieceAt("e2", null);
        board.setPieceAt("f7", null);
        
        // Set up the scenario: White pawn at e5, Black pawn at f7
        board.setPieceAt("e5", new Pawn("White", "e5"));
        board.setPieceAt("f7", new Pawn("Black", "f7"));
        
        System.out.println("Initial setup:");
        System.out.println("White pawn at e5, Black pawn at f7");
        printSimpleBoard(board);
        
        System.out.println("\nStep 1: Black pawn moves two squares f7 -> f5");
        board.movePiece("f7", "f5");
        System.out.println("En passant target set to: " + board.getEnPassantTarget());
        printSimpleBoard(board);
        
        System.out.println("\nStep 2: White pawn captures en passant e5 -> f6");
        board.movePiece("e5", "f6");
        System.out.println("Black pawn captured and removed from f5!");
        System.out.println("En passant target cleared: " + (board.getEnPassantTarget() == null ? "Yes" : "No"));
        printSimpleBoard(board);
        
        System.out.println("\n✓ En passant capture completed successfully!");
    }
    
    private static void printSimpleBoard(Board board) {
        System.out.println("  a b c d e f g h");
        for (int row = 7; row >= 0; row--) {
            System.out.print((row + 1) + " ");
            for (int col = 0; col < 8; col++) {
                String position = board.convertCoordinatesToPosition(row, col);
                var piece = board.getPieceAt(position);
                if (piece == null) {
                    System.out.print(". ");
                } else {
                    char symbol = piece.getClass().getSimpleName().charAt(0);
                    if (piece.getColor().equals("White")) {
                        symbol = Character.toUpperCase(symbol);
                    } else {
                        symbol = Character.toLowerCase(symbol);
                    }
                    System.out.print(symbol + " ");
                }
            }
            System.out.println((row + 1));
        }
        System.out.println("  a b c d e f g h\n");
    }
}
