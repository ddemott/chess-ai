package com.ddemott.chessai;

import com.ddemott.chessai.ai.AIDifficulty;
import com.ddemott.chessai.console.AIvsAIChessGame;
import com.ddemott.chessai.engine.GameEngine;
import com.ddemott.chessai.MoveHistory;

/**
 * Interactive demo showcasing new PGN loading and AI difficulty features
 */
public class NewFeaturesDemo {
    
    public static void main(String[] args) {
        System.out.println("=== ChessAI New Features Demo ===\n");
        
        System.out.println("This demo showcases the new features:");
        System.out.println("1. PGN Loading Functionality");
        System.out.println("2. AI Difficulty Levels");
        System.out.println("3. AI vs AI Game Mode");
        System.out.println();
        
        // Demo 1: AI Difficulty Levels
        demoDifficultyLevels();
        
        // Demo 2: PGN Operations
        demoPGNOperations();
        
        // Demo 3: AI vs AI Gameplay
        demoAIvsAI();
        
        System.out.println("=== Demo Complete ===");
        System.out.println("New features are ready for use!");
    }
    
    private static void demoDifficultyLevels() {
        System.out.println("\\n=== Demo 1: AI Difficulty Levels ===");
        
        // Show all available difficulties
        System.out.println(AIDifficulty.getAllDifficulties());
        
        // Create engines with different difficulties
        System.out.println("Creating game engines with different AI difficulties...");
        
        AIDifficulty[] testDifficulties = {
            AIDifficulty.BEGINNER, 
            AIDifficulty.INTERMEDIATE, 
            AIDifficulty.EXPERT
        };
        
        for (AIDifficulty difficulty : testDifficulties) {
            System.out.println("\\n" + difficulty);
            
            GameEngine engine = new GameEngine(difficulty);
            System.out.println("  Engine created with depth: " + difficulty.getDepth());
            
            // Test AI move generation
            engine.movePiece("e2", "e4");
            long startTime = System.currentTimeMillis();
            String aiMove = engine.getBestMove();
            long endTime = System.currentTimeMillis();
            
            System.out.println("  AI Move: " + aiMove);
            System.out.println("  Think Time: " + (endTime - startTime) + "ms");
        }
        
        // Demo difficulty switching
        System.out.println("\\nDemo: Dynamic difficulty switching...");
        GameEngine engine = new GameEngine(AIDifficulty.BEGINNER);
        System.out.println("Started with: " + engine.getAIDifficulty());
        
        engine.setAIDifficulty(AIDifficulty.MASTER);
        System.out.println("Changed to: " + engine.getAIDifficulty());
    }
    
    private static void demoPGNOperations() {
        System.out.println("\\n\\n=== Demo 2: PGN Loading and Saving ===");
        
        // Create a sample game
        System.out.println("Creating a sample game...");
        GameEngine engine = new GameEngine(AIDifficulty.INTERMEDIATE);
        
        // Play a few moves
        String[][] moves = {
            {"e2", "e4"}, {"e7", "e5"},
            {"g1", "f3"}, {"b8", "c6"},
            {"f1", "b5"}, {"a7", "a6"},
            {"b5", "a4"}, {"g8", "f6"}
        };
        
        for (String[] move : moves) {
            engine.movePiece(move[0], move[1]);
        }
        
        System.out.println("Game moves played:");
        System.out.println(engine.getMoveListDisplay());
        
        // Save to PGN
        String filename = "demo_game.pgn";
        System.out.println("\\nSaving game to PGN file: " + filename);
        boolean saved = engine.saveGameToPGNFile(filename, "Demo_White", "Demo_Black", "*");
        System.out.println("Save result: " + (saved ? "SUCCESS" : "FAILED"));
        
        if (saved) {
            // Show PGN content
            String pgnContent = engine.exportGameToPGN("Demo_White", "Demo_Black", "*");
            System.out.println("\\nPGN Content:");
            System.out.println(pgnContent);
            
            // Load the game in a new engine
            System.out.println("\\nLoading game in new engine...");
            GameEngine newEngine = new GameEngine(AIDifficulty.BEGINNER);
            boolean loaded = newEngine.loadGameFromPGNFile(filename);
            System.out.println("Load result: " + (loaded ? "SUCCESS" : "FAILED"));
            
            if (loaded) {
                System.out.println("Loaded game moves:");
                System.out.println(newEngine.getMoveListDisplay());
                
                // Verify move count matches
                int originalMoves = engine.getMoveHistory().getMoves().size();
                int loadedMoves = newEngine.getMoveHistory().getMoves().size();
                System.out.println("Move count verification: " + originalMoves + " vs " + loadedMoves + 
                                 (originalMoves == loadedMoves ? " ✅" : " ❌"));
            }
        }
        
        // Demo PGN parsing
        System.out.println("\\nDemo: PGN Parsing...");
        String samplePGN = """
            [Event "Demo Game"]
            [Site "Local"]
            [Date "2025.08.07"]
            [White "Alice"]
            [Black "Bob"]
            [Result "1-0"]
            
            1. e4 e5 2. Nf3 Nc6 3. Bb5 a6 1-0
            """;
        
        MoveHistory.PGNGameData gameData = MoveHistory.parsePGN(samplePGN);
        System.out.println("Parsed PGN: " + gameData);
        System.out.println("Moves found: " + gameData.moves);
    }
    
