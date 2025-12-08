package com.ddemott.chessai;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import com.ddemott.chessai.pieces.Queen;
import com.ddemott.chessai.pieces.Pawn;

public class QueenMovementTest {

	@Test
	public void testValidDiagonalMove() {
		Board board = new Board();
		board.clearBoard();
		Queen queen = new Queen("White", "d4");
		board.setPieceAt("d4", queen);
		queen.setPosition("d4");
		assertTrue(queen.isValidMove("g7", board), "Queen should be able to move diagonally from d4 to g7");
	}

	@Test
	public void testValidHorizontalMove() {
		Board board = new Board();
		board.clearBoard();
		Queen queen = new Queen("White", "d4");
		board.setPieceAt("d4", queen);
		queen.setPosition("d4");
		assertTrue(queen.isValidMove("h4", board), "Queen should be able to move horizontally from d4 to h4");
	}

	@Test
	public void testValidVerticalMove() {
		Board board = new Board();
		board.clearBoard();
		Queen queen = new Queen("White", "d4");
		board.setPieceAt("d4", queen);
		queen.setPosition("d4");
		assertTrue(queen.isValidMove("d8", board), "Queen should be able to move vertically from d4 to d8");
	}

	@Test
	public void testInvalidLShapeMove() {
		Board board = new Board();
		board.clearBoard();
		Queen queen = new Queen("White", "d4");
		board.setPieceAt("d4", queen);
		queen.setPosition("d4");
		assertFalse(queen.isValidMove("e6", board), "Queen should NOT be able to move in an L-shape like a knight");
	}

	@Test
	public void testBlockedByOwnPiece() {
		Board board = new Board();
		board.clearBoard();
		Queen queen = new Queen("White", "d4");
		Pawn pawn = new Pawn("White", "f6");
		board.setPieceAt("d4", queen);
		queen.setPosition("d4");
		board.setPieceAt("f6", pawn);
		pawn.setPosition("f6");
		assertFalse(queen.isValidMove("g7", board), "Queen should NOT be able to move through own piece");
	}

	@Test
	public void testBlockedByOpponentPiece() {
		Board board = new Board();
		board.clearBoard();
		Queen queen = new Queen("White", "d4");
		Pawn pawn = new Pawn("Black", "f6");
		board.setPieceAt("d4", queen);
		queen.setPosition("d4");
		board.setPieceAt("f6", pawn);
		pawn.setPosition("f6");
		assertFalse(queen.isValidMove("g7", board), "Queen should NOT be able to move through opponent's piece");
		assertTrue(queen.isValidMove("f6", board), "Queen should be able to capture opponent's piece at f6");
	}
}
