package com.ddemott.chessai;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BoardKingCheckmateStalemateTest {
	@Test
	public void testFindKingPosition() {
		Board board = new Board();
		assertEquals("e1", board.findKingPosition("White"));
		assertEquals("e8", board.findKingPosition("Black"));
		board.setPieceAt("e1", null);
		assertNull(board.findKingPosition("White"));
	}

	@Test
	public void testIsSquareUnderAttack() {
		Board board = new Board();
		// Place black rook attacking e1
		board.setPieceAt("e2", null);
		board.setPieceAt("e3", null);
		board.setPieceAt("e4", null);
		board.setPieceAt("e5", null);
		board.setPieceAt("e6", null);
		board.setPieceAt("e7", null);
		board.setPieceAt("e8", new com.ddemott.chessai.pieces.Rook("Black", "e8"));
		assertTrue(board.isSquareUnderAttack("e1", "White"));
		assertFalse(board.isSquareUnderAttack("e1", "Black"));
	}

	@Test
	public void testIsCheckmate() {
		Board board = new Board();
		// Remove all pieces except white king and black queen
		for (char file = 'a'; file <= 'h'; file++) {
			for (int rank = 1; rank <= 8; rank++) {
				String pos = "" + file + rank;
				if (!pos.equals("e1") && !pos.equals("e2") && !pos.equals("d2")) {
					board.setPieceAt(pos, null);
				}
			}
		}
		board.setPieceAt("e1", new com.ddemott.chessai.pieces.King("White", "e1"));
		board.setPieceAt("e2", new com.ddemott.chessai.pieces.Queen("Black", "e2"));
		board.setPieceAt("d2", new com.ddemott.chessai.pieces.Queen("Black", "d2"));
		assertTrue(board.isCheckmate("White"));
		assertFalse(board.isCheckmate("Black"));
	}

	@Test
	public void testIsStalemate() {
		// Debug: print possible moves for White king
		Board board = new Board();
		// Clear board
		for (char file = 'a'; file <= 'h'; file++) {
			for (int rank = 1; rank <= 8; rank++) {
				board.setPieceAt("" + file + rank, null);
			}
		}
		// Classic stalemate: White king on h1, Black king on f2, Black queen on g2
		board.setPieceAt("h1", new com.ddemott.chessai.pieces.King("White", "h1"));
		board.setPieceAt("f2", new com.ddemott.chessai.pieces.King("Black", "f2"));
		board.setPieceAt("g3", new com.ddemott.chessai.pieces.Queen("Black", "g3"));
		com.ddemott.chessai.pieces.King whiteKing = (com.ddemott.chessai.pieces.King) board.getPieceAt("h1");
		System.out.println("White king possible moves: " + whiteKing.getAllPossibleMoves(board));
		assertTrue(board.isStalemate("White"));
		assertFalse(board.isStalemate("Black"));
	}
}