    private static void demoAIvsAI() {
        System.out.println("\\n\\n=== Demo 3: AI vs AI Gameplay ===");
        
        System.out.println("Setting up AI vs AI match: Beginner vs Advanced");
        
        try {
            GameEngine engine = new GameEngine(AIDifficulty.BEGINNER, AIDifficulty.ADVANCED);
            
            System.out.println("\\nPlaying 8 moves (4 per side)...");
            
            for (int moveCount = 0; moveCount < 8; moveCount++) {
                String currentPlayer = engine.getCurrentTurn();
                
                // Set appropriate difficulty for current player
                AIDifficulty currentDifficulty;
                if (currentPlayer.equals("White")) {
                    currentDifficulty = AIDifficulty.BEGINNER;
                    engine.setAIDifficulty(currentDifficulty);
                    System.out.print("Move " + (moveCount + 1) + " - Beginner (White): ");
                } else {
                    currentDifficulty = AIDifficulty.ADVANCED;
                    engine.setAIDifficulty(currentDifficulty);
                    System.out.print("Move " + (moveCount + 1) + " - Advanced (Black): ");
                }
                
                // Time the AI thinking
                long startTime = System.currentTimeMillis();
                String aiMove = engine.getBestMove();
                long thinkTime = System.currentTimeMillis() - startTime;
                
                if (aiMove == null) {
                    System.out.println("No move available");
                    break;
                }
                
                // Execute the move
                String[] positions = aiMove.split(" ");
                engine.movePiece(positions[0], positions[1]);
                
                // Display the move
                if (engine.getLastMove() != null) {
                    System.out.println(engine.getLastMove().getAlgebraicNotation() + 
                                     " (" + aiMove + ") [" + thinkTime + "ms]");
                } else {
                    System.out.println(aiMove + " [" + thinkTime + "ms]");
                }
                
                // Small delay for readability
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
            
            // Show final position
            System.out.println("\\nFinal board position:");
            System.out.println(engine.getBoardRepresentation());
            
            // Show game statistics
            System.out.println("\\nGame Statistics:");
            System.out.println("Total moves: " + engine.getMoveHistory().getMoves().size());
            System.out.println("Current turn: " + engine.getCurrentTurn());
            
            // Save the AI vs AI game
            System.out.println("\\nSaving AI vs AI game...");
            boolean saved = engine.saveGameToPGNFile("ai_vs_ai_demo.pgn", 
                "AI_Beginner", "AI_Advanced", "*");
            System.out.println("Save result: " + (saved ? "SUCCESS" : "FAILED"));
            
        } catch (Exception e) {
            System.out.println("Error in AI vs AI demo: " + e.getMessage());
        }
    }
    
    /**
     * Run all test suites to verify functionality
     */
    public static void runAllTests() {
        System.out.println("\\n=== Running All Tests ===");
        
        System.out.println("\\n1. PGN Loading Tests:");
        PGNLoadingTest.main(new String[0]);
        
        System.out.println("\\n2. AI Difficulty Tests:");
        AIDifficultyTest.main(new String[0]);
        
        System.out.println("\\n3. AI vs AI Tests:");
        AIvsAITest.main(new String[0]);
    }
}
