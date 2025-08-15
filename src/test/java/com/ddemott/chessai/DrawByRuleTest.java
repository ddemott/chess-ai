package com.ddemott.chessai;

import com.ddemott.chessai.engine.GameEngine;
import com.ddemott.chessai.pieces.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for threefold repetition and the fifty-move rule.
 */
public class DrawByRuleTest {

    @Test
    void testThreefoldRepetition() {
        GameEngine engine = new GameEngine(1);
        clearBoard(engine);
        
        // Set up a simple position with just kings and a knight
        engine.getGameState().getBoard().setPieceAt("e1", new King("White", "e1"));
        engine.getGameState().getBoard().setPieceAt("e8", new King("Black", "e8"));
        engine.getGameState().getBoard().setPieceAt("g1", new Knight("White", "g1"));
        
        // Make some moves to trigger threefold repetition
        // Move 1: White knight g1-f3
        engine.getGameState().setCurrentTurn("White");
        assertTrue(engine.movePiece("g1", "f3"), "Knight should move to f3");
        
        // Move 1: Black king e8-d8
        engine.getGameState().setCurrentTurn("Black");
        assertTrue(engine.movePiece("e8", "d8"), "King should move to d8");
        
        // Move 2: White knight f3-g1
        engine.getGameState().setCurrentTurn("White");
        assertTrue(engine.movePiece("f3", "g1"), "Knight should move to g1");
        
        // Move 2: Black king d8-e8
        engine.getGameState().setCurrentTurn("Black");
        assertTrue(engine.movePiece("d8", "e8"), "King should move to e8");
        
        // Move 3: White knight g1-f3
        engine.getGameState().setCurrentTurn("White");
        assertTrue(engine.movePiece("g1", "f3"), "Knight should move to f3");
        
        // Move 3: Black king e8-d8
        engine.getGameState().setCurrentTurn("Black");
        assertTrue(engine.movePiece("e8", "d8"), "King should move to d8");
        
        // Move 4: White knight f3-g1
        engine.getGameState().setCurrentTurn("White");
        assertTrue(engine.movePiece("f3", "g1"), "Knight should move to g1");
        
        // Move 4: Black king d8-e8
        engine.getGameState().setCurrentTurn("Black");
        assertTrue(engine.movePiece("d8", "e8"), "King should move to e8");
        
        // Move 5: White knight g1-f3
        engine.getGameState().setCurrentTurn("White");
        assertTrue(engine.movePiece("g1", "f3"), "Knight should move to f3");
        
        // Now we should have had the same position 3 times
        assertTrue(engine.getGameState().isThreefoldRepetition(), "Should detect threefold repetition");
    }
    
    @Test
    void testFiftyMoveRule() {
        GameEngine engine = new GameEngine(1);
        clearBoard(engine);
        
        // Set up a simple endgame position
        engine.getGameState().getBoard().setPieceAt("e1", new King("White", "e1"));
        engine.getGameState().getBoard().setPieceAt("e8", new King("Black", "e8"));
        engine.getGameState().getBoard().setPieceAt("a1", new Rook("White", "a1"));
        
        // For testing purposes, we'll manually make a series of non-capture, non-pawn moves
        // Each move increases the halfmove clock by 1
        for (int i = 0; i < 49; i++) {
            // Move white king back and forth
            engine.getGameState().setCurrentTurn("White");
            engine.movePiece("e1", "d1");
            engine.getGameState().setCurrentTurn("Black");
            engine.movePiece("e8", "d8");
            
            engine.getGameState().setCurrentTurn("White");
            engine.movePiece("d1", "e1");
            engine.getGameState().setCurrentTurn("Black");
            engine.movePiece("d8", "e8");
        }
        
        // Directly set the halfmove clock value
        // For test purposes, we can use reflection to set the private field
        // This is not ideal but allows us to test the functionality without 100 moves
        try {
            java.lang.reflect.Field halfmoveField = MoveHistory.class.getDeclaredField("halfmoveClock");
            halfmoveField.setAccessible(true);
            halfmoveField.setInt(engine.getMoveHistory(), 99);
        } catch (Exception e) {
            fail("Could not set halfmove clock: " + e.getMessage());
        }
        
        // Check that we're not yet at 50 moves
        assertFalse(engine.getGameState().isFiftyMoveRule(), "Should not be 50-move rule yet");
        
        // Make one more move
        engine.getGameState().setCurrentTurn("White");
        assertTrue(engine.movePiece("a1", "a2"), "Rook should move to a2");
        
        // Now we should be at 50 moves
        assertTrue(engine.getGameState().isFiftyMoveRule(), "Should detect 50-move rule");
        
        // Reset and test that a pawn move resets the counter
        engine = new GameEngine(1);
        clearBoard(engine);
        
        engine.getGameState().getBoard().setPieceAt("e1", new King("White", "e1"));
        engine.getGameState().getBoard().setPieceAt("e8", new King("Black", "e8"));
        engine.getGameState().getBoard().setPieceAt("a2", new Pawn("White", "a2"));
        
        // Directly set the halfmove clock value
        try {
            java.lang.reflect.Field halfmoveField = MoveHistory.class.getDeclaredField("halfmoveClock");
            halfmoveField.setAccessible(true);
            halfmoveField.setInt(engine.getMoveHistory(), 99);
        } catch (Exception e) {
            fail("Could not set halfmove clock: " + e.getMessage());
        }
        
        // Make a pawn move
        engine.getGameState().setCurrentTurn("White");
        assertTrue(engine.movePiece("a2", "a3"), "Pawn should move to a3");
        
        // The counter should be reset
        assertEquals(0, engine.getMoveHistory().getHalfmoveClock(), "Halfmove clock should be reset to 0 after pawn move");
    }

