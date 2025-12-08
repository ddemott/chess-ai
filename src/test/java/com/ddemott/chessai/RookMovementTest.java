package com.ddemott.chessai;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import com.ddemott.chessai.pieces.Rook;
import com.ddemott.chessai.pieces.Pawn;

public class RookMovementTest {

	@Test
	public void testValidHorizontalMove() {
		Board board = new Board();
		board.clearBoard();
		Rook rook = new Rook("White", "a1");
		board.setPieceAt("a1", rook);
		rook.setPosition("a1");
		assertTrue(rook.isValidMove("h1", board), "Rook should be able to move horizontally from a1 to h1");
	}

	@Test
	public void testValidVerticalMove() {
		Board board = new Board();
		board.clearBoard();
		Rook rook = new Rook("White", "a1");
		board.setPieceAt("a1", rook);
		rook.setPosition("a1");
		assertTrue(rook.isValidMove("a8", board), "Rook should be able to move vertically from a1 to a8");
	}

	@Test
	public void testInvalidDiagonalMove() {
		Board board = new Board();
		board.clearBoard();
		Rook rook = new Rook("White", "a1");
		board.setPieceAt("a1", rook);
		rook.setPosition("a1");
		assertFalse(rook.isValidMove("h8", board), "Rook should NOT be able to move diagonally from a1 to h8");
	}

	@Test
	public void testBlockedByOwnPiece() {
		Board board = new Board();
		board.clearBoard();
		Rook rook = new Rook("White", "a1");
		Pawn pawn = new Pawn("White", "a4");
		board.setPieceAt("a1", rook);
		rook.setPosition("a1");
		board.setPieceAt("a4", pawn);
		pawn.setPosition("a4");
		assertFalse(rook.isValidMove("a8", board), "Rook should NOT be able to move through own piece");
	}

	@Test
	public void testBlockedByOpponentPiece() {
		Board board = new Board();
		board.clearBoard();
		Rook rook = new Rook("White", "a1");
		Pawn pawn = new Pawn("Black", "a4");
		board.setPieceAt("a1", rook);
		rook.setPosition("a1");
		board.setPieceAt("a4", pawn);
		pawn.setPosition("a4");
		assertFalse(rook.isValidMove("a8", board), "Rook should NOT be able to move through opponent's piece");
		assertTrue(rook.isValidMove("a4", board), "Rook should be able to capture opponent's piece at a4");
	}
}
