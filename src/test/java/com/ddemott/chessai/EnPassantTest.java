package com.ddemott.chessai;

import com.ddemott.chessai.pieces.Pawn;

/**
 * Comprehensive test suite for En Passant chess rule implementation
 */
public class EnPassantTest {

	public static void main(String[] args) {
		System.out.println("=== En Passant Test Suite ===");

		int testsPassedCount = 0;
		int totalTests = 8;

		// Test 1: Basic en passant setup
		System.out.println("\nTest 1: En Passant Setup After Pawn Two-Square Move");
		if (testEnPassantSetup()) {
			System.out.println("✓ En passant setup test passed");
			testsPassedCount++;
		} else {
			System.out.println("✗ En passant setup test failed");
		}

		// Test 2: En passant capture execution (White captures Black)
		System.out.println("\nTest 2: White En Passant Capture");
		if (testWhiteEnPassantCapture()) {
			System.out.println("✓ White en passant capture test passed");
			testsPassedCount++;
		} else {
			System.out.println("✗ White en passant capture test failed");
		}

		// Test 3: En passant capture execution (Black captures White)
		System.out.println("\nTest 3: Black En Passant Capture");
		if (testBlackEnPassantCapture()) {
			System.out.println("✓ Black en passant capture test passed");
			testsPassedCount++;
		} else {
			System.out.println("✗ Black en passant capture test failed");
		}

		// Test 4: En passant target clearing after non-pawn move
		System.out.println("\nTest 4: En Passant Target Clearing");
		if (testEnPassantTargetClearing()) {
			System.out.println("✓ En passant target clearing test passed");
			testsPassedCount++;
		} else {
			System.out.println("✗ En passant target clearing test failed");
		}

		// Test 5: Invalid en passant (no target)
		System.out.println("\nTest 5: Invalid En Passant (No Target)");
		if (testInvalidEnPassantNoTarget()) {
			System.out.println("✓ Invalid en passant (no target) test passed");
			testsPassedCount++;
		} else {
			System.out.println("✗ Invalid en passant (no target) test failed");
		}

		// Test 6: Invalid en passant (wrong timing)
		System.out.println("\nTest 6: Invalid En Passant (Wrong Timing)");
		if (testInvalidEnPassantWrongTiming()) {
			System.out.println("✓ Invalid en passant (wrong timing) test passed");
			testsPassedCount++;
		} else {
			System.out.println("✗ Invalid en passant (wrong timing) test failed");
		}

		// Test 7: Multiple pawns adjacent scenario
		System.out.println("\nTest 7: Multiple Adjacent Pawns En Passant");
		if (testMultipleAdjacentPawns()) {
			System.out.println("✓ Multiple adjacent pawns test passed");
			testsPassedCount++;
		} else {
			System.out.println("✗ Multiple adjacent pawns test failed");
		}

		// Test 8: En passant with pawn promotion potential
		System.out.println("\nTest 8: En Passant Various File Positions");
		if (testEnPassantVariousFiles()) {
			System.out.println("✓ En passant various files test passed");
			testsPassedCount++;
		} else {
			System.out.println("✗ En passant various files test failed");
		}

		// Summary
		System.out.println("\n=== Test Summary ===");
		System.out.println("Tests Passed: " + testsPassedCount + "/" + totalTests);
		if (testsPassedCount == totalTests) {
			System.out.println("✓ ALL EN PASSANT TESTS PASSED!");
		} else {
			System.out.println("✗ Some tests failed");
		}
	}

	private static boolean testEnPassantSetup() {
		try {
			Board board = new Board();

			// Clear some pieces to make space
			board.setPieceAt("e2", null);
			board.setPieceAt("d7", null);

			// Place white pawn at e4
			board.setPieceAt("e4", new Pawn("White", "e4"));

			// Place black pawn at d7
			board.setPieceAt("d7", new Pawn("Black", "d7"));

			// Black pawn moves two squares: d7 to d5
			boolean moveSuccess = board.movePiece("d7", "d5");

			// Check that en passant target is set to d6
			String enPassantTarget = board.getEnPassantTarget();

			return moveSuccess && "d6".equals(enPassantTarget);
		} catch (Exception e) {
			System.out.println("  Error: " + e.getMessage());
			return false;
		}
	}

	private static boolean testWhiteEnPassantCapture() {
		try {
			Board board = new Board();

			// Clear pieces
			board.setPieceAt("e2", null);
			board.setPieceAt("d7", null);

			// Set up en passant scenario
			board.setPieceAt("e5", new Pawn("White", "e5"));
			board.setPieceAt("d7", new Pawn("Black", "d7"));

			// Black pawn moves two squares, creating en passant opportunity
			board.movePiece("d7", "d5");

			// White pawn captures en passant
			boolean captureSuccess = board.movePiece("e5", "d6");

			// Verify results
			boolean whitePawnAtD6 = board.getPieceAt("d6") != null
					&& board.getPieceAt("d6").getClass().getSimpleName().equals("Pawn")
					&& board.getPieceAt("d6").getColor().equals("White");
			boolean blackPawnRemoved = board.getPieceAt("d5") == null;
			boolean enPassantTargetCleared = board.getEnPassantTarget() == null;

			return captureSuccess && whitePawnAtD6 && blackPawnRemoved && enPassantTargetCleared;
		} catch (Exception e) {
			System.out.println("  Error: " + e.getMessage());
			return false;
		}
	}

