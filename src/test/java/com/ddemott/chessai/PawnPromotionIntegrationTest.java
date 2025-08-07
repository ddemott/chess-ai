package com.ddemott.chessai;

import com.ddemott.chessai.engine.GameEngine;

/**
 * Integration test to verify pawn promotion works with the full game engine
 */
public class PawnPromotionIntegrationTest {
    
    public static void main(String[] args) {
        System.out.println("=== Pawn Promotion Integration Test ===");
        
        GameEngine engine = new GameEngine(3);
        
        // Set up a scenario where White pawn can promote
        engine.getGameState().getBoard().clearBoard();
        
        // Place White pawn on 7th rank
        engine.getGameState().getBoard().setPieceAt("e7", 
            new com.ddemott.chessai.pieces.Pawn("White", "e7"));
        
        // Test promotion move through GameEngine
        System.out.println("Testing pawn promotion through GameEngine...");
        boolean success = engine.movePiece("e7", "e8", "Q");
        System.out.println("Promotion successful: " + success);
        
        // Verify the promoted piece
        var piece = engine.getGameState().getBoard().getPieceAt("e8");
        System.out.println("Piece at e8: " + (piece != null ? piece.getClass().getSimpleName() : "null"));
        System.out.println("Piece color: " + (piece != null ? piece.getColor() : "N/A"));
        
        // Test move history notation
        var lastMove = engine.getLastMove();
        if (lastMove != null) {
            System.out.println("Move notation: " + lastMove.getAlgebraicNotation());
            System.out.println("Promotion piece: " + lastMove.getPromotionPiece());
        }
        
        System.out.println("✅ Pawn promotion integration test completed!");
    }
}
