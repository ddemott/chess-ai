package com.ddemott.chessai.pieces;

import com.ddemott.chessai.Board;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for individual piece movement logic and edge cases
 */
public class PieceMovementTest {
	@Test
	void testPawnDoubleMoveAndCapture() {
		Board board = new Board();

		// Create a fresh test scenario
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				board.setPieceAt(board.convertCoordinatesToPosition(i, j), null);
			}
		}

		// Place a white pawn at e2 (which is board[6][4])
		IPiece whitePawn = new Pawn("White", "e2");
		board.setPieceAt("e2", whitePawn);

		System.out.println("Testing pawn double move: e2 to e4");

		// Test double move
		assertTrue(whitePawn.isValidMove("e4", board),
				"White pawn should be able to move two squares from starting position");

		// Move the pawn to e4
		boolean moveSuccess = board.movePiece("e2", "e4");
		assertTrue(moveSuccess, "Moving pawn from e2 to e4 should succeed");

		// Get the pawn at its new position
		whitePawn = board.getPieceAt("e4");
		assertNotNull(whitePawn, "Pawn should be at e4 after move");

		// Check that double move is no longer allowed
		assertFalse(whitePawn.isValidMove("e6", board),
				"Pawn should not be able to move two squares after initial move");

		// Place a black pawn for capture
		IPiece blackPawn = new Pawn("Black", "d5");
		board.setPieceAt("d5", blackPawn);

		System.out.println("\nTesting pawn capture: e4 to d5");

		// Verify the pawn is properly set up at d5
		IPiece captureTarget = board.getPieceAt("d5");
		assertNotNull(captureTarget, "Black pawn should be at d5");
		assertEquals("Black", captureTarget.getColor(), "Pawn at d5 should be black");

		// Test the diagonal capture
		assertTrue(whitePawn.isValidMove("d5", board), "White pawn at e4 should be able to capture black pawn at d5");

		// Execute the capture
		assertTrue(board.movePiece("e4", "d5"), "Capture move should succeed");

		// Verify capture result
		IPiece afterCapture = board.getPieceAt("d5");
		assertNotNull(afterCapture, "White pawn should now be at d5");
		assertEquals("White", afterCapture.getColor(), "Pawn at d5 should be white after capture");
		assertNull(board.getPieceAt("e4"), "e4 should be empty after capture");
	}

	@Test
	void testKnightMovementAndIllegalMoves() {
		Board board = new Board();
		IPiece knight = board.getPieceAt("g1");
		assertTrue(knight.isValidMove("f3", board)); // legal
		assertTrue(knight.isValidMove("h3", board)); // legal
		assertFalse(knight.isValidMove("g2", board)); // illegal
	}

	@Test
	void testBishopMovementBlockedAndLegal() {
		Board board = new Board();
		IPiece bishop = board.getPieceAt("c1");
		assertFalse(bishop.isValidMove("e3", board)); // blocked by pawn
		board.setPieceAt("d2", null); // clear path
		assertTrue(bishop.isValidMove("e3", board)); // legal
	}

	@Test
	void testRookMovementBlockedAndLegal() {
		Board board = new Board();
		IPiece rook = board.getPieceAt("a1");
		assertFalse(rook.isValidMove("a3", board)); // blocked by pawn
		board.setPieceAt("a2", null); // clear path
		assertTrue(rook.isValidMove("a3", board)); // legal
	}

	@Test
	void testQueenMovementBlockedAndLegal() {
		Board board = new Board();
		IPiece queen = board.getPieceAt("d1");
		assertFalse(queen.isValidMove("d3", board)); // blocked by pawn
		board.setPieceAt("d2", null); // clear path
		assertTrue(queen.isValidMove("d3", board)); // legal
	}

	@Test
	void testKingMovementAndIllegalMoves() {
		Board board = new Board();
		IPiece king = board.getPieceAt("e1");
		board.setPieceAt("e2", null); // clear pawn blocking e2
		assertTrue(king.isValidMove("e2", board)); // legal
		assertFalse(king.isValidMove("e3", board)); // illegal (too far)
	}

	@Test
	void testOutOfBoundsMove() {
		Board board = new Board();
		IPiece rook = board.getPieceAt("a1");
		assertFalse(rook.isValidMove("a0", board)); // out of bounds
		assertFalse(rook.isValidMove("i1", board)); // out of bounds
	}
}