    @Test
    void testCaptureResetsFiftyMoveCounter() {
        GameEngine engine = new GameEngine(1);
        clearBoard(engine);
        
        // Set up a position with a piece to capture
        engine.getGameState().getBoard().setPieceAt("e1", new King("White", "e1"));
        engine.getGameState().getBoard().setPieceAt("e8", new King("Black", "e8"));
        engine.getGameState().getBoard().setPieceAt("a1", new Rook("White", "a1"));
        engine.getGameState().getBoard().setPieceAt("a8", new Bishop("Black", "a8"));
        
        // Directly set the halfmove clock value
        try {
            java.lang.reflect.Field halfmoveField = MoveHistory.class.getDeclaredField("halfmoveClock");
            halfmoveField.setAccessible(true);
            halfmoveField.setInt(engine.getMoveHistory(), 99);
        } catch (Exception e) {
            fail("Could not set halfmove clock: " + e.getMessage());
        }
        
        // Make a capture move
        engine.getGameState().setCurrentTurn("White");
        assertTrue(engine.movePiece("a1", "a8"), "Rook should capture bishop");
        
        // The counter should be reset
        assertEquals(0, engine.getMoveHistory().getHalfmoveClock(), "Halfmove clock should be reset to 0 after capture");
    }

    @Test
    void testGameOverDetection() {
        GameEngine engine = new GameEngine(1);
        clearBoard(engine);
        
        // Set up a simple position
        engine.getGameState().getBoard().setPieceAt("e1", new King("White", "e1"));
        engine.getGameState().getBoard().setPieceAt("e8", new King("Black", "e8"));
        engine.getGameState().getBoard().setPieceAt("g1", new Knight("White", "g1"));
        
        // Initially the game should not be over
        assertFalse(engine.getGameState().isGameOver(), "Game should not be over initially");
        
        // Create a threefold repetition
        for (int i = 0; i < 2; i++) {
            engine.getGameState().setCurrentTurn("White");
            engine.movePiece("g1", "f3");
            engine.getGameState().setCurrentTurn("Black");
            engine.movePiece("e8", "d8");
            engine.getGameState().setCurrentTurn("White");
            engine.movePiece("f3", "g1");
            engine.getGameState().setCurrentTurn("Black");
            engine.movePiece("d8", "e8");
        }
        engine.getGameState().setCurrentTurn("White");
        engine.movePiece("g1", "f3");

        // Game should be over by threefold repetition
        assertTrue(engine.getGameState().isThreefoldRepetition(), "Should be threefold repetition");
        assertTrue(engine.getGameState().isGameOver(), "Game should be over by threefold repetition");
    }

    @Test
    void testStalemateGameOver() {
        GameEngine engine = new GameEngine(1);
        clearBoard(engine);
        
        // Setup a stalemate position
        engine.getGameState().getBoard().setPieceAt("h8", new King("Black", "h8"));
        engine.getGameState().getBoard().setPieceAt("f7", new King("White", "f7"));
        engine.getGameState().getBoard().setPieceAt("g6", new Queen("White", "g6"));
        engine.getGameState().setCurrentTurn("Black");

        // Verify it's stalemate and game is over
        assertTrue(engine.getGameState().isStalemate("Black"), "Should be stalemate");
        assertTrue(engine.getGameState().isGameOver(), "Game should be over by stalemate");
        assertFalse(engine.getGameState().getBoard().isKingInCheck("Black"), "King should not be in check");
    }
    
    // Helper method to clear the board
    private void clearBoard(GameEngine engine) {
        engine.getGameState().getBoard().clearBoard();
    }
}
