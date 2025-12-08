package com.ddemott.chessai;

import com.ddemott.chessai.pieces.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BoardSetPieceAtTest {
	@Test
	void testSetPieceAtValidSquare() {
		Board board = new Board();
		Pawn pawn = new Pawn("White", "e4");
		board.setPieceAt("e4", pawn);
		assertEquals(pawn, board.getPieceAt("e4"));
	}

	@Test
	void testOverwritePiece() {
		Board board = new Board();
		Rook rook = new Rook("Black", "a1");
		board.setPieceAt("a1", rook);
		assertEquals(rook, board.getPieceAt("a1"));
	}

	@Test
	void testSetPieceAtEmptySquare() {
		Board board = new Board();
		Queen queen = new Queen("White", "d4");
		board.setPieceAt("d4", queen);
		assertEquals(queen, board.getPieceAt("d4"));
	}

	@Test
	void testRemovePiece() {
		Board board = new Board();
		board.setPieceAt("e2", null);
		assertNull(board.getPieceAt("e2"));
	}

	@Test
	void testSetPieceAtOutOfBounds() {
		Board board = new Board();
		Bishop bishop = new Bishop("Black", "z9");
		board.setPieceAt("z9", bishop);
		assertNull(board.getPieceAt("z9"));
	}
}
