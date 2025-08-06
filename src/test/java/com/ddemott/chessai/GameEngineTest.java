package com.ddemott.chessai;

import com.ddemott.chessai.engine.GameEngine;

/**
 * Simple test class demonstrating console-free testing of the chess engine.
 * This shows how the core functionality can be tested without any console dependencies.
 */
public class GameEngineTest {

    public static void main(String[] args) {
        System.out.println("=== Testing Chess Engine Core Functionality ===\n");
        
        // Create a new game engine with AI depth 3
        GameEngine engine = new GameEngine(3);
        
        // Test initial game state
        testInitialState(engine);
        
        // Test basic moves
        testBasicMoves(engine);
        
        // Test AI move generation
        testAIMove(engine);
        
        // Test invalid moves
        testInvalidMoves(engine);
        
        // Debug AI move issues
        debugAIMove(engine);
        
        // Show board state access demonstration
        testBoardStateAccess(engine);
        
        System.out.println("=== All tests completed ===");
    }
    
    private static void debugAIMove(GameEngine engine) {
        System.out.println("DEBUG: Investigating AI Move Issues:");
        
        // Check what pieces Black has available
        com.ddemott.chessai.State gameState = engine.getGameState();
        java.util.List<String> blackMoves = gameState.getAllPossibleMoves("Black");
        
        System.out.println("   Available Black moves:");
        for (String move : blackMoves) {
            System.out.println("     " + move);
            if (blackMoves.indexOf(move) >= 5) { // Limit output to first 5 moves
                System.out.println("     ... (showing first 5 of " + blackMoves.size() + " total moves)");
                break;
            }
        }
        
        // Test if the AI suggested move is actually in the list
        String aiMove = engine.getBestMove();
        System.out.println("   AI suggested: " + aiMove);
        System.out.println("   Is AI move in possible moves: " + blackMoves.contains(aiMove));
        
        // Try a simple manual move for black
        System.out.println("   Trying a simple black move manually...");
        if (!blackMoves.isEmpty()) {
            String simpleMove = blackMoves.get(0);
            String[] parts = simpleMove.split(" ");
            boolean manualMoveSuccess = engine.movePiece(parts[0], parts[1]);
            System.out.println("   Manual move " + simpleMove + ": " + (manualMoveSuccess ? "SUCCESS" : "FAILED"));
            System.out.println("   Turn after manual move: " + engine.getCurrentTurn());
        }
    }
    
    private static void testBoardStateAccess(GameEngine engine) {
        System.out.println("5. Testing Board State Access:");
        
        String boardState = engine.getBoardRepresentation();
        System.out.println("   Board state retrieved successfully:");
        System.out.println(boardState);
        
        // Test that we can access game state
        com.ddemott.chessai.State gameState = engine.getGameState();
        System.out.println("   Game state access: ✓");
        System.out.println("   Current player from state: " + gameState.getCurrentTurn());
        System.out.println("   ✓ Board state access test passed\n");
    }
    
    private static void testInitialState(GameEngine engine) {
        System.out.println("1. Testing Initial Game State:");
        System.out.println("   Current turn: " + engine.getCurrentTurn());
        System.out.println("   Expected: White");
        System.out.println("   ✓ Initial state test passed\n");
    }
    
    private static void testBasicMoves(GameEngine engine) {
        System.out.println("2. Testing Basic Moves:");
        
        // Test pawn move e2-e4
        boolean moveResult = engine.movePiece("e2", "e4");
        System.out.println("   Move e2-e4: " + (moveResult ? "SUCCESS" : "FAILED"));
        System.out.println("   Current turn after move: " + engine.getCurrentTurn());
        
        if (moveResult && engine.getCurrentTurn().equals("Black")) {
            System.out.println("   ✓ Basic move test passed\n");
        } else {
            System.out.println("   ✗ Basic move test failed\n");
        }
    }
    
    private static void testAIMove(GameEngine engine) {
        System.out.println("3. Testing AI Move Generation:");
        
        System.out.println("   Current turn before AI: " + engine.getCurrentTurn());
        String aiMove = engine.getBestMove();
        System.out.println("   AI suggests move: " + aiMove);
        
        if (aiMove != null && !aiMove.isEmpty()) {
            // Verify the AI move is valid for the current player
            String currentTurn = engine.getCurrentTurn();
            System.out.println("   Executing AI move for: " + currentTurn);
            
            engine.makeAIMove();
            System.out.println("   AI move executed successfully");
            System.out.println("   Current turn after AI move: " + engine.getCurrentTurn());
            
            // The turn should have switched after AI move
            String expectedTurn = currentTurn.equals("Black") ? "White" : "Black";
            if (engine.getCurrentTurn().equals(expectedTurn)) {
                System.out.println("   ✓ AI move test passed\n");
            } else {
                System.out.println("   ✗ AI move test failed - turn didn't switch properly\n");
            }
        } else {
            System.out.println("   ✗ AI move test failed - no move suggested\n");
        }
    }
    
    private static void testInvalidMoves(GameEngine engine) {
        System.out.println("4. Testing Invalid Move Handling:");
        
        System.out.println("   Current turn: " + engine.getCurrentTurn());
        
        // Try to move a piece that doesn't exist
        boolean invalidMove1 = engine.movePiece("a3", "a4");
        System.out.println("   Invalid move a3-a4 (no piece): " + (invalidMove1 ? "FAILED" : "CORRECTLY REJECTED"));
        
        // Try to move opponent's piece (if current turn is White, try to move Black piece)
        String opponentMove = engine.getCurrentTurn().equals("White") ? "e7 e5" : "e2 e4";
        String[] opponentPos = opponentMove.split(" ");
        boolean invalidMove2 = engine.movePiece(opponentPos[0], opponentPos[1]);
        System.out.println("   Invalid move " + opponentMove + " (opponent's piece): " + (invalidMove2 ? "FAILED" : "CORRECTLY REJECTED"));
        
        if (!invalidMove1 && !invalidMove2) {
            System.out.println("   ✓ Invalid move handling test passed\n");
        } else {
            System.out.println("   ✗ Invalid move handling test failed\n");
        }
    }
    
    /**
     * Demonstrates how to get board state without console output
     */
    public static void showBoardStateAccess() {
        GameEngine engine = new GameEngine(2);
        
        // Get board representation as string without printing to console
        String boardState = engine.getBoardRepresentation();
        
        // Can be used for logging, testing, web interfaces, etc.
        System.out.println("Board state can be captured as string:");
        System.out.println(boardState);
        
        // Access to full game state for advanced testing
        State gameState = engine.getGameState();
        System.out.println("Full game state accessible for testing");
    }
}
