package com.ddemott.chessai;

import com.ddemott.chessai.pieces.Pawn;
import com.ddemott.chessai.pieces.Queen;

/**
 * Unit tests for the Move class
 */
public class MoveClassTest {
    
    private static int testsPassed = 0;
    private static int totalTests = 0;
    
    public static void main(String[] args) {
        System.out.println("=== Move Class Unit Tests ===\n");
        
        testMoveCreation();
        testMoveGetters();
        testMoveCapture();
        testMoveToString();
        testCreateSimpleMove();
        
        System.out.println("\n=== Move Class Test Summary ===");
        System.out.println("Tests Passed: " + testsPassed + "/" + totalTests);
        if (testsPassed == totalTests) {
            System.out.println("🎉 ALL MOVE CLASS TESTS PASSED!");
        } else {
            System.out.println("❌ Some Move class tests failed.");
        }
    }
    
    private static void testMoveCreation() {
        System.out.println("Test 1: Move Creation");
        
        Pawn pawn = new Pawn("White", "e2");
        Move move = new Move("e2", "e4", pawn, null, "e4", 1, "White", 
                           false, false, false, false, null);
        
        assertNotNull("Move created", move);
        assertEqual("Move from position", "e2", move.getFrom());
        assertEqual("Move to position", "e4", move.getTo());
        assertEqual("Moving piece", pawn, move.getMovingPiece());
        assertEqual("No captured piece", null, move.getCapturedPiece());
        assertEqual("Algebraic notation", "e4", move.getAlgebraicNotation());
        assertEqual("Move number", 1, move.getMoveNumber());
        assertEqual("Player color", "White", move.getPlayerColor());
        
        System.out.println("✅ Move creation tests passed\n");
    }
    
    private static void testMoveGetters() {
        System.out.println("Test 2: Move Getters");
        
        Pawn pawn = new Pawn("White", "e2");
        Queen queen = new Queen("Black", "d8");
        
        Move move = new Move("e2", "d3", pawn, queen, "exd3", 5, "White", 
                           true, false, false, false, null);
        
        assertEqual("isCheck getter", true, move.isCheck());
        assertEqual("isCheckmate getter", false, move.isCheckmate());
        assertEqual("isCastle getter", false, move.isCastle());
        assertEqual("isEnPassant getter", false, move.isEnPassant());
        assertEqual("getPromotionPiece getter", null, move.getPromotionPiece());
        
        System.out.println("✅ Move getters tests passed\n");
    }
    
    private static void testMoveCapture() {
        System.out.println("Test 3: Move Capture Detection");
        
        Pawn pawn = new Pawn("White", "e2");
        Queen queen = new Queen("Black", "d8");
        
        // Test normal capture
        Move captureMove = new Move("e2", "d3", pawn, queen, "exd3", 1, "White", 
                                  false, false, false, false, null);
        assertEqual("Normal capture detection", true, captureMove.isCapture());
        
        // Test en passant
        Move enPassantMove = new Move("e5", "d6", pawn, null, "exd6", 1, "White", 
                                    false, false, false, true, null);
        assertEqual("En passant capture detection", true, enPassantMove.isCapture());
        
        // Test non-capture
        Move nonCaptureMove = new Move("e2", "e4", pawn, null, "e4", 1, "White", 
                                     false, false, false, false, null);
        assertEqual("Non-capture detection", false, nonCaptureMove.isCapture());
        
        System.out.println("✅ Move capture detection tests passed\n");
    }
    
    private static void testMoveToString() {
        System.out.println("Test 4: Move toString Method");
        
        Pawn pawn = new Pawn("White", "e2");
        Move move = new Move("e2", "e4", pawn, null, "e4", 1, "White", 
                           false, false, false, false, null);
        
        assertEqual("toString returns algebraic notation", "e4", move.toString());
        
        System.out.println("✅ Move toString tests passed\n");
    }
    
    private static void testCreateSimpleMove() {
        System.out.println("Test 5: Create Simple Move Factory Method");
        
        Pawn pawn = new Pawn("White", "e2");
        Queen capturedQueen = new Queen("Black", "d4");
        
        Move simpleMove = Move.createSimpleMove("e2", "d4", pawn, capturedQueen, 3, "White");
        
        assertNotNull("Simple move created", simpleMove);
        assertEqual("Simple move from", "e2", simpleMove.getFrom());
        assertEqual("Simple move to", "d4", simpleMove.getTo());
        assertEqual("Simple move piece", pawn, simpleMove.getMovingPiece());
        assertEqual("Simple move captured", capturedQueen, simpleMove.getCapturedPiece());
        assertEqual("Simple move number", 3, simpleMove.getMoveNumber());
        assertEqual("Simple move color", "White", simpleMove.getPlayerColor());
        assertEqual("Simple move no check", false, simpleMove.isCheck());
        assertEqual("Simple move no checkmate", false, simpleMove.isCheckmate());
        assertEqual("Simple move no castle", false, simpleMove.isCastle());
        assertEqual("Simple move no en passant", false, simpleMove.isEnPassant());
        assertEqual("Simple move no promotion", null, simpleMove.getPromotionPiece());
        assertEqual("Simple move empty notation", "", simpleMove.getAlgebraicNotation());
        
        System.out.println("✅ Create simple move tests passed\n");
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
}
