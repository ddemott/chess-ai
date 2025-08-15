package com.ddemott.chessai;

import com.ddemott.chessai.engine.GameEngine;
import com.ddemott.chessai.pieces.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests various checkmate patterns in the chess engine.
 */
public class CheckmatePatternTest {

    @Test
    void testBackRankMate() {
        GameEngine engine = new GameEngine(1);
        clearBoard(engine);
        
        // Set up a back-rank mate: Black king on g8, Black pawns on f7, g7, h7, White queen on a8
        engine.getGameState().getBoard().setPieceAt("g8", new King("Black", "g8"));
        engine.getGameState().getBoard().setPieceAt("f7", new Pawn("Black", "f7"));
        engine.getGameState().getBoard().setPieceAt("g7", new Pawn("Black", "g7"));
        engine.getGameState().getBoard().setPieceAt("h7", new Pawn("Black", "h7"));
        engine.getGameState().getBoard().setPieceAt("a8", new Queen("White", "a8"));
        engine.getGameState().getBoard().setPieceAt("e1", new King("White", "e1")); // White king needed
        
        engine.getGameState().setCurrentTurn("White");
        
        // Verify it's checkmate
        assertTrue(engine.getGameState().getBoard().isCheckmate("Black"), "Back-rank mate should be detected as checkmate");
        assertFalse(engine.getGameState().getBoard().isStalemate("Black"), "Back-rank mate should not be detected as stalemate");
    }

    @Test
    void testSmotheredMate() {
        GameEngine engine = new GameEngine(1);
        clearBoard(engine);
        
        // Set up a smothered mate: Black king on h8, Black pawns on g7, h7, Black rook on g8, White knight on f7
        engine.getGameState().getBoard().setPieceAt("h8", new King("Black", "h8"));
        engine.getGameState().getBoard().setPieceAt("g7", new Pawn("Black", "g7"));
        engine.getGameState().getBoard().setPieceAt("h7", new Pawn("Black", "h7"));
        engine.getGameState().getBoard().setPieceAt("g8", new Rook("Black", "g8"));
        engine.getGameState().getBoard().setPieceAt("f7", new Knight("White", "f7"));
        engine.getGameState().getBoard().setPieceAt("e1", new King("White", "e1")); // White king needed
        
        engine.getGameState().setCurrentTurn("White");
        
        // Verify it's checkmate
        assertTrue(engine.getGameState().getBoard().isCheckmate("Black"), "Smothered mate should be detected as checkmate");
        assertFalse(engine.getGameState().getBoard().isStalemate("Black"), "Smothered mate should not be detected as stalemate");
    }

    @Test
    void testScholarsMatePrepare() {
        GameEngine engine = new GameEngine(1);
        // The GameEngine constructor already initializes the board
        
        // Execute Scholar's mate pattern
        engine.movePiece("e2", "e4");
        engine.movePiece("e7", "e5");
        engine.movePiece("f1", "c4"); // White bishop to c4
        engine.movePiece("b8", "c6"); // Black knight to c6
        engine.movePiece("d1", "h5"); // White queen to h5
        engine.movePiece("g8", "f6"); // Black knight to f6
        
        // Not checkmate yet
        assertFalse(engine.getGameState().getBoard().isCheckmate("Black"), "Scholar's mate should not be checkmate yet");
        
        // Complete the scholar's mate
        engine.movePiece("h5", "f7"); // Queen takes f7 pawn
        
        // Now it should be checkmate
        assertTrue(engine.getGameState().getBoard().isCheckmate("Black"), "Scholar's mate should be detected as checkmate");
    }

    @Test
    void testLegalsMate() {
        GameEngine engine = new GameEngine(1);
        clearBoard(engine);
        
        // Set up Legal's mate: Black king on e8, Black pawns/pieces around, White rook on e1, White knight on f6
        engine.getGameState().getBoard().setPieceAt("e8", new King("Black", "e8"));
        engine.getGameState().getBoard().setPieceAt("d7", new Pawn("Black", "d7"));
        engine.getGameState().getBoard().setPieceAt("e7", new Pawn("Black", "e7"));
        engine.getGameState().getBoard().setPieceAt("f7", new Pawn("Black", "f7"));
        engine.getGameState().getBoard().setPieceAt("f8", new Bishop("Black", "f8"));
        engine.getGameState().getBoard().setPieceAt("f6", new Knight("White", "f6")); // Knight checking the king
        engine.getGameState().getBoard().setPieceAt("e1", new Rook("White", "e1"));   // Rook aligned with king
        engine.getGameState().getBoard().setPieceAt("a1", new King("White", "a1"));   // White king needed
        
        engine.getGameState().setCurrentTurn("White");
        
        // Verify it's checkmate
        assertTrue(engine.getGameState().getBoard().isCheckmate("Black"), "Legal's mate should be detected as checkmate");
    }

    @Test
    void testArabianMate() {
        GameEngine engine = new GameEngine(1);
        clearBoard(engine);
        
        // Set up Arabian mate: Black king on h8, White rook on g7, White knight on f7
        engine.getGameState().getBoard().setPieceAt("h8", new King("Black", "h8"));
        engine.getGameState().getBoard().setPieceAt("g7", new Rook("White", "g7")); // Rook checking the king
        engine.getGameState().getBoard().setPieceAt("f7", new Knight("White", "f7")); // Knight blocking escape
        engine.getGameState().getBoard().setPieceAt("a1", new King("White", "a1")); // White king needed
        
        engine.getGameState().setCurrentTurn("White");
        
        // Verify it's checkmate
        assertTrue(engine.getGameState().getBoard().isCheckmate("Black"), "Arabian mate should be detected as checkmate");
    }
    
    @Test
    void testFoolsMate() {
        GameEngine engine = new GameEngine(1);
        // GameEngine constructor already initializes the board
        
        // Execute Fool's mate pattern
        engine.movePiece("f2", "f3"); // White's first move
        engine.movePiece("e7", "e5"); // Black's first move
        engine.movePiece("g2", "g4"); // White's second move
        engine.movePiece("d8", "h4"); // Black queen delivers checkmate
        
        // Verify it's checkmate
        assertTrue(engine.getGameState().getBoard().isCheckmate("White"), "Fool's mate should be detected as checkmate");
    }

    // Helper method to clear the board
    private void clearBoard(GameEngine engine) {
        engine.getGameState().getBoard().clearBoard();
    }
}
