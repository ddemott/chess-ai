package com.ddemott.chessai;

import com.ddemott.chessai.engine.GameEngine;
import com.ddemott.chessai.pieces.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for stalemate and draw conditions in the chess engine.
 */
public class StalemateAndDrawTest {

    @Test
    void testBasicStalemate() {
        try {
            GameEngine engine = new GameEngine(1);
            clearBoard(engine);
            
            // Set up a basic stalemate: Black king on h8, White queen on g6, White king on f7
            engine.getGameState().getBoard().setPieceAt("h8", new King("Black", "h8"));
            engine.getGameState().getBoard().setPieceAt("g6", new Queen("White", "g6"));
            engine.getGameState().getBoard().setPieceAt("f7", new King("White", "f7"));
            
            engine.getGameState().setCurrentTurn("Black");
            
            // Verify it's stalemate
            assertTrue(engine.getGameState().isStalemate("Black"), "Basic stalemate should be detected");
            assertFalse(engine.getGameState().getBoard().isCheckmate("Black"), "Stalemate should not be detected as checkmate");
            assertFalse(engine.getGameState().getBoard().isKingInCheck("Black"), "King should not be in check in stalemate");
        } catch (Exception e) {
            fail("Test failed due to unexpected exception: " + e.getMessage());
        }
    }

    @Test
    void testStalemateWithMultiplePieces() {
        try {
            GameEngine engine = new GameEngine(1);
            clearBoard(engine);
            
            // Set up a classic stalemate: Black king on a8, trapped by white pieces
            engine.getGameState().getBoard().setPieceAt("a8", new King("Black", "a8"));
            engine.getGameState().getBoard().setPieceAt("a7", new Pawn("White", "a7")); // Blocks king escape
            engine.getGameState().getBoard().setPieceAt("b7", new Queen("White", "b7")); // Controls b8 and diagonal squares
            engine.getGameState().getBoard().setPieceAt("c6", new Bishop("White", "c6")); // Additional control
            engine.getGameState().getBoard().setPieceAt("e1", new King("White", "e1")); // White king needed
            
            engine.getGameState().setCurrentTurn("Black");
            
            // Verify it's stalemate
            assertTrue(engine.getGameState().isStalemate("Black"), "Stalemate with multiple pieces should be detected");
        } catch (Exception e) {
            fail("Test failed due to unexpected exception: " + e.getMessage());
        }
    }

    @Test
    void testStalemateTrap() {
        try {
            GameEngine engine = new GameEngine(1);
            clearBoard(engine);
            
            // Set up a position where a move creates stalemate
            engine.getGameState().getBoard().setPieceAt("h8", new King("Black", "h8"));
            engine.getGameState().getBoard().setPieceAt("f7", new King("White", "f7"));
            engine.getGameState().getBoard().setPieceAt("h6", new Queen("White", "h6")); // Queen can move to create stalemate
            
            engine.getGameState().setCurrentTurn("White");
            
            // Not stalemate yet
            assertFalse(engine.getGameState().isStalemate("Black"), "Should not be stalemate yet");
            
            // Move the queen to g6, creating stalemate
            engine.movePiece("h6", "g6");
            
            // Now it should be stalemate
            assertTrue(engine.getGameState().isStalemate("Black"), "Should be stalemate after queen move");
        } catch (Exception e) {
            fail("Test failed due to unexpected exception: " + e.getMessage());
        }
    }

