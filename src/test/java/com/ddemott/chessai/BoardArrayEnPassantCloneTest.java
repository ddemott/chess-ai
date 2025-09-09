package com.ddemott.chessai;

import com.ddemott.chessai.pieces.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BoardArrayEnPassantCloneTest {
    @Test
    void testGetBoardArray() {
        Board board = new Board();
        IPiece[][] arr = board.getBoardArray();
        assertNotNull(arr);
        assertEquals(8, arr.length);
        for (int i = 0; i < 8; i++) {
            assertEquals(8, arr[i].length);
        }
        // Check initial position: a1 is a Rook
        assertTrue(arr[0][0] instanceof Rook);
        // Check empty square: a3 is null
        assertNull(arr[2][0]);
    }

    @Test
    void testCloneCreatesDeepCopy() {
        Board board = new Board();
        Board copy = board.clone();
        assertNotSame(board, copy);
        assertNotSame(board.getBoardArray(), copy.getBoardArray());
        // Changing copy should not affect original
        copy.setPieceAt("a1", null);
        assertTrue(board.getPieceAt("a1") instanceof Rook);
        assertNull(copy.getPieceAt("a1"));
    }

    @Test
    void testGetEnPassantTargetDefault() {
        Board board = new Board();
        assertNull(board.getEnPassantTarget());
    }

    @Test
    void testSetAndGetEnPassantTarget() {
        Board board = new Board();
        board.setEnPassantTarget("e3");
        assertEquals("e3", board.getEnPassantTarget());
        board.setEnPassantTarget(null);
        assertNull(board.getEnPassantTarget());
    }

    @Test
    void testEnPassantTargetDoesNotAffectBoard() {
        Board board = new Board();
        board.setEnPassantTarget("d6");
        assertNull(board.getPieceAt("d6"));
    }
}
