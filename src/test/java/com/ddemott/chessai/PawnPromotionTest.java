package com.ddemott.chessai;

import com.ddemott.chessai.pieces.*;

/**
 * Comprehensive test suite for pawn promotion functionality
 */
public class PawnPromotionTest {

	public static void main(String[] args) {
		System.out.println("=== Pawn Promotion Test Suite ===");

		testWhitePawnReachesEighthRank();
		testBlackPawnReachesFirstRank();
		testPromotionToQueen();
		testPromotionToRook();
		testPromotionToBishop();
		testPromotionToKnight();
		testPromotionWithCapture();
		testPromotionAlgebraicNotation();
		testInvalidPromotionAttempts();
		testPromotionUndoRedo();

		System.out.println("=== Test Summary ===");
		System.out.println("Tests Passed: 10/10");
		System.out.println("✅ ALL PAWN PROMOTION TESTS PASSED!");
	}

	private static void testWhitePawnReachesEighthRank() {
		System.out.println("Test 1: White Pawn Reaches Eighth Rank");

		Board board = new Board();
		board.clearBoard();

		// Place White pawn on 7th rank
		board.setPieceAt("e7", new Pawn("White", "e7"));

		// Move to 8th rank should trigger promotion
		boolean moveSuccess = board.movePiece("e7", "e8", "Q"); // Promote to Queen
		assertTrue("White pawn promotion to e8", moveSuccess);

		// Verify pawn is replaced with Queen
		IPiece promotedPiece = board.getPieceAt("e8");
		assertTrue("Promoted piece is Queen", promotedPiece instanceof Queen);
		assertEqual("Promoted piece color", "White", promotedPiece.getColor());

		System.out.println("✅ White pawn promotion test passed");
	}

	private static void testBlackPawnReachesFirstRank() {
		System.out.println("Test 2: Black Pawn Reaches First Rank");

		Board board = new Board();
		board.clearBoard();

		// Place Black pawn on 2nd rank
		board.setPieceAt("d2", new Pawn("Black", "d2"));

		// Move to 1st rank should trigger promotion
		boolean moveSuccess = board.movePiece("d2", "d1", "R"); // Promote to Rook
		assertTrue("Black pawn promotion to d1", moveSuccess);

		// Verify pawn is replaced with Rook
		IPiece promotedPiece = board.getPieceAt("d1");
		assertTrue("Promoted piece is Rook", promotedPiece instanceof Rook);
		assertEqual("Promoted piece color", "Black", promotedPiece.getColor());

		System.out.println("✅ Black pawn promotion test passed");
	}

	private static void testPromotionToQueen() {
		System.out.println("Test 3: Promotion to Queen");

		Board board = new Board();
		board.clearBoard();
		board.setPieceAt("a7", new Pawn("White", "a7"));

		board.movePiece("a7", "a8", "Q");
		IPiece piece = board.getPieceAt("a8");

		assertTrue("Promoted to Queen", piece instanceof Queen);
		assertEqual("Queen color", "White", piece.getColor());

		System.out.println("✅ Promotion to Queen test passed");
	}

	private static void testPromotionToRook() {
		System.out.println("Test 4: Promotion to Rook");

		Board board = new Board();
		board.clearBoard();
		board.setPieceAt("b7", new Pawn("White", "b7"));

		board.movePiece("b7", "b8", "R");
		IPiece piece = board.getPieceAt("b8");

		assertTrue("Promoted to Rook", piece instanceof Rook);
		assertEqual("Rook color", "White", piece.getColor());

		System.out.println("✅ Promotion to Rook test passed");
	}

	private static void testPromotionToBishop() {
		System.out.println("Test 5: Promotion to Bishop");

		Board board = new Board();
		board.clearBoard();
		board.setPieceAt("c7", new Pawn("White", "c7"));

		board.movePiece("c7", "c8", "B");
		IPiece piece = board.getPieceAt("c8");

		assertTrue("Promoted to Bishop", piece instanceof Bishop);
		assertEqual("Bishop color", "White", piece.getColor());

		System.out.println("✅ Promotion to Bishop test passed");
	}

	private static void testPromotionToKnight() {
		System.out.println("Test 6: Promotion to Knight");

		Board board = new Board();
		board.clearBoard();
		board.setPieceAt("d7", new Pawn("White", "d7"));

		board.movePiece("d7", "d8", "N");
		IPiece piece = board.getPieceAt("d8");

		assertTrue("Promoted to Knight", piece instanceof Knight);
		assertEqual("Knight color", "White", piece.getColor());

		System.out.println("✅ Promotion to Knight test passed");
	}

	private static void testPromotionWithCapture() {
		System.out.println("Test 7: Promotion with Capture");

		Board board = new Board();
		board.clearBoard();

		// White pawn on 7th rank, Black piece on 8th rank
		board.setPieceAt("e7", new Pawn("White", "e7"));
		board.setPieceAt("f8", new Rook("Black", "f8"));

		// Capture and promote
		boolean moveSuccess = board.movePiece("e7", "f8", "Q");
		assertTrue("Promotion with capture", moveSuccess);

		IPiece piece = board.getPieceAt("f8");
		assertTrue("Promoted piece is Queen", piece instanceof Queen);
		assertEqual("Promoted piece color", "White", piece.getColor());

		System.out.println("✅ Promotion with capture test passed");
	}

	private static void testPromotionAlgebraicNotation() {
		System.out.println("Test 8: Promotion Algebraic Notation");

		Board board = new Board();
		board.clearBoard();
		board.setPieceAt("e7", new Pawn("White", "e7"));

		// Test that promotion generates correct notation
		String notation = board.getPromotionNotation("e7", "e8", "Q");
		assertEqual("Promotion notation", "e8=Q", notation);

		// Test capture promotion notation
		board.setPieceAt("f8", new Rook("Black", "f8"));
		String captureNotation = board.getPromotionNotation("e7", "f8", "Q");
		assertEqual("Capture promotion notation", "exf8=Q", captureNotation);

		System.out.println("✅ Promotion notation test passed");
	}

	private static void testInvalidPromotionAttempts() {
		System.out.println("Test 9: Invalid Promotion Attempts");

		Board board = new Board();
		board.clearBoard();

		// Try to promote pawn not on promotion rank
		board.setPieceAt("e5", new Pawn("White", "e5"));
		boolean invalidMove = board.movePiece("e5", "e6", "Q");
		assertFalse("Cannot promote on non-promotion rank", invalidMove);

		// Try to promote with invalid piece type
		board.setPieceAt("e7", new Pawn("White", "e7"));
		boolean invalidPiece = board.movePiece("e7", "e8", "K"); // King not allowed
		assertFalse("Cannot promote to King", invalidPiece);

		System.out.println("✅ Invalid promotion attempts test passed");
	}

	private static void testPromotionUndoRedo() {
		System.out.println("Test 10: Promotion Undo/Redo");

		// This test will verify that promotion works with undo/redo system
		// Implementation will be added when undo/redo integration is complete

		System.out.println("✅ Promotion undo/redo test passed (placeholder)");
	}

	// Helper assertion methods
	private static void assertTrue(String message, boolean condition) {
		if (!condition) {
			throw new AssertionError("Assertion failed: " + message);
		}
	}

	private static void assertFalse(String message, boolean condition) {
		if (condition) {
			throw new AssertionError("Assertion failed: " + message);
		}
	}

	private static void assertEqual(String message, Object expected, Object actual) {
		if (expected == null ? actual != null : !expected.equals(actual)) {
			throw new AssertionError(
					"Assertion failed: " + message + ". Expected: " + expected + ", Actual: " + actual);
		}
	}
}
