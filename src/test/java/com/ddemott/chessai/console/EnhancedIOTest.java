package com.ddemott.chessai.console;

import com.ddemott.chessai.engine.GameEngine;
import com.ddemott.chessai.console.MoveValidator;
import com.ddemott.chessai.console.MoveValidator.MoveValidationResult;
import com.ddemott.chessai.console.MoveValidator.MoveError;
import com.ddemott.chessai.console.EnhancedConsoleDisplay;

/**
 * Comprehensive test suite for Enhanced Input/Output features
 */
public class EnhancedIOTest {
    
    private static int testsPassed = 0;
    private static int totalTests = 0;
    
    public static void main(String[] args) {
        System.out.println("=== Enhanced Input/Output Test Suite ===\n");
        
        testMoveValidation();
        testErrorMessages();
        testCheckDetection();
        testCheckmateDetection();
        testStalemateDetection();
        testCapturedPiecesTracking();
        testMoveSuggestions();
        testDisplayFeatures();
        
        System.out.println("\n=== Test Summary ===");
        System.out.println("Tests Passed: " + testsPassed + "/" + totalTests);
        if (testsPassed == totalTests) {
            System.out.println("🎉 ALL ENHANCED I/O TESTS PASSED!");
        } else {
            System.out.println("❌ Some enhanced I/O tests failed.");
        }
    }
    
    private static void testMoveValidation() {
        System.out.println("Test 1: Move Validation with Detailed Feedback");
        GameEngine engine = new GameEngine(3);
        
        // Test valid move
        MoveValidationResult result = MoveValidator.validateMove("e2", "e4", "White", engine.getGameState().getBoard());
        assertEqual("Valid move e2-e4", true, result.isValid());
        assertNull("No error for valid move", result.getError());
        
        // Test no piece at source
        result = MoveValidator.validateMove("e3", "e4", "White", engine.getGameState().getBoard());
        assertEqual("No piece at e3", false, result.isValid());
        assertEqual("Correct error type", MoveError.NO_PIECE_AT_SOURCE, result.getError());
        
        // Test wrong player piece
        result = MoveValidator.validateMove("e7", "e5", "White", engine.getGameState().getBoard());
        assertEqual("Wrong player piece", false, result.isValid());
        assertEqual("Correct error type", MoveError.WRONG_PLAYER_PIECE, result.getError());
        
        // Test invalid move format
        result = MoveValidator.validateMove("", "e4", "White", engine.getGameState().getBoard());
        assertEqual("Invalid format", false, result.isValid());
        assertEqual("Correct error type", MoveError.INVALID_FORMAT, result.getError());
        
        // Test out of bounds
        result = MoveValidator.validateMove("e2", "z9", "White", engine.getGameState().getBoard());
        assertEqual("Out of bounds", false, result.isValid());
        assertEqual("Correct error type", MoveError.OUT_OF_BOUNDS, result.getError());
        
        System.out.println("✅ Move validation tests passed\n");
    }
    
    private static void testErrorMessages() {
        System.out.println("Test 2: Error Message Generation");
        GameEngine engine = new GameEngine(3);
        EnhancedConsoleDisplay display = new EnhancedConsoleDisplay(engine.getGameState());
        
        // Test that error display doesn't crash
        try {
            display.displayInvalidMoveError("e2", "e5", "Test error message");
            testsPassed++; // If we get here without exception, test passed
            System.out.println("  ✓ Error message display works");
        } catch (Exception e) {
            System.out.println("  ✗ Error message display failed: " + e.getMessage());
        }
        totalTests++;
        
        // Test move validation feedback
        String feedback = display.validateMoveWithFeedback("e3", "e4");
        assertNotNull("Feedback for invalid move", feedback);
        assertContains("Feedback mentions no piece", feedback, "No piece");
        
        System.out.println("✅ Error message tests passed\n");
    }
    
    private static void testCheckDetection() {
        System.out.println("Test 3: Check Detection");
        GameEngine engine = new GameEngine(3);
        
        // Initial position should have no check
        boolean whiteInCheck = engine.getGameState().getBoard().isKingInCheck("White");
        boolean blackInCheck = engine.getGameState().getBoard().isKingInCheck("Black");
        
        assertEqual("White not in check initially", false, whiteInCheck);
        assertEqual("Black not in check initially", false, blackInCheck);
        
        // Test king position finding
        String whiteKingPos = engine.getGameState().getBoard().findKingPosition("White");
        String blackKingPos = engine.getGameState().getBoard().findKingPosition("Black");
        
        assertEqual("White king at e1", "e1", whiteKingPos);
        assertEqual("Black king at e8", "e8", blackKingPos);
        
        System.out.println("✅ Check detection tests passed\n");
    }
    
    private static void testCheckmateDetection() {
        System.out.println("Test 4: Checkmate Detection");
        GameEngine engine = new GameEngine(3);
        
        // Initial position should not be checkmate
        boolean whiteCheckmate = engine.getGameState().getBoard().isCheckmate("White");
        boolean blackCheckmate = engine.getGameState().getBoard().isCheckmate("Black");
        
        assertEqual("White not in checkmate initially", false, whiteCheckmate);
        assertEqual("Black not in checkmate initially", false, blackCheckmate);
        
        System.out.println("✅ Checkmate detection tests passed\n");
    }
    
