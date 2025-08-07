package com.ddemott.chessai;

import com.ddemott.chessai.ai.AIDifficulty;
import com.ddemott.chessai.console.AIvsAIChessGame;
import com.ddemott.chessai.engine.GameEngine;

/**
 * Test suite for AI vs AI functionality
 */
public class AIvsAITest {
    
    public static void main(String[] args) {
        System.out.println("=== AI vs AI Test Suite ===\n");
        
        int totalTests = 0;
        int passedTests = 0;
        
        // Test 1: AI vs AI game creation
        totalTests++;
        if (testAIvsAIGameCreation()) {
            passedTests++;
            System.out.println("✅ Test 1: AI vs AI Game Creation - PASSED");
        } else {
            System.out.println("❌ Test 1: AI vs AI Game Creation - FAILED");
        }
        
        // Test 2: Different AI difficulties can compete
        totalTests++;
        if (testDifferentDifficultiesCompete()) {
            passedTests++;
            System.out.println("✅ Test 2: Different Difficulties Compete - PASSED");
        } else {
            System.out.println("❌ Test 2: Different Difficulties Compete - FAILED");
        }
        
        // Test 3: Game progression and move alternation
        totalTests++;
        if (testGameProgression()) {
            passedTests++;
            System.out.println("✅ Test 3: Game Progression - PASSED");
        } else {
            System.out.println("❌ Test 3: Game Progression - FAILED");
        }
        
        // Test 4: Game state management during AI vs AI
        totalTests++;
        if (testGameStateManagement()) {
            passedTests++;
            System.out.println("✅ Test 4: Game State Management - PASSED");
        } else {
            System.out.println("❌ Test 4: Game State Management - FAILED");
        }
        
        // Test 5: AI switching during game
        totalTests++;
        if (testAISwitching()) {
            passedTests++;
            System.out.println("✅ Test 5: AI Switching - PASSED");
        } else {
            System.out.println("❌ Test 5: AI Switching - FAILED");
        }
        
        System.out.println("\\n=== Test Summary ===");
        System.out.println("Tests Passed: " + passedTests + "/" + totalTests);
        if (passedTests == totalTests) {
            System.out.println("🎉 ALL AI vs AI TESTS PASSED!");
        } else {
            System.out.println("⚠️  Some tests failed - check implementation");
        }
        
        // Run a quick demo game
        runQuickDemo();
    }
    
    private static boolean testAIvsAIGameCreation() {
        try {
            AIvsAIChessGame game = new AIvsAIChessGame(AIDifficulty.BEGINNER, AIDifficulty.ADVANCED);
            return game != null;
        } catch (Exception e) {
            System.out.println("Exception in AI vs AI creation test: " + e.getMessage());
            return false;
        }
    }
    
    private static boolean testDifferentDifficultiesCompete() {
        try {
            // Test all combinations of difficulties
            AIDifficulty[] difficulties = {AIDifficulty.BEGINNER, AIDifficulty.INTERMEDIATE, AIDifficulty.EXPERT};
            
            for (AIDifficulty white : difficulties) {
                for (AIDifficulty black : difficulties) {
                    GameEngine engine = new GameEngine(white, black);
                    if (engine == null) {
                        System.out.println("Failed to create engine with " + white + " vs " + black);
                        return false;
                    }
                }
            }
            
            return true;
        } catch (Exception e) {
            System.out.println("Exception in different difficulties test: " + e.getMessage());
            return false;
        }
    }
    
