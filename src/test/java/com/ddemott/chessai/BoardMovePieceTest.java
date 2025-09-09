package com.ddemott.chessai;

import com.ddemott.chessai.pieces.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BoardMovePieceTest {
    @Test
    void testMovePieceValid() {
        Board board = new Board();
        // Move white pawn from e2 to e4
        assertTrue(board.movePiece("e2", "e4"));
        assertNull(board.getPieceAt("e2"));
        assertTrue(board.getPieceAt("e4") instanceof Pawn);
    }

    @Test
    void testMovePieceInvalid() {
        Board board = new Board();
        // Try to move pawn from e2 to e5 (invalid for initial position)
        assertFalse(board.movePiece("e2", "e5"));
        assertTrue(board.getPieceAt("e2") instanceof Pawn);
        assertNull(board.getPieceAt("e5"));
    }

    @Test
    void testMovePieceFromEmptySquare() {
        Board board = new Board();
        assertFalse(board.movePiece("e3", "e4"));
    }

    @Test
    void testMovePieceExposesKingToCheck() {
        Board board = new Board();
        // Place white king on e1, white pawn on e2, black rook on e3
        King whiteKing = new King("White", "e1");
        Pawn whitePawn = new Pawn("White", "e2");
        Rook blackRook = new Rook("Black", "e3");
        board.setPieceAt("e1", whiteKing);
        board.setPieceAt("e2", whitePawn);
        board.setPieceAt("e3", blackRook);
        // Moving pawn from e2 to e4 exposes king to rook
        assertFalse(board.movePiece("e2", "e4"));
    }

    @Test
    void testWouldExposeKingToCheckTrue() {
        Board board = new Board();
        // Place white king on e1, white pawn on e2, black rook on e3
        King whiteKing = new King("White", "e1");
        Pawn whitePawn = new Pawn("White", "e2");
        Rook blackRook = new Rook("Black", "e3");
        board.setPieceAt("e1", whiteKing);
        board.setPieceAt("e2", whitePawn);
        board.setPieceAt("e3", blackRook);
        // Moving pawn from e2 to e4 would expose king to rook
        assertTrue(board.wouldExposeKingToCheck("e2", "e4"));
    }

    @Test
    void testWouldExposeKingToCheckFalse() {
        Board board = new Board();
        // No attacking piece
        assertFalse(board.wouldExposeKingToCheck("e2", "e3"));
    }
}