	private static boolean testBlackEnPassantCapture() {
		try {
			Board board = new Board();

			// Clear pieces
			board.setPieceAt("e2", null);
			board.setPieceAt("f7", null);

			// Set up en passant scenario
			board.setPieceAt("f4", new Pawn("Black", "f4"));
			board.setPieceAt("e2", new Pawn("White", "e2"));

			// White pawn moves two squares, creating en passant opportunity
			board.movePiece("e2", "e4");

			// Black pawn captures en passant
			boolean captureSuccess = board.movePiece("f4", "e3");

			// Verify results
			boolean blackPawnAtE3 = board.getPieceAt("e3") != null
					&& board.getPieceAt("e3").getClass().getSimpleName().equals("Pawn")
					&& board.getPieceAt("e3").getColor().equals("Black");
			boolean whitePawnRemoved = board.getPieceAt("e4") == null;
			boolean enPassantTargetCleared = board.getEnPassantTarget() == null;

			return captureSuccess && blackPawnAtE3 && whitePawnRemoved && enPassantTargetCleared;
		} catch (Exception e) {
			System.out.println("  Error: " + e.getMessage());
			return false;
		}
	}

	private static boolean testEnPassantTargetClearing() {
		try {
			Board board = new Board();

			// Set up en passant
			board.setPieceAt("e2", null);
			board.setPieceAt("d7", null);
			board.setPieceAt("d7", new Pawn("Black", "d7"));

			// Create en passant opportunity
			board.movePiece("d7", "d5");
			boolean targetSet = "d6".equals(board.getEnPassantTarget());

			// Make a different move (not en passant)
			board.movePiece("g1", "f3"); // Knight move
			boolean targetCleared = board.getEnPassantTarget() == null;

			return targetSet && targetCleared;
		} catch (Exception e) {
			System.out.println("  Error: " + e.getMessage());
			return false;
		}
	}

	private static boolean testInvalidEnPassantNoTarget() {
		try {
			Board board = new Board();

			// Set up pawns without en passant opportunity
			board.setPieceAt("e2", null);
			board.setPieceAt("d7", null);
			board.setPieceAt("e4", new Pawn("White", "e4"));

			// Try to capture diagonally to an empty square when no en passant target exists
			boolean invalidCaptureBlocked = !board.movePiece("e4", "d5");

			return invalidCaptureBlocked;
		} catch (Exception e) {
			System.out.println("  Error: " + e.getMessage());
			return false;
		}
	}

	private static boolean testInvalidEnPassantWrongTiming() {
		try {
			Board board = new Board();

			// Set up scenario
			board.setPieceAt("e2", null);
			board.setPieceAt("d7", null);
			board.setPieceAt("e5", new Pawn("White", "e5"));
			board.setPieceAt("d7", new Pawn("Black", "d7"));

			// Black pawn moves two squares
			board.movePiece("d7", "d5");

			// Make another move to clear en passant target
			board.movePiece("b1", "c3"); // Knight move

			// Try to capture en passant after it's no longer valid
			boolean invalidCaptureBlocked = !board.movePiece("e5", "d6");

			return invalidCaptureBlocked;
		} catch (Exception e) {
			System.out.println("  Error: " + e.getMessage());
			return false;
		}
	}

	private static boolean testMultipleAdjacentPawns() {
		try {
			Board board = new Board();

			// Clear pieces
			board.setPieceAt("e2", null);
			board.setPieceAt("f2", null);
			board.setPieceAt("d7", null);

			// Set up multiple white pawns adjacent to potential en passant target
			board.setPieceAt("e5", new Pawn("White", "e5"));
			board.setPieceAt("c5", new Pawn("White", "c5"));
			board.setPieceAt("d7", new Pawn("Black", "d7"));

			// Black pawn moves two squares
			board.movePiece("d7", "d5");

			// Both adjacent white pawns should be able to capture en passant
			Board boardCopy1 = board.clone();
			Board boardCopy2 = board.clone();

			boolean leftPawnCanCapture = boardCopy1.movePiece("c5", "d6");
			boolean rightPawnCanCapture = boardCopy2.movePiece("e5", "d6");

			return leftPawnCanCapture && rightPawnCanCapture;
		} catch (Exception e) {
			System.out.println("  Error: " + e.getMessage());
			return false;
		}
	}

	private static boolean testEnPassantVariousFiles() {
		try {
			// Test en passant on different files (a, h files - edge cases)
			Board board = new Board();

			// Test on a-file
			board.setPieceAt("a2", null);
			board.setPieceAt("b7", null);
			board.setPieceAt("b5", new Pawn("White", "b5"));
			board.setPieceAt("a7", new Pawn("Black", "a7"));

			// Black pawn moves two squares
			board.movePiece("a7", "a5");
			boolean aFileEnPassant = board.movePiece("b5", "a6");

			// Test on h-file
			Board board2 = new Board();
			board2.setPieceAt("h2", null);
			board2.setPieceAt("g7", null);
			board2.setPieceAt("g5", new Pawn("White", "g5"));
			board2.setPieceAt("h7", new Pawn("Black", "h7"));

			// Black pawn moves two squares
			board2.movePiece("h7", "h5");
			boolean hFileEnPassant = board2.movePiece("g5", "h6");

			return aFileEnPassant && hFileEnPassant;
		} catch (Exception e) {
			System.out.println("  Error: " + e.getMessage());
			return false;
		}
	}
}
