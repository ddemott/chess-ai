package com.ddemott.chessai;

import com.ddemott.chessai.MoveHistory;
import com.ddemott.chessai.engine.GameEngine;
import com.ddemott.chessai.ai.AIDifficulty;

/**
 * Comprehensive test suite for PGN loading functionality
 */
public class PGNLoadingTest {
    
    public static void main(String[] args) {
        System.out.println("=== PGN Loading Test Suite ===\n");
        
        int totalTests = 0;
        int passedTests = 0;
        
        // Test 1: Parse simple PGN headers
        totalTests++;
        if (testPGNHeaderParsing()) {
            passedTests++;
            System.out.println("✅ Test 1: PGN Header Parsing - PASSED");
        } else {
            System.out.println("❌ Test 1: PGN Header Parsing - FAILED");
        }
        
        // Test 2: Parse simple move list
        totalTests++;
        if (testSimpleMoveListParsing()) {
            passedTests++;
            System.out.println("✅ Test 2: Simple Move List Parsing - PASSED");
        } else {
            System.out.println("❌ Test 2: Simple Move List Parsing - FAILED");
        }
        
        // Test 3: Parse castling moves
        totalTests++;
        if (testCastlingMoves()) {
            passedTests++;
            System.out.println("✅ Test 3: Castling Moves - PASSED");
        } else {
            System.out.println("❌ Test 3: Castling Moves - FAILED");
        }
        
        // Test 4: Parse promotion moves
        totalTests++;
        if (testPromotionMoves()) {
            passedTests++;
            System.out.println("✅ Test 4: Promotion Moves - PASSED");
        } else {
            System.out.println("❌ Test 4: Promotion Moves - FAILED");
        }
        
        // Test 5: Test PGN file creation and loading
        totalTests++;
        if (testPGNFileOperations()) {
            passedTests++;
            System.out.println("✅ Test 5: PGN File Operations - PASSED");
        } else {
            System.out.println("❌ Test 5: PGN File Operations - FAILED");
        }
        
        // Test 6: Test error handling
        totalTests++;
        if (testErrorHandling()) {
            passedTests++;
            System.out.println("✅ Test 6: Error Handling - PASSED");
        } else {
            System.out.println("❌ Test 6: Error Handling - FAILED");
        }
        
        System.out.println("\\n=== Test Summary ===");
        System.out.println("Tests Passed: " + passedTests + "/" + totalTests);
        if (passedTests == totalTests) {
            System.out.println("🎉 ALL PGN LOADING TESTS PASSED!");
        } else {
            System.out.println("⚠️  Some tests failed - check implementation");
        }
    }
    
    private static boolean testPGNHeaderParsing() {
        String pgnContent = """
            [Event "Test Game"]
            [Site "Local"]
            [Date "2025.08.07"]
            [Round "1"]
            [White "Alice"]
            [Black "Bob"]
            [Result "1-0"]
            
            1. e4 e5 2. Nf3 Nc6 1-0
            """;
        
        try {
            MoveHistory.PGNGameData gameData = MoveHistory.parsePGN(pgnContent);
            
            return gameData.event.equals("Test Game") &&
                   gameData.site.equals("Local") &&
                   gameData.date.equals("2025.08.07") &&
                   gameData.whitePlayer.equals("Alice") &&
                   gameData.blackPlayer.equals("Bob") &&
                   gameData.result.equals("1-0");
        } catch (Exception e) {
            System.out.println("Exception in header parsing: " + e.getMessage());
            return false;
        }
    }
    
