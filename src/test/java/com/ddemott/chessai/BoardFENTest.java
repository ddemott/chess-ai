package com.ddemott.chessai;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.ddemott.chessai.pieces.*;

class BoardFENTest {
	private Board board;

	@BeforeEach
	void setUp() {
		board = new Board();
		board.clearBoard(); // Start with empty board for each test
	}

	@Test
	void testStartingPosition() {
		board = new Board(); // Initialize with starting position
		String expectedFEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR";
		assertEquals(expectedFEN, board.toFEN());
	}

	@Test
	void testEmptyBoard() {
		String expectedFEN = "8/8/8/8/8/8/8/8";
		assertEquals(expectedFEN, board.toFEN());
	}

	@Test
	void testMixedPosition() {
		// Set up a mixed position with various pieces
		board.setPieceAt("e4", new King("White", "e4"));
		board.setPieceAt("e8", new King("Black", "e8"));
		board.setPieceAt("d5", new Pawn("White", "d5"));
		board.setPieceAt("f5", new Pawn("Black", "f5"));
		board.setPieceAt("c3", new Knight("White", "c3"));
		board.setPieceAt("g6", new Bishop("Black", "g6"));

		String expectedFEN = "4k3/8/6b1/3P1p2/4K3/2N5/8/8";
		assertEquals(expectedFEN, board.toFEN());
	}

	@Test
	void testComplexPosition() {
		// Test a more complex position with multiple empty squares and pieces
		board.setPieceAt("a8", new Rook("Black", "a8"));
		board.setPieceAt("h8", new King("Black", "h8"));
		board.setPieceAt("a7", new Pawn("Black", "a7"));
		board.setPieceAt("g7", new Pawn("Black", "g7"));
		board.setPieceAt("h7", new Pawn("Black", "h7"));
		board.setPieceAt("c6", new Bishop("Black", "c6"));
		board.setPieceAt("e4", new King("White", "e4"));
		board.setPieceAt("b2", new Pawn("White", "b2"));
		board.setPieceAt("f2", new Pawn("White", "f2"));

		String expectedFEN = "r6k/p5pp/2b5/8/4K3/8/1P3P2/8";
		assertEquals(expectedFEN, board.toFEN());
	}

	@Test
	void testSinglePiece() {
		board.setPieceAt("e4", new Queen("White", "e4"));
		String expectedFEN = "8/8/8/8/4Q3/8/8/8";
		assertEquals(expectedFEN, board.toFEN());
	}

	@Test
	void testAllPieceTypes() {
		// Place one of each piece type in white and black
		board.setPieceAt("a1", new King("White", "a1"));
		board.setPieceAt("b1", new Queen("White", "b1"));
		board.setPieceAt("c1", new Rook("White", "c1"));
		board.setPieceAt("d1", new Bishop("White", "d1"));
		board.setPieceAt("e1", new Knight("White", "e1"));
		board.setPieceAt("f1", new Pawn("White", "f1"));

		board.setPieceAt("a8", new King("Black", "a8"));
		board.setPieceAt("b8", new Queen("Black", "b8"));
		board.setPieceAt("c8", new Rook("Black", "c8"));
		board.setPieceAt("d8", new Bishop("Black", "d8"));
		board.setPieceAt("e8", new Knight("Black", "e8"));
		board.setPieceAt("f8", new Pawn("Black", "f8"));

		String expectedFEN = "kqrbnp2/8/8/8/8/8/8/KQRBNP2";
		assertEquals(expectedFEN, board.toFEN());
	}
}
