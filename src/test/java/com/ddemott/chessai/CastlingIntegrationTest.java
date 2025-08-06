package com.ddemott.chessai;

import com.ddemott.chessai.engine.GameEngine;

/**
 * Integration test showing castling in a real game scenario
 */
public class CastlingIntegrationTest {
    
    public static void main(String[] args) {
        System.out.println("=== Castling Integration Test ===");
        
        // Create a game engine
        GameEngine engine = new GameEngine(3);
        
        System.out.println("Initial board:");
        System.out.println(engine.getBoardRepresentation());
        
        // Clear pieces to allow castling
        System.out.println("Clearing pieces for castling...");
        
        // Make moves to clear pieces between king and rook
        engine.movePiece("g1", "f3"); // Move knight out of the way
        engine.movePiece("a7", "a6"); // Black move
        engine.movePiece("f1", "c4"); // Move bishop out of the way 
        engine.movePiece("b7", "b6"); // Black move
        
        System.out.println("After clearing pieces:");
        System.out.println(engine.getBoardRepresentation());
        
        // Try castling
        System.out.println("Attempting kingside castling (e1 to g1)...");
        boolean castlingSuccessful = engine.movePiece("e1", "g1");
        
        System.out.println("Castling successful: " + castlingSuccessful);
        
        if (castlingSuccessful) {
            System.out.println("Board after castling:");
            System.out.println(engine.getBoardRepresentation());
        }
        
        // Verify final positions
        System.out.println("=== Verification ===");
        String boardState = engine.getBoardRepresentation();
        boolean kingAtG1 = boardState.contains("K") && boardState.indexOf("K") > boardState.indexOf("f");
        boolean rookAtF1 = boardState.contains("R") && boardState.indexOf("R") < boardState.indexOf("g");
        
        System.out.println("King moved to g1: " + kingAtG1);
        System.out.println("Rook moved to f1: " + rookAtF1);
        
        if (castlingSuccessful && kingAtG1 && rookAtF1) {
            System.out.println("✓ CASTLING INTEGRATION TEST PASSED!");
        } else {
            System.out.println("✗ Castling integration test failed");
        }
    }
}