    private static boolean testSimpleMoveListParsing() {
        String pgnContent = """
            [Event "Test Game"]
            [White "Player1"]
            [Black "Player2"]
            [Result "*"]
            
            1. e4 e5 2. Nf3 Nc6 3. Bb5 a6 *
            """;
        
        try {
            MoveHistory.PGNGameData gameData = MoveHistory.parsePGN(pgnContent);
            
            // Should parse: e4, e5, Nf3, Nc6, Bb5, a6
            return gameData.moves.size() == 6 &&
                   gameData.moves.get(0).equals("e4") &&
                   gameData.moves.get(1).equals("e5") &&
                   gameData.moves.get(2).equals("Nf3") &&
                   gameData.moves.get(3).equals("Nc6") &&
                   gameData.moves.get(4).equals("Bb5") &&
                   gameData.moves.get(5).equals("a6");
        } catch (Exception e) {
            System.out.println("Exception in move parsing: " + e.getMessage());
            return false;
        }
    }
    
    private static boolean testCastlingMoves() {
        String pgnContent = """
            [Event "Castling Test"]
            [White "Player1"]
            [Black "Player2"]
            [Result "*"]
            
            1. e4 e5 2. Nf3 Nc6 3. Bc4 Bc5 4. O-O O-O-O *
            """;
        
        try {
            MoveHistory.PGNGameData gameData = MoveHistory.parsePGN(pgnContent);
            
            // Should include castling moves
            return gameData.moves.contains("O-O") &&
                   gameData.moves.contains("O-O-O");
        } catch (Exception e) {
            System.out.println("Exception in castling test: " + e.getMessage());
            return false;
        }
    }
    
    private static boolean testPromotionMoves() {
        String pgnContent = """
            [Event "Promotion Test"]
            [White "Player1"]
            [Black "Player2"]
            [Result "*"]
            
            1. e4 e5 2. f4 d5 3. exd5 e4 4. d6 e3 5. d7 e2 6. d8=Q e1=N *
            """;
        
        try {
            MoveHistory.PGNGameData gameData = MoveHistory.parsePGN(pgnContent);
            
            // Should include promotion moves
            return gameData.moves.contains("d8=Q") &&
                   gameData.moves.contains("e1=N");
        } catch (Exception e) {
            System.out.println("Exception in promotion test: " + e.getMessage());
            return false;
        }
    }
    
    private static boolean testPGNFileOperations() {
        try {
            // Create a simple game and save it
            GameEngine engine = new GameEngine(AIDifficulty.BEGINNER);
            
            // Make a few moves
            engine.movePiece("e2", "e4");
            engine.movePiece("e7", "e5");
            engine.movePiece("g1", "f3");
            engine.movePiece("b8", "c6");
            
            // Save to file
            String filename = "test_game.pgn";
            boolean saved = engine.saveGameToPGNFile(filename, "TestWhite", "TestBlack", "*");
            if (!saved) {
                System.out.println("Failed to save PGN file");
                return false;
            }
            
            // Load from file
            GameEngine newEngine = new GameEngine(AIDifficulty.BEGINNER);
            boolean loaded = newEngine.loadGameFromPGNFile(filename);
            if (!loaded) {
                System.out.println("Failed to load PGN file");
                return false;
            }
            
            // Compare move counts
            int originalMoves = engine.getMoveHistory().getMoves().size();
            int loadedMoves = newEngine.getMoveHistory().getMoves().size();
            
            return originalMoves == loadedMoves;
            
        } catch (Exception e) {
            System.out.println("Exception in file operations test: " + e.getMessage());
            return false;
        }
    }
    
    private static boolean testErrorHandling() {
        try {
            // Test loading non-existent file
            GameEngine engine = new GameEngine(AIDifficulty.BEGINNER);
            boolean result1 = engine.loadGameFromPGNFile("nonexistent_file.pgn");
            if (result1) {
                System.out.println("Should not have loaded non-existent file");
                return false;
            }
            
            // Test parsing invalid PGN
            MoveHistory.PGNGameData result2 = MoveHistory.parsePGN("Invalid PGN content");
            // Should not crash, even with invalid content
            
            return true;
        } catch (Exception e) {
            System.out.println("Exception in error handling test: " + e.getMessage());
            return false;
        }
    }
}