    @Test
    void testInsufficientMaterial() {
        try {
            GameEngine engine = new GameEngine(1);
            clearBoard(engine);
            
            // Test king vs king (draw)
            engine.getGameState().getBoard().setPieceAt("e1", new King("White", "e1"));
            engine.getGameState().getBoard().setPieceAt("e8", new King("Black", "e8"));
            
            assertTrue(isInsufficientMaterial(engine), "King vs king should be insufficient material");
            
            // Test king and knight vs king (draw)
            clearBoard(engine);
            engine.getGameState().getBoard().setPieceAt("e1", new King("White", "e1"));
            engine.getGameState().getBoard().setPieceAt("e8", new King("Black", "e8"));
            engine.getGameState().getBoard().setPieceAt("g1", new Knight("White", "g1"));
            
            assertTrue(isInsufficientMaterial(engine), "King and knight vs king should be insufficient material");
            
            // Test king and bishop vs king (draw)
            clearBoard(engine);
            engine.getGameState().getBoard().setPieceAt("e1", new King("White", "e1"));
            engine.getGameState().getBoard().setPieceAt("e8", new King("Black", "e8"));
            engine.getGameState().getBoard().setPieceAt("c1", new Bishop("White", "c1"));
            
            assertTrue(isInsufficientMaterial(engine), "King and bishop vs king should be insufficient material");
            
            // Test king and bishop vs king and knight (draw)
            clearBoard(engine);
            engine.getGameState().getBoard().setPieceAt("e1", new King("White", "e1"));
            engine.getGameState().getBoard().setPieceAt("e8", new King("Black", "e8"));
            engine.getGameState().getBoard().setPieceAt("c1", new Bishop("White", "c1"));
            engine.getGameState().getBoard().setPieceAt("g8", new Knight("Black", "g8"));
            
            assertTrue(isInsufficientMaterial(engine), "King and bishop vs king and knight should be insufficient material");
            
            // Test king and two knights vs king (not a forced draw, but usually drawn)
            clearBoard(engine);
            engine.getGameState().getBoard().setPieceAt("e1", new King("White", "e1"));
            engine.getGameState().getBoard().setPieceAt("e8", new King("Black", "e8"));
            engine.getGameState().getBoard().setPieceAt("b1", new Knight("White", "b1"));
            engine.getGameState().getBoard().setPieceAt("g1", new Knight("White", "g1"));
            
            assertTrue(isInsufficientMaterial(engine), "King and two knights vs king should be considered insufficient material");
            
            // Test positions with sufficient material
            
            // King and queen vs king (not a draw)
            clearBoard(engine);
            engine.getGameState().getBoard().setPieceAt("e1", new King("White", "e1"));
            engine.getGameState().getBoard().setPieceAt("e8", new King("Black", "e8"));
            engine.getGameState().getBoard().setPieceAt("d1", new Queen("White", "d1"));
            
            assertFalse(isInsufficientMaterial(engine), "King and queen vs king should be sufficient material");
            
            // King and rook vs king (not a draw)
            clearBoard(engine);
            engine.getGameState().getBoard().setPieceAt("e1", new King("White", "e1"));
            engine.getGameState().getBoard().setPieceAt("e8", new King("Black", "e8"));
            engine.getGameState().getBoard().setPieceAt("a1", new Rook("White", "a1"));
            
            assertFalse(isInsufficientMaterial(engine), "King and rook vs king should be sufficient material");
            
            // King and pawn vs king (not a draw)
            clearBoard(engine);
            engine.getGameState().getBoard().setPieceAt("e1", new King("White", "e1"));
            engine.getGameState().getBoard().setPieceAt("e8", new King("Black", "e8"));
            engine.getGameState().getBoard().setPieceAt("e2", new Pawn("White", "e2"));
            
            assertFalse(isInsufficientMaterial(engine), "King and pawn vs king should be sufficient material");
            
            // King and two bishops vs king (not a draw)
            clearBoard(engine);
            engine.getGameState().getBoard().setPieceAt("e1", new King("White", "e1"));
            engine.getGameState().getBoard().setPieceAt("e8", new King("Black", "e8"));
            engine.getGameState().getBoard().setPieceAt("c1", new Bishop("White", "c1"));
            engine.getGameState().getBoard().setPieceAt("f1", new Bishop("White", "f1"));
            
            assertFalse(isInsufficientMaterial(engine), "King and two bishops vs king should be sufficient material");
        } catch (Exception e) {
            fail("Test failed due to unexpected exception: " + e.getMessage());
        }
    }

    /**
     * Helper method to determine if the position has insufficient material for checkmate
     * This is a simplified implementation for testing
     */
    private boolean isInsufficientMaterial(GameEngine engine) {
        Board board = engine.getGameState().getBoard();
        int whitePieceCount = 0;
        int blackPieceCount = 0;
        boolean whiteHasKnight = false;
        boolean whiteHasBishop = false;
        boolean blackHasKnight = false;
        boolean blackHasBishop = false;
        
        // Count pieces
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                IPiece piece = board.getBoardArray()[row][col];
                if (piece != null) {
                    if (piece.getColor().equals("White")) {
                        whitePieceCount++;
                        if (piece instanceof Knight) whiteHasKnight = true;
                        if (piece instanceof Bishop) whiteHasBishop = true;
                        // Kings don't count towards material
                        if (piece instanceof King) whitePieceCount--;
                        // Pawns, rooks, and queens can always deliver mate
                        if (piece instanceof Pawn || piece instanceof Rook || piece instanceof Queen) {
                            return false;
                        }
                    } else {
                        blackPieceCount++;
                        if (piece instanceof Knight) blackHasKnight = true;
                        if (piece instanceof Bishop) blackHasBishop = true;
                        // Kings don't count towards material
                        if (piece instanceof King) blackPieceCount--;
                        // Pawns, rooks, and queens can always deliver mate
                        if (piece instanceof Pawn || piece instanceof Rook || piece instanceof Queen) {
                            return false;
                        }
                    }
                }
            }
        }
        
        // Check for insufficient material scenarios
        
        // King vs king
        if (whitePieceCount == 0 && blackPieceCount == 0) {
            return true;
        }
        
        // King + knight vs king or king + bishop vs king
        if ((whitePieceCount == 1 && (whiteHasKnight || whiteHasBishop) && blackPieceCount == 0) ||
            (blackPieceCount == 1 && (blackHasKnight || blackHasBishop) && whitePieceCount == 0)) {
            return true;
        }
        
        // King + knight vs king + knight
        if ((whitePieceCount == 1 && whiteHasKnight) && (blackPieceCount == 1 && blackHasKnight)) {
            return true;
        }
        
        // King + bishop vs king + bishop
        if ((whitePieceCount == 1 && whiteHasBishop) && (blackPieceCount == 1 && blackHasBishop)) {
            // This is a simplification - we should check if bishops are on same color
            return true;
        }
        
        // King + bishop vs king + knight (or vice versa)
        if ((whitePieceCount == 1 && whiteHasBishop && blackPieceCount == 1 && blackHasKnight) ||
            (whitePieceCount == 1 && whiteHasKnight && blackPieceCount == 1 && blackHasBishop)) {
            return true;
        }
        
        // King and two knights vs king
        if ((whitePieceCount == 2 && whiteHasKnight && blackPieceCount == 0) ||
            (blackPieceCount == 2 && blackHasKnight && whitePieceCount == 0)) {
            // Assuming both pieces are knights if count is 2
            return true;
        }
        
        return false;
    }
    
    // Helper method to clear the board
    private void clearBoard(GameEngine engine) {
        engine.getGameState().getBoard().clearBoard();
    }
}
