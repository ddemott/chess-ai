package com.ddemott.chessai;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import com.ddemott.chessai.pieces.Rook;
import com.ddemott.chessai.pieces.Knight;
import com.ddemott.chessai.pieces.Pawn;
import com.ddemott.chessai.pieces.King;

public class BoardIsKingInCheckTest {
	@Test
	public void testKingNotInCheckAtStart() {
		Board board = new Board();
		assertFalse(board.isKingInCheck("White"), "White king should not be in check at game start");
		assertFalse(board.isKingInCheck("Black"), "Black king should not be in check at game start");
	}

	@Test
	public void testKingInCheckByRook() {
		Board board = new Board();
		// Remove pieces blocking the rook
		board.setPieceAt("e2", null);
		board.setPieceAt("e3", null);
		board.setPieceAt("e4", null);
		board.setPieceAt("e5", null);
		board.setPieceAt("e6", null);
		board.setPieceAt("e7", null);
		// Place black rook on e8
		board.setPieceAt("e8", new Rook("Black", "e8"));
		assertTrue(board.isKingInCheck("White"), "White king should be in check from black rook on e8");
	}

	@Test
	public void testKingInCheckByKnight() {
		Board board = new Board();
		// Remove pieces and place black knight on f3 which CAN attack e1
		board.setPieceAt("f3", null);
		board.setPieceAt("f3", new Knight("Black", "f3"));
		assertTrue(board.isKingInCheck("White"), "White king should be in check from black knight on f3");
	}

	@Test
	public void testKingInCheckByPawn() {
		Board board = new Board();
		// Place black pawn on d2, white king on e1
		board.setPieceAt("d2", new Pawn("Black", "d2"));
		board.setPieceAt("e1", new King("White", "e1"));
		assertTrue(board.isKingInCheck("White"), "White king should be in check from black pawn on d2");
	}

	@Test
	public void testKingNotInCheckWhenBlocked() {
		Board board = new Board();
		// Place black rook on e8, but block with white pawn on e2
		board.setPieceAt("e8", new Rook("Black", "e8"));
		board.setPieceAt("e2", new Pawn("White", "e2"));
		assertFalse(board.isKingInCheck("White"), "White king should not be in check if path is blocked");
	}
}
