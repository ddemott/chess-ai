package com.ddemott.chessai;

import com.ddemott.chessai.engine.GameEngine;
import com.ddemott.chessai.pieces.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive tests for pin detection in the chess engine.
 */
public class PinDetectionTest {

    @Test
    void testHorizontalPin() {
        GameEngine engine = new GameEngine(1);
        clearBoard(engine);
        
        // Set up a horizontal pin: White king on e1, White queen on e2, Black rook on e8
        engine.getGameState().getBoard().setPieceAt("e1", new King("White", "e1"));
        engine.getGameState().getBoard().setPieceAt("e2", new Queen("White", "e2"));
        engine.getGameState().getBoard().setPieceAt("e8", new Rook("Black", "e8"));
        engine.getGameState().setCurrentTurn("White");
        
        // Try to move the pinned queen sideways (should fail)
        boolean queenCanMove = engine.movePiece("e2", "d3");
        assertFalse(queenCanMove, "Horizontally pinned queen should not be able to move sideways");
        
        // Try to move the queen along the pin line toward the attacker (should succeed)
        queenCanMove = engine.movePiece("e2", "e3");
        assertTrue(queenCanMove, "Queen should be able to move along the pin line toward the attacker");
    }

    @Test
    void testVerticalPin() {
        GameEngine engine = new GameEngine(1);
        clearBoard(engine);
        
        // Set up a vertical pin: White king on d1, White knight on d3, Black rook on d8
        engine.getGameState().getBoard().setPieceAt("d1", new King("White", "d1"));
        engine.getGameState().getBoard().setPieceAt("d3", new Knight("White", "d3"));
        engine.getGameState().getBoard().setPieceAt("d8", new Rook("Black", "d8"));
        engine.getGameState().setCurrentTurn("White");
        
        // Knights cannot move along the pin line, so any move should fail
        boolean knightCanMove = engine.movePiece("d3", "b4"); // L-shape move
        assertFalse(knightCanMove, "Vertically pinned knight should not be able to move");
        
        knightCanMove = engine.movePiece("d3", "f4"); // L-shape move
        assertFalse(knightCanMove, "Vertically pinned knight should not be able to move");
        
        // Replace knight with a rook and test movement along the pin line
        engine.getGameState().getBoard().setPieceAt("d3", null);
        engine.getGameState().getBoard().setPieceAt("d3", new Rook("White", "d3"));
        
        boolean rookCanMove = engine.movePiece("d3", "d4"); // Move along pin line
        assertTrue(rookCanMove, "Rook should be able to move along the vertical pin line");
    }

    @Test
    void testDiagonalPin() {
        GameEngine engine = new GameEngine(1);
        clearBoard(engine);
        
        // Set up a diagonal pin: White king on c1, White rook on d2, Black bishop on f4
        engine.getGameState().getBoard().setPieceAt("c1", new King("White", "c1"));
        engine.getGameState().getBoard().setPieceAt("d2", new Rook("White", "d2"));
        engine.getGameState().getBoard().setPieceAt("f4", new Bishop("Black", "f4"));
        engine.getGameState().setCurrentTurn("White");
        
        // Rook cannot move along diagonal, so any move should fail
        boolean rookCanMove = engine.movePiece("d2", "d3"); // Vertical move
        assertFalse(rookCanMove, "Diagonally pinned rook should not be able to move");
        
        rookCanMove = engine.movePiece("d2", "e2"); // Horizontal move
        assertFalse(rookCanMove, "Diagonally pinned rook should not be able to move");
        
        // Replace rook with a bishop and test movement along the pin line
        engine.getGameState().getBoard().setPieceAt("d2", null);
        engine.getGameState().getBoard().setPieceAt("d2", new Bishop("White", "d2"));
        
        boolean bishopCanMove = engine.movePiece("d2", "e3"); // Move along pin line
        assertTrue(bishopCanMove, "Bishop should be able to move along the diagonal pin line");
    }

    @Test
    void testMultiplePins() {
        GameEngine engine = new GameEngine(1);
        clearBoard(engine);
        
        // Set up multiple pins: White king on d4, pinned white pieces, black attackers
        engine.getGameState().getBoard().setPieceAt("d4", new King("White", "d4"));
        engine.getGameState().getBoard().setPieceAt("d5", new Queen("White", "d5")); // Vertically pinned
        engine.getGameState().getBoard().setPieceAt("e4", new Bishop("White", "e4")); // Horizontally pinned
        engine.getGameState().getBoard().setPieceAt("d8", new Rook("Black", "d8")); // Vertical attacker
        engine.getGameState().getBoard().setPieceAt("h4", new Rook("Black", "h4")); // Horizontal attacker
        engine.getGameState().setCurrentTurn("White");
        
        // Test queen movement - should only be able to move vertically
        boolean queenCanMove = engine.movePiece("d5", "e6"); // Diagonal move
        assertFalse(queenCanMove, "Vertically pinned queen should not be able to move diagonally");
        
        queenCanMove = engine.movePiece("d5", "d6"); // Vertical move along pin line
        assertTrue(queenCanMove, "Queen should be able to move along the vertical pin line");
        
        // Test bishop movement - should not be able to move at all (bishops can't move horizontally)
        boolean bishopCanMove = engine.movePiece("e4", "f5"); // Diagonal move
        assertFalse(bishopCanMove, "Horizontally pinned bishop should not be able to move");
        
        bishopCanMove = engine.movePiece("e4", "f4"); // Horizontal move along pin line
        assertFalse(bishopCanMove, "Bishop should not be able to move horizontally even along pin line");
    }

    @Test
    void testPinEdgeCases() {
        GameEngine engine = new GameEngine(1);
        clearBoard(engine);
        
        // Edge case 1: King in corner, pinned piece at edge
        engine.getGameState().getBoard().setPieceAt("a1", new King("White", "a1"));
        engine.getGameState().getBoard().setPieceAt("a2", new Queen("White", "a2"));
        engine.getGameState().getBoard().setPieceAt("a8", new Rook("Black", "a8"));
        engine.getGameState().setCurrentTurn("White");
        
        boolean queenCanMove = engine.movePiece("a2", "b3"); // Diagonal move
        assertFalse(queenCanMove, "Queen at edge should not be able to move when pinned");
        
        queenCanMove = engine.movePiece("a2", "a3"); // Vertical move along pin line
        assertTrue(queenCanMove, "Queen should be able to move along the vertical pin line");
        
        // Edge case 2: Potential pin that isn't actually a pin (piece can capture attacker)
        engine = new GameEngine(1);
        clearBoard(engine);
        
        engine.getGameState().getBoard().setPieceAt("e1", new King("White", "e1"));
        engine.getGameState().getBoard().setPieceAt("e2", new Rook("White", "e2"));
        engine.getGameState().getBoard().setPieceAt("e3", new Bishop("Black", "e3")); // Not a pin, can be captured
        engine.getGameState().setCurrentTurn("White");
        
        boolean rookCanCapture = engine.movePiece("e2", "e3"); // Capture the "attacker"
        assertTrue(rookCanCapture, "Rook should be able to capture the attacking piece");
    }

    // Helper method to clear the board
    private void clearBoard(GameEngine engine) {
        engine.getGameState().getBoard().clearBoard();
    }
}