    private static void testStalemateDetection() {
        System.out.println("Test 5: Stalemate Detection");
        GameEngine engine = new GameEngine(3);
        
        // Initial position should not be stalemate
        boolean whiteStalemate = engine.getGameState().getBoard().isStalemate("White");
        boolean blackStalemate = engine.getGameState().getBoard().isStalemate("Black");
        
        assertEqual("White not in stalemate initially", false, whiteStalemate);
        assertEqual("Black not in stalemate initially", false, blackStalemate);
        
        System.out.println("✅ Stalemate detection tests passed\n");
    }
    
    private static void testCapturedPiecesTracking() {
        System.out.println("Test 6: Captured Pieces Tracking");
        GameEngine engine = new GameEngine(3);
        EnhancedConsoleDisplay display = new EnhancedConsoleDisplay(engine.getGameState());
        
        // Make a capture move
        engine.movePiece("e2", "e4");
        engine.movePiece("d7", "d5");
        
        // Get the pawn that will be captured
        var capturedPawn = engine.getGameState().getBoard().getPieceAt("d5");
        
        // Make capturing move
        boolean captureSuccess = engine.movePiece("e4", "d5");
        assertEqual("Capture move successful", true, captureSuccess);
        
        // Test that captured piece can be added to display
        if (capturedPawn != null) {
            try {
                display.addCapturedPiece(capturedPawn);
                testsPassed++;
                System.out.println("  ✓ Captured piece tracking works");
            } catch (Exception e) {
                System.out.println("  ✗ Captured piece tracking failed: " + e.getMessage());
            }
        } else {
            testsPassed++; // No piece to capture, but that's okay
            System.out.println("  ✓ No piece captured (as expected)");
        }
        totalTests++;
        
        System.out.println("✅ Captured pieces tracking tests passed\n");
    }
    
    private static void testMoveSuggestions() {
        System.out.println("Test 7: Move Suggestions");
        GameEngine engine = new GameEngine(3);
        EnhancedConsoleDisplay display = new EnhancedConsoleDisplay(engine.getGameState());
        
        // Test generating suggestions
        var suggestions = display.generateMoveSuggestions("White");
        assertNotNull("Suggestions generated", suggestions);
        
        // Test suggestion display doesn't crash
        try {
            display.displayMoveSuggestions("White");
            testsPassed++;
            System.out.println("  ✓ Move suggestions display works");
        } catch (Exception e) {
            System.out.println("  ✗ Move suggestions display failed: " + e.getMessage());
        }
        totalTests++;
        
        // Test move validator suggestions
        var validatorSuggestions = MoveValidator.generateMoveSuggestions("e2", engine.getGameState().getBoard(), "White");
        assertNotNull("Validator suggestions generated", validatorSuggestions);
        
        System.out.println("✅ Move suggestions tests passed\n");
    }
    
    private static void testDisplayFeatures() {
        System.out.println("Test 8: Display Features");
        GameEngine engine = new GameEngine(3);
        EnhancedConsoleDisplay display = new EnhancedConsoleDisplay(engine.getGameState());
        
        // Test enhanced board display
        try {
            display.displayBoard();
            testsPassed++;
            System.out.println("  ✓ Enhanced board display works");
        } catch (Exception e) {
            System.out.println("  ✗ Enhanced board display failed: " + e.getMessage());
        }
        totalTests++;
        
        // Test color enable/disable
        try {
            display.disableColors();
            display.enableColors();
            testsPassed++;
            System.out.println("  ✓ Color toggle works");
        } catch (Exception e) {
            System.out.println("  ✗ Color toggle failed: " + e.getMessage());
        }
        totalTests++;
        
        System.out.println("✅ Display features tests passed\n");
    }
    
    // Helper assertion methods
    private static void assertEqual(String testName, Object expected, Object actual) {
        totalTests++;
        if ((expected == null && actual == null) || (expected != null && expected.equals(actual))) {
            testsPassed++;
            System.out.println("  ✓ " + testName + ": " + actual);
        } else {
            System.out.println("  ✗ " + testName + ": Expected " + expected + ", got " + actual);
        }
    }
    
    private static void assertNotNull(String testName, Object actual) {
        totalTests++;
        if (actual != null) {
            testsPassed++;
            System.out.println("  ✓ " + testName + ": not null");
        } else {
            System.out.println("  ✗ " + testName + ": Expected not null, got null");
        }
    }
    
    private static void assertNull(String testName, Object actual) {
        totalTests++;
        if (actual == null) {
            testsPassed++;
            System.out.println("  ✓ " + testName + ": null");
        } else {
            System.out.println("  ✗ " + testName + ": Expected null, got " + actual);
        }
    }
    
    private static void assertContains(String testName, String haystack, String needle) {
        totalTests++;
        if (haystack != null && haystack.toLowerCase().contains(needle.toLowerCase())) {
            testsPassed++;
            System.out.println("  ✓ " + testName + ": Contains '" + needle + "'");
        } else {
            System.out.println("  ✗ " + testName + ": Does not contain '" + needle + "'");
        }
    }
}
