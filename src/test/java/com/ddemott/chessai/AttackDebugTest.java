package com.ddemott.chessai;

import com.ddemott.chessai.pieces.*;

/**
 * Debug attack detection
 */
public class AttackDebugTest {
    
    public static void main(String[] args) {
        System.out.println("=== Attack Detection Debug ===");
        
        // Test direct rook attack from e8 to e1
        Board board = new Board();
        
        // Remove pieces that might be in the way
        board.setPieceAt("e2", null); // Remove pawn
        board.setPieceAt("e7", null); // Remove pawn
        
        // Place black rook at e8
        Rook blackRook = new Rook("Black", "e8");
        board.setPieceAt("e8", blackRook);
        
        System.out.println("Testing rook attack from e8 to e1:");
        System.out.println("Rook at e8: " + board.getPieceAt("e8"));
        System.out.println("King at e1: " + board.getPieceAt("e1"));
        System.out.println("Piece at e2: " + board.getPieceAt("e2"));
        System.out.println("Piece at e7: " + board.getPieceAt("e7"));
        
        // Test if rook can attack e1
        boolean rookCanAttackE1 = blackRook.isValidMove("e1", board);
        System.out.println("Can rook attack e1? " + rookCanAttackE1);
        
        // Test general attack detection
        boolean e1UnderAttack = board.isSquareUnderAttack("e1", "White");
        System.out.println("Is e1 under attack (general)? " + e1UnderAttack);
        
        // Test all the squares along e-file
        for (int i = 1; i <= 8; i++) {
            String square = "e" + i;
            IPiece piece = board.getPieceAt(square);
            if (piece != null) {
                System.out.println("Piece at " + square + ": " + piece.getClass().getSimpleName() + " (" + piece.getColor() + ")");
            } else {
                System.out.println("Piece at " + square + ": empty");
            }
        }
        
        System.out.println("\n=== Test Rook f8 to f1 ===");
        Board board2 = new Board();
        board2.setPieceAt("f1", null); // Remove bishop
        board2.setPieceAt("f2", null); // Remove pawn  
        board2.setPieceAt("f7", null); // Remove pawn
        
        Rook blackRook2 = new Rook("Black", "f8");
        board2.setPieceAt("f8", blackRook2);
        
        boolean rookCanAttackF1 = blackRook2.isValidMove("f1", board2);
        System.out.println("Can rook attack f1? " + rookCanAttackF1);
        
        boolean f1UnderAttack = board2.isSquareUnderAttack("f1", "White");
        System.out.println("Is f1 under attack (general)? " + f1UnderAttack);
    }
}
