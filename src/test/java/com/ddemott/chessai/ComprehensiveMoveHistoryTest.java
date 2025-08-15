package com.ddemott.chessai;

import com.ddemott.chessai.engine.GameEngine;

/**
 * Comprehensive test suite for Move History and Undo/Redo functionality
 */
public class ComprehensiveMoveHistoryTest {
    
    private static int testsPassed = 0;
    private static int totalTests = 0;
    
    public static void main(String[] args) {
        System.out.println("=== Comprehensive Move History & Undo/Redo Test Suite ===\n");
        
        testInitialState();
        testMoveRecording();
        testAlgebraicNotation();
        testUndoRedoSequence();
        testUndoRedoBoardState();
        testPGNExport();
        testEdgeCases();
        testMultipleUndoRedo();
        testUndoRedoLimits();
        
        System.out.println("\n=== Test Summary ===");
        System.out.println("Tests Passed: " + testsPassed + "/" + totalTests);
        if (testsPassed == totalTests) {
            System.out.println("🎉 ALL TESTS PASSED!");
        } else {
            System.out.println("❌ Some tests failed.");
        }
    }
    
    private static void testInitialState() {
        System.out.println("Test 1: Initial State");
        GameEngine engine = new GameEngine(3);
        
        assertEqual("Initial move count", 0, engine.getMoveHistory().getMoveCount());
        assertEqual("Initial undo capability", false, engine.canUndo());
        assertEqual("Initial redo capability", false, engine.canRedo());
        assertEqual("Initial current turn", "White", engine.getCurrentTurn());
        assertNull("Initial last move", engine.getLastMove());
        
        System.out.println("✅ Initial state tests passed\n");
    }
    
    private static void testMoveRecording() {
        System.out.println("Test 2: Move Recording");
        GameEngine engine = new GameEngine(3);
        
        // Make a move
        boolean moveSuccess = engine.movePiece("e2", "e4");
        assertEqual("First move success", true, moveSuccess);
        assertEqual("Move count after first move", 1, engine.getMoveHistory().getMoveCount());
        assertEqual("Turn after first move", "Black", engine.getCurrentTurn());
        
        Move lastMove = engine.getLastMove();
        assertNotNull("Last move exists", lastMove);
        assertEqual("Last move from", "e2", lastMove.getFrom());
        assertEqual("Last move to", "e4", lastMove.getTo());
        assertEqual("Last move player", "White", lastMove.getPlayerColor());
        assertEqual("Last move notation", "e4", lastMove.getAlgebraicNotation());
        
        // Make AI move
        engine.makeAIMove();
        assertEqual("Move count after AI move", 2, engine.getMoveHistory().getMoveCount());
        assertEqual("Turn after AI move", "White", engine.getCurrentTurn());
        
        lastMove = engine.getLastMove();
        assertEqual("AI move player", "Black", lastMove.getPlayerColor());
        
        System.out.println("✅ Move recording tests passed\n");
    }
    
    private static void testAlgebraicNotation() {
        System.out.println("Test 3: Algebraic Notation");
        GameEngine engine = new GameEngine(3);
        
        // Test pawn move
        engine.movePiece("e2", "e4");
        Move move1 = engine.getLastMove();
        assertEqual("Pawn move notation", "e4", move1.getAlgebraicNotation());
        
        engine.makeAIMove(); // AI move
        
        // Test knight move
        engine.movePiece("g1", "f3");
        Move move2 = engine.getLastMove();
        assertEqual("Knight move notation", "Nf3", move2.getAlgebraicNotation());
        
        System.out.println("✅ Algebraic notation tests passed\n");
    }
    
    private static void testUndoRedoSequence() {
        System.out.println("Test 4: Undo/Redo Sequence");
        GameEngine engine = new GameEngine(3);
        
        // Make some moves
        engine.movePiece("e2", "e4");
        engine.makeAIMove();
        engine.movePiece("g1", "f3");
        engine.makeAIMove();
        
        assertEqual("Initial move count", 4, engine.getMoveHistory().getMoveCount());
        assertEqual("Can undo initially", true, engine.canUndo());
        assertEqual("Cannot redo initially", false, engine.canRedo());
        
        // Test single undo
        boolean undoSuccess = engine.undoLastMove();
        assertEqual("Undo success", true, undoSuccess);
        assertEqual("Move count after undo", 4, engine.getMoveHistory().getMoveCount());
        assertEqual("Can redo after undo", true, engine.canRedo());
        
        // Test redo
        boolean redoSuccess = engine.redoLastMove();
        assertEqual("Redo success", true, redoSuccess);
        assertEqual("Move count after redo", 4, engine.getMoveHistory().getMoveCount());
        assertEqual("Cannot redo after redo", false, engine.canRedo());
        
        System.out.println("✅ Undo/Redo sequence tests passed\n");
    }
    
