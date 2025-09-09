package com.ddemott.chessai;

import com.ddemott.chessai.pieces.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BoardPiecePinTest {
    @Test
    public void testIsPiecePinned_basicPin() {
        Board board = new Board();
        board.clearBoard();
        // Place white king on e1, white rook on e2, black rook on e8
        board.setPieceAt("e1", new King("White", "e1"));
        board.setPieceAt("e2", new Rook("White", "e2"));
        board.setPieceAt("e8", new Rook("Black", "e8"));
        // The white rook on e2 is pinned to the king by the black rook on e8
        assertTrue(board.isPiecePinned("e2"));
    }

    @Test
    public void testIsPiecePinned_notPinned() {
        Board board = new Board();
        board.clearBoard();
        // Place white king on e1, white rook on d2, black rook on e8
        board.setPieceAt("e1", new King("White", "e1"));
        board.setPieceAt("d2", new Rook("White", "d2"));
        board.setPieceAt("e8", new Rook("Black", "e8"));
        // The white rook on d2 is not pinned
        assertFalse(board.isPiecePinned("d2"));
    }

    @Test
    public void testIsPiecePinned_knightCannotBePinned() {
        Board board = new Board();
        board.clearBoard();
        // Place white king on e1, white knight on e2, black rook on e8
        board.setPieceAt("e1", new King("White", "e1"));
        board.setPieceAt("e2", new Knight("White", "e2"));
        board.setPieceAt("e8", new Rook("Black", "e8"));
        // Knights cannot be pinned
        assertTrue(board.isPiecePinned("e2"));
    }

    @Test
    public void testIsPiecePinned_noKing() {
        Board board = new Board();
        board.clearBoard();
        // Place rook on e2, no king
        board.setPieceAt("e2", new Rook("White", "e2"));
        assertFalse(board.isPiecePinned("e2"));
    }

    @Test
    public void testIsPiecePinned_noPiece() {
        Board board = new Board();
        board.clearBoard();
        // No piece at e2
        assertFalse(board.isPiecePinned("e2"));
    }
}