    private static boolean testGameProgression() {
        try {
            GameEngine engine = new GameEngine(AIDifficulty.BEGINNER, AIDifficulty.EASY);
            
            // Simulate several AI moves alternating between white and black
            int movesPlayed = 0;
            int maxMoves = 10; // Limit for testing
            
            while (movesPlayed < maxMoves) {
                String currentPlayer = engine.getCurrentTurn();
                
                // Set appropriate difficulty for current player
                if (currentPlayer.equals("White")) {
                    engine.setAIDifficulty(AIDifficulty.BEGINNER);
                } else {
                    engine.setAIDifficulty(AIDifficulty.EASY);
                }
                
                // Get AI move
                String aiMove = engine.getBestMove();
                if (aiMove == null) {
                    break; // Game ended or no moves available
                }
                
                // Execute the move
                String[] positions = aiMove.split(" ");
                if (positions.length != 2) {
                    System.out.println("Invalid AI move format: " + aiMove);
                    return false;
                }
                
                boolean moveSuccess = engine.movePiece(positions[0], positions[1]);
                if (!moveSuccess) {
                    System.out.println("AI move failed: " + aiMove);
                    return false;
                }
                
                movesPlayed++;
                
                // Check that turn alternated
                String newPlayer = engine.getCurrentTurn();
                if (newPlayer.equals(currentPlayer)) {
                    System.out.println("Turn did not alternate properly");
                    return false;
                }
            }
            
            return movesPlayed > 0; // At least some moves were played
        } catch (Exception e) {
            System.out.println("Exception in game progression test: " + e.getMessage());
            return false;
        }
    }
    
    private static boolean testGameStateManagement() {
        try {
            GameEngine engine = new GameEngine(AIDifficulty.INTERMEDIATE, AIDifficulty.ADVANCED);
            
            // Initial state
            String initialPlayer = engine.getCurrentTurn();
            int initialMoveCount = engine.getMoveHistory().getMoves().size();
            
            // Make one AI move
            engine.makeAIMove();
            
            // Check state changed appropriately
            String newPlayer = engine.getCurrentTurn();
            int newMoveCount = engine.getMoveHistory().getMoves().size();
            
            return !newPlayer.equals(initialPlayer) &&
                   newMoveCount == initialMoveCount + 1;
        } catch (Exception e) {
            System.out.println("Exception in game state test: " + e.getMessage());
            return false;
        }
    }
    
    private static boolean testAISwitching() {
        try {
            GameEngine engine = new GameEngine(AIDifficulty.BEGINNER);
            
            // Start with beginner
            AIDifficulty initial = engine.getAIDifficulty();
            if (initial != AIDifficulty.BEGINNER) {
                return false;
            }
            
            // Switch to expert
            engine.setAIDifficulty(AIDifficulty.EXPERT);
            AIDifficulty changed = engine.getAIDifficulty();
            if (changed != AIDifficulty.EXPERT) {
                return false;
            }
            
            // Verify AI can still make moves after switch
            String aiMove = engine.getBestMove();
            return aiMove != null && !aiMove.isEmpty();
        } catch (Exception e) {
            System.out.println("Exception in AI switching test: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Run a quick demo of AI vs AI gameplay
     */
    private static void runQuickDemo() {
        System.out.println("\\n=== Quick AI vs AI Demo ===");
        System.out.println("Running Beginner vs Intermediate for 6 moves...");
        
        try {
            GameEngine engine = new GameEngine(AIDifficulty.BEGINNER, AIDifficulty.INTERMEDIATE);
            
            for (int moveCount = 0; moveCount < 6; moveCount++) {
                String currentPlayer = engine.getCurrentTurn();
                
                // Set appropriate difficulty
                if (currentPlayer.equals("White")) {
                    engine.setAIDifficulty(AIDifficulty.BEGINNER);
                    System.out.print("Beginner (White): ");
                } else {
                    engine.setAIDifficulty(AIDifficulty.INTERMEDIATE);
                    System.out.print("Intermediate (Black): ");
                }
                
                // Make AI move
                String aiMove = engine.getBestMove();
                if (aiMove == null) {
                    System.out.println("No move available");
                    break;
                }
                
                String[] positions = aiMove.split(" ");
                engine.movePiece(positions[0], positions[1]);
                
                // Get the actual move played for display
                if (engine.getLastMove() != null) {
                    System.out.println(engine.getLastMove().getAlgebraicNotation() + 
                                     " (" + aiMove + ")");
                } else {
                    System.out.println(aiMove);
                }
            }
            
            System.out.println("\\nFinal position:");
            System.out.println(engine.getBoardRepresentation());
            System.out.println("Demo completed successfully!");
            
        } catch (Exception e) {
            System.out.println("Exception in demo: " + e.getMessage());
        }
    }
}
