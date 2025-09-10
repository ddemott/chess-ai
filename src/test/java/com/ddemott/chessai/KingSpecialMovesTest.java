package com.ddemott.chessai;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import com.ddemott.chessai.pieces.King;
import com.ddemott.chessai.pieces.Rook;
import com.ddemott.chessai.Board;

public class KingSpecialMovesTest {

    @Test
    public void testKingsideCastlingAllowed() {
        Board board = new Board();
        board.clearBoard();
        King king = new King("White", "e1");
        Rook rook = new Rook("White", "h1");
        board.setPieceAt("e1", king);
        king.setPosition("e1");
        board.setPieceAt("h1", rook);
        rook.setPosition("h1");
        // No pieces between king and rook, no check
        assertTrue(king.isValidMove("g1", board), "King should be able to castle king-side");
    }

    @Test
    public void testQueensideCastlingAllowed() {
        Board board = new Board();
        board.clearBoard();
        King king = new King("White", "e1");
        Rook rook = new Rook("White", "a1");
        board.setPieceAt("e1", king);
        king.setPosition("e1");
        board.setPieceAt("a1", rook);
        rook.setPosition("a1");
        // No pieces between king and rook, no check
        assertTrue(king.isValidMove("c1", board), "King should be able to castle queen-side");
    }

    @Test
    public void testCastlingBlockedByPiece() {
        Board board = new Board();
        board.clearBoard();
        King king = new King("White", "e1");
        Rook rook = new Rook("White", "h1");
        board.setPieceAt("e1", king);
        king.setPosition("e1");
        board.setPieceAt("h1", rook);
        rook.setPosition("h1");
        // Place a piece between king and rook
        Rook blocker = new Rook("White", "f1");
        board.setPieceAt("f1", blocker);
        blocker.setPosition("f1");
        assertFalse(king.isValidMove("g1", board), "King should NOT be able to castle king-side if path is blocked");
    }

    @Test
    public void testCastlingThroughCheck() {
        Board board = new Board();
        board.clearBoard();
        King king = new King("White", "e1");
        Rook rook = new Rook("White", "h1");
        board.setPieceAt("e1", king);
        king.setPosition("e1");
        board.setPieceAt("h1", rook);
        rook.setPosition("h1");
        // Place an opponent rook attacking f1
        Rook oppRook = new Rook("Black", "f8");
        board.setPieceAt("f8", oppRook);
        oppRook.setPosition("f8");
        assertFalse(king.isValidMove("g1", board), "King should NOT be able to castle through check");
    }

    @Test
    public void testKingCannotMoveNextToOpponentKing() {
        Board board = new Board();
        board.clearBoard();
        King whiteKing = new King("White", "e4");
        King blackKing = new King("Black", "e6");
        board.setPieceAt("e4", whiteKing);
        whiteKing.setPosition("e4");
        board.setPieceAt("e6", blackKing);
        blackKing.setPosition("e6");
        // Try to move white king next to black king
        assertFalse(whiteKing.isValidMove("e5", board), "King should NOT be able to move next to opponent king");
        assertFalse(whiteKing.isValidMove("d5", board), "King should NOT be able to move next to opponent king");
        assertFalse(whiteKing.isValidMove("f5", board), "King should NOT be able to move next to opponent king");
    }
}
