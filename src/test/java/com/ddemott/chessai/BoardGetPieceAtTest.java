package com.ddemott.chessai;

import com.ddemott.chessai.pieces.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BoardGetPieceAtTest {
    @Test
    void testInitialPositions() {
        Board board = new Board();
        // Test white pieces
        assertTrue(board.getPieceAt("a1") instanceof Rook);
        assertTrue(board.getPieceAt("b1") instanceof Knight);
        assertTrue(board.getPieceAt("c1") instanceof Bishop);
        assertTrue(board.getPieceAt("d1") instanceof Queen);
        assertTrue(board.getPieceAt("e1") instanceof King);
        assertTrue(board.getPieceAt("f1") instanceof Bishop);
        assertTrue(board.getPieceAt("g1") instanceof Knight);
        assertTrue(board.getPieceAt("h1") instanceof Rook);
        for (char col = 'a'; col <= 'h'; col++) {
            assertTrue(board.getPieceAt(col + "2") instanceof Pawn);
        }
        // Test black pieces
        assertTrue(board.getPieceAt("a8") instanceof Rook);
        assertTrue(board.getPieceAt("b8") instanceof Knight);
        assertTrue(board.getPieceAt("c8") instanceof Bishop);
        assertTrue(board.getPieceAt("d8") instanceof Queen);
        assertTrue(board.getPieceAt("e8") instanceof King);
        assertTrue(board.getPieceAt("f8") instanceof Bishop);
        assertTrue(board.getPieceAt("g8") instanceof Knight);
        assertTrue(board.getPieceAt("h8") instanceof Rook);
        for (char col = 'a'; col <= 'h'; col++) {
            assertTrue(board.getPieceAt(col + "7") instanceof Pawn);
        }
    }

    @Test
    void testEmptySquares() {
        Board board = new Board();
        assertNull(board.getPieceAt("a3"));
        assertNull(board.getPieceAt("e4"));
        assertNull(board.getPieceAt("h5"));
    }

    @Test
    void testOutOfBounds() {
        Board board = new Board();
        assertNull(board.getPieceAt("a0"));
        assertNull(board.getPieceAt("i1"));
        assertNull(board.getPieceAt("z9"));
        assertNull(board.getPieceAt("a9"));
    }

    @Test
    void testAfterMove() {
        Board board = new Board();
        board.movePiece("e2", "e4");
        assertNull(board.getPieceAt("e2"));
        assertTrue(board.getPieceAt("e4") instanceof Pawn);
    }
}
