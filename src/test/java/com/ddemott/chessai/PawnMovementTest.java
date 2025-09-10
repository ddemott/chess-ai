package com.ddemott.chessai;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import com.ddemott.chessai.pieces.Pawn;
import com.ddemott.chessai.pieces.Queen;
import com.ddemott.chessai.Board;

public class PawnMovementTest {

    @Test
    public void testValidSingleForwardMove() {
        Board board = new Board();
        board.clearBoard();
        Pawn pawn = new Pawn("White", "e2");
        board.setPieceAt("e2", pawn);
        pawn.setPosition("e2");
        assertTrue(pawn.isValidMove("e3", board), "Pawn should be able to move forward one square");
    }

    @Test
    public void testValidDoubleForwardMove() {
        Board board = new Board();
        board.clearBoard();
        Pawn pawn = new Pawn("White", "e2");
        board.setPieceAt("e2", pawn);
        pawn.setPosition("e2");
        assertTrue(pawn.isValidMove("e4", board), "Pawn should be able to move forward two squares from starting position");
    }

    @Test
    public void testInvalidBackwardMove() {
        Board board = new Board();
        board.clearBoard();
        Pawn pawn = new Pawn("White", "e3");
        board.setPieceAt("e3", pawn);
        pawn.setPosition("e3");
        assertFalse(pawn.isValidMove("e2", board), "Pawn should NOT be able to move backward");
    }

    @Test
    public void testCaptureDiagonally() {
        Board board = new Board();
        board.clearBoard();
        Pawn pawn = new Pawn("White", "e4");
        Pawn opponent = new Pawn("Black", "d5");
        board.setPieceAt("e4", pawn);
        pawn.setPosition("e4");
        board.setPieceAt("d5", opponent);
        opponent.setPosition("d5");
        assertTrue(pawn.isValidMove("d5", board), "Pawn should be able to capture diagonally");
    }

    @Test
    public void testBlockedByOwnPiece() {
        Board board = new Board();
        board.clearBoard();
        Pawn pawn = new Pawn("White", "e2");
        Pawn blocker = new Pawn("White", "e3");
        board.setPieceAt("e2", pawn);
        pawn.setPosition("e2");
        board.setPieceAt("e3", blocker);
        blocker.setPosition("e3");
        assertFalse(pawn.isValidMove("e3", board), "Pawn should NOT be able to move forward if blocked by own piece");
    }

    @Test
    public void testBlockedByOpponentPiece() {
        Board board = new Board();
        board.clearBoard();
        Pawn pawn = new Pawn("White", "e2");
        Pawn blocker = new Pawn("Black", "e3");
        board.setPieceAt("e2", pawn);
        pawn.setPosition("e2");
        board.setPieceAt("e3", blocker);
        blocker.setPosition("e3");
        assertFalse(pawn.isValidMove("e3", board), "Pawn should NOT be able to move forward if blocked by opponent's piece");
    }

    @Test
    public void testEnPassantCapture() {
        Board board = new Board();
        board.clearBoard();
        Pawn whitePawn = new Pawn("White", "e5");
        Pawn blackPawn = new Pawn("Black", "d7");
        board.setPieceAt("e5", whitePawn);
        whitePawn.setPosition("e5");
        board.setPieceAt("d7", blackPawn);
        blackPawn.setPosition("d7");
        // Black pawn moves two squares forward
        board.movePiece("d7", "d5");
        // Set en passant target
        board.setEnPassantTarget("d6");
        assertTrue(whitePawn.isValidMove("d6", board), "White pawn should be able to capture en passant at d6");
    }

    @Test
    public void testPromotionToQueen() {
        Board board = new Board();
        board.clearBoard();
        Pawn pawn = new Pawn("White", "e7");
        board.setPieceAt("e7", pawn);
        pawn.setPosition("e7");
        // Move pawn to e8 and promote to queen
        assertTrue(board.movePiece("e7", "e8", "Q"), "Pawn should be able to promote to queen at e8");
        assertTrue(board.getPieceAt("e8") instanceof Queen, "Pawn should be promoted to queen at e8");
    }
}