    private static void testUndoRedoBoardState() {
        System.out.println("Test 5: Undo/Redo Board State Consistency");
        GameEngine engine = new GameEngine(3);
        
        // Record initial board state
        String initialBoard = engine.getBoardRepresentation();
        
        // Make some moves
        engine.movePiece("e2", "e4");
        String afterFirstMove = engine.getBoardRepresentation();
        assertNotEqual("Board changed after move", initialBoard, afterFirstMove);
        
        engine.makeAIMove();
        engine.movePiece("g1", "f3");
        engine.makeAIMove();
        
    engine.getBoardRepresentation();
        
        // Undo all moves
        engine.undoLastMove(); // Undo AI move
        engine.undoLastMove(); // Undo player move
        engine.undoLastMove(); // Undo AI move
        engine.undoLastMove(); // Undo player move
        
        String afterUndoAll = engine.getBoardRepresentation();
        assertEqual("Board state restored after undo all", initialBoard, afterUndoAll);
        assertEqual("Turn restored after undo all", "White", engine.getCurrentTurn());
        
        System.out.println("✅ Board state consistency tests passed\n");
    }
    
    private static void testPGNExport() {
        System.out.println("Test 6: PGN Export");
        GameEngine engine = new GameEngine(3);
        
        // Make some moves
        engine.movePiece("e2", "e4");
        engine.makeAIMove();
        engine.movePiece("g1", "f3");
        engine.makeAIMove();
        
        String pgn = engine.exportGameToPGN("TestPlayer", "ChessAI", "*");
        
        assertContains("PGN contains headers", pgn, "[Event \"ChessAI Game\"]");
        assertContains("PGN contains white player", pgn, "[White \"TestPlayer\"]");
        assertContains("PGN contains black player", pgn, "[Black \"ChessAI\"]");
        assertContains("PGN contains first move", pgn, "1. e4");
        assertContains("PGN contains knight move", pgn, "Nf3");
        
        System.out.println("✅ PGN export tests passed\n");
    }
    
    private static void testEdgeCases() {
        System.out.println("Test 7: Edge Cases");
        GameEngine engine = new GameEngine(3);
        
        // Test undo when no moves made
        boolean undoEmpty = engine.undoLastMove();
        assertEqual("Undo with no moves", false, undoEmpty);
        
        // Test redo when no moves undone
        boolean redoEmpty = engine.redoLastMove();
        assertEqual("Redo with no undone moves", false, redoEmpty);
        
        // Make one move and test limits
        engine.movePiece("e2", "e4");
        engine.undoLastMove();
        
        // Try to undo again
        boolean undoAgain = engine.undoLastMove();
        assertEqual("Undo beyond limit", false, undoAgain);
        
        System.out.println("✅ Edge case tests passed\n");
    }
    
    private static void testMultipleUndoRedo() {
        System.out.println("Test 8: Multiple Undo/Redo Operations");
        GameEngine engine = new GameEngine(3);
        
        // Make 6 moves (3 each player)
        engine.movePiece("e2", "e4");
        engine.makeAIMove();
        engine.movePiece("g1", "f3");
        engine.makeAIMove();
        engine.movePiece("f1", "c4");
        engine.makeAIMove();
        
        int initialMoveCount = engine.getMoveHistory().getMoveCount();
        assertEqual("Initial move count", 6, initialMoveCount);
        
        // Undo 3 moves
        engine.undoLastMove();
        engine.undoLastMove();
        engine.undoLastMove();
        
        assertEqual("Move count after 3 undos", 6, engine.getMoveHistory().getMoveCount());
        assertEqual("Can redo after undos", true, engine.canRedo());
        
        // Redo 2 moves
        engine.redoLastMove();
        engine.redoLastMove();
        
        assertEqual("Move count after 2 redos", 6, engine.getMoveHistory().getMoveCount());
        assertEqual("Can still redo", true, engine.canRedo());
        
        System.out.println("✅ Multiple undo/redo tests passed\n");
    }
    
    private static void testUndoRedoLimits() {
        System.out.println("Test 9: Undo/Redo Limits and Boundaries");
        GameEngine engine = new GameEngine(3);
        
        // Make some moves
        engine.movePiece("e2", "e4");
        engine.makeAIMove();
        
        // Undo all moves
        while (engine.canUndo()) {
            engine.undoLastMove();
        }
        
        assertEqual("Cannot undo when at start", false, engine.canUndo());
        
        // Redo all moves
        while (engine.canRedo()) {
            engine.redoLastMove();
        }
        
        assertEqual("Cannot redo when at end", false, engine.canRedo());
        
        // Make a new move after redo (should clear redo history)
        engine.movePiece("g1", "f3");
        assertEqual("Cannot redo after new move", false, engine.canRedo());
        
        System.out.println("✅ Undo/Redo limits tests passed\n");
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
    
    private static void assertNotEqual(String testName, Object expected, Object actual) {
        totalTests++;
        if ((expected == null && actual != null) || (expected != null && !expected.equals(actual))) {
            testsPassed++;
            System.out.println("  ✓ " + testName + ": Values are different");
        } else {
            System.out.println("  ✗ " + testName + ": Expected different values, but both were " + actual);
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
    
    private static void assertNotNull(String testName, Object actual) {
        totalTests++;
        if (actual != null) {
            testsPassed++;
            System.out.println("  ✓ " + testName + ": not null");
        } else {
            System.out.println("  ✗ " + testName + ": Expected not null, got null");
        }
    }
    
    private static void assertContains(String testName, String haystack, String needle) {
        totalTests++;
        if (haystack != null && haystack.contains(needle)) {
            testsPassed++;
            System.out.println("  ✓ " + testName + ": Contains '" + needle + "'");
        } else {
            System.out.println("  ✗ " + testName + ": Does not contain '" + needle + "'");
        }
    }
}
