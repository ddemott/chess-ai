package com.ddemott.chessai;

import com.ddemott.chessai.pieces.*;

/**
 * Comprehensive test suite for castling functionality
 */
public class CastlingTest {

	public static void main(String[] args) {
		System.out.println("=== Castling Test Suite ===");

		int totalTests = 0;
		int passedTests = 0;

		// Test 1: Initial castling availability
		System.out.println("Test 1: Initial Castling Availability");
		if (testInitialCastlingAvailability()) {
			System.out.println("✓ Initial castling availability test passed");
			passedTests++;
		} else {
			System.out.println("✗ Initial castling availability test failed");
		}
		totalTests++;

		// Test 2: Kingside castling execution
		System.out.println("\nTest 2: Kingside Castling Execution");
		if (testKingsideCastling()) {
			System.out.println("✓ Kingside castling execution test passed");
			passedTests++;
		} else {
			System.out.println("✗ Kingside castling execution test failed");
		}
		totalTests++;

		// Test 3: Queenside castling execution
		System.out.println("\nTest 3: Queenside Castling Execution");
		if (testQueensideCastling()) {
			System.out.println("✓ Queenside castling execution test passed");
			passedTests++;
		} else {
			System.out.println("✗ Queenside castling execution test failed");
		}
		totalTests++;

		// Test 4: Castling blocked by pieces
		System.out.println("\nTest 4: Castling Blocked by Pieces");
		if (testCastlingBlockedByPieces()) {
			System.out.println("✓ Castling blocked by pieces test passed");
			passedTests++;
		} else {
			System.out.println("✗ Castling blocked by pieces test failed");
		}
		totalTests++;

		// Test 5: Castling after king has moved
		System.out.println("\nTest 5: Castling After King Has Moved");
		if (testCastlingAfterKingMoved()) {
			System.out.println("✓ Castling after king moved test passed");
			passedTests++;
		} else {
			System.out.println("✗ Castling after king moved test failed");
		}
		totalTests++;

		// Test 6: Castling after rook has moved
		System.out.println("\nTest 6: Castling After Rook Has Moved");
		if (testCastlingAfterRookMoved()) {
			System.out.println("✓ Castling after rook moved test passed");
			passedTests++;
		} else {
			System.out.println("✗ Castling after rook moved test failed");
		}
		totalTests++;

		// Test 7: Castling while in check
		System.out.println("\nTest 7: Castling While in Check");
		if (testCastlingWhileInCheck()) {
			System.out.println("✓ Castling while in check test passed");
			passedTests++;
		} else {
			System.out.println("✗ Castling while in check test failed");
		}
		totalTests++;

		// Test 8: Castling through attacked square
		System.out.println("\nTest 8: Castling Through Attacked Square");
		if (testCastlingThroughAttackedSquare()) {
			System.out.println("✓ Castling through attacked square test passed");
			passedTests++;
		} else {
			System.out.println("✗ Castling through attacked square test failed");
		}
		totalTests++;

		// Test 9: Both colors can castle
		System.out.println("\nTest 9: Both Colors Can Castle");
		if (testBothColorsCanCastle()) {
			System.out.println("✓ Both colors can castle test passed");
			passedTests++;
		} else {
			System.out.println("✗ Both colors can castle test failed");
		}
		totalTests++;

		// Summary
		System.out.println("\n=== Test Summary ===");
		System.out.println("Tests Passed: " + passedTests + "/" + totalTests);
		if (passedTests == totalTests) {
			System.out.println("✓ ALL CASTLING TESTS PASSED!");
		} else {
			System.out.println("⚠ Some castling tests failed - review implementation");
		}
	}

	private static boolean testInitialCastlingAvailability() {
		try {
			Board board = new Board();

			// Clear pieces between king and rooks for white
			board.setPieceAt("b1", null); // knight
			board.setPieceAt("c1", null); // bishop
			board.setPieceAt("d1", null); // queen
			board.setPieceAt("f1", null); // bishop
			board.setPieceAt("g1", null); // knight

			IPiece whiteKing = board.getPieceAt("e1");

			// Check that king hasn't moved initially
			boolean kingNotMoved = !whiteKing.hasMoved();

			// Check that castling moves are possible
			boolean kingsidePossible = whiteKing.isValidMove("g1", board);
			boolean queensidePossible = whiteKing.isValidMove("c1", board);

			return kingNotMoved && kingsidePossible && queensidePossible;
		} catch (Exception e) {
			System.out.println("  Error: " + e.getMessage());
			return false;
		}
	}

	private static boolean testKingsideCastling() {
		try {
			Board board = new Board();

			// Clear pieces for kingside castling
			board.setPieceAt("f1", null);
			board.setPieceAt("g1", null);

			// Execute kingside castling
			boolean castlingSuccessful = board.movePiece("e1", "g1");

			// Check final positions
			IPiece kingAtG1 = board.getPieceAt("g1");
			IPiece rookAtF1 = board.getPieceAt("f1");
			IPiece nothingAtE1 = board.getPieceAt("e1");
			IPiece nothingAtH1 = board.getPieceAt("h1");

			boolean correctKingPosition = kingAtG1 != null && kingAtG1 instanceof King
					&& kingAtG1.getColor().equals("White");
			boolean correctRookPosition = rookAtF1 != null && rookAtF1 instanceof Rook
					&& rookAtF1.getColor().equals("White");
			boolean kingMoved = nothingAtE1 == null;
			boolean rookMoved = nothingAtH1 == null;

			return castlingSuccessful && correctKingPosition && correctRookPosition && kingMoved && rookMoved;
		} catch (Exception e) {
			System.out.println("  Error: " + e.getMessage());
			return false;
		}
	}

	private static boolean testQueensideCastling() {
		try {
			Board board = new Board();

			// Clear pieces for queenside castling
			board.setPieceAt("b1", null);
			board.setPieceAt("c1", null);
			board.setPieceAt("d1", null);

			// Execute queenside castling
			boolean castlingSuccessful = board.movePiece("e1", "c1");

			// Check final positions
			IPiece kingAtC1 = board.getPieceAt("c1");
			IPiece rookAtD1 = board.getPieceAt("d1");
			IPiece nothingAtE1 = board.getPieceAt("e1");
			IPiece nothingAtA1 = board.getPieceAt("a1");

			boolean correctKingPosition = kingAtC1 != null && kingAtC1 instanceof King
					&& kingAtC1.getColor().equals("White");
			boolean correctRookPosition = rookAtD1 != null && rookAtD1 instanceof Rook
					&& rookAtD1.getColor().equals("White");
			boolean kingMoved = nothingAtE1 == null;
			boolean rookMoved = nothingAtA1 == null;

			return castlingSuccessful && correctKingPosition && correctRookPosition && kingMoved && rookMoved;
		} catch (Exception e) {
			System.out.println("  Error: " + e.getMessage());
			return false;
		}
	}

	private static boolean testCastlingBlockedByPieces() {
		try {
			Board board = new Board();

			// Try castling with pieces still in the way (initial position)
			boolean kingsideBlocked = !board.movePiece("e1", "g1");
			boolean queensideBlocked = !board.movePiece("e1", "c1");

			return kingsideBlocked && queensideBlocked;
		} catch (Exception e) {
			System.out.println("  Error: " + e.getMessage());
			return false;
		}
	}

	private static boolean testCastlingAfterKingMoved() {
		try {
			Board board = new Board();

			// Clear pieces
			board.setPieceAt("f1", null);
			board.setPieceAt("g1", null);

			// Move king and then move it back
			board.movePiece("e1", "f1");
			board.movePiece("f1", "e1");

			// Try to castle - should fail because king has moved
			boolean castlingFailed = !board.movePiece("e1", "g1");

			return castlingFailed;
		} catch (Exception e) {
			System.out.println("  Error: " + e.getMessage());
			return false;
		}
	}

	private static boolean testCastlingAfterRookMoved() {
		try {
			Board board = new Board();

			// Clear pieces
			board.setPieceAt("f1", null);
			board.setPieceAt("g1", null);

			// Move rook and then move it back
			board.movePiece("h1", "g1");
			board.movePiece("g1", "h1");

			// Try to castle - should fail because rook has moved
			boolean castlingFailed = !board.movePiece("e1", "g1");

			return castlingFailed;
		} catch (Exception e) {
			System.out.println("  Error: " + e.getMessage());
			return false;
		}
	}

	private static boolean testCastlingWhileInCheck() {
		try {
			Board board = new Board();

			// Clear pieces for castling
			board.setPieceAt("f1", null);
			board.setPieceAt("g1", null);

			// Clear pawn path so rook can attack king
			board.setPieceAt("e2", null); // Remove white pawn
			board.setPieceAt("e7", null); // Remove black pawn

			// Place black rook attacking the king
			board.setPieceAt("e8", new Rook("Black", "e8"));

			// Try to castle while in check - should fail
			boolean castlingFailed = !board.movePiece("e1", "g1");

			return castlingFailed;
		} catch (Exception e) {
			System.out.println("  Error: " + e.getMessage());
			return false;
		}
	}

	private static boolean testCastlingThroughAttackedSquare() {
		try {
			Board board = new Board();

			// Clear pieces for castling
			board.setPieceAt("f1", null);
			board.setPieceAt("g1", null);

			// Clear pawn path so rook can attack f1
			board.setPieceAt("f2", null); // Remove white pawn
			board.setPieceAt("f7", null); // Remove black pawn

			// Place black rook attacking f1 (square king passes through)
			board.setPieceAt("f8", new Rook("Black", "f8"));

			// Try to castle through attacked square - should fail
			boolean castlingFailed = !board.movePiece("e1", "g1");

			return castlingFailed;
		} catch (Exception e) {
			System.out.println("  Error: " + e.getMessage());
			return false;
		}
	}

	private static boolean testBothColorsCanCastle() {
		try {
			Board board = new Board();

			// Clear pieces for both sides
			// White
			board.setPieceAt("f1", null);
			board.setPieceAt("g1", null);

			// Black
			board.setPieceAt("f8", null);
			board.setPieceAt("g8", null);

			// Test white castling
			boolean whiteCastling = board.movePiece("e1", "g1");

			// Test black castling
			boolean blackCastling = board.movePiece("e8", "g8");

			// Check positions
			IPiece whiteKingAtG1 = board.getPieceAt("g1");
			IPiece blackKingAtG8 = board.getPieceAt("g8");

			boolean whiteKingCorrect = whiteKingAtG1 != null && whiteKingAtG1 instanceof King
					&& whiteKingAtG1.getColor().equals("White");
			boolean blackKingCorrect = blackKingAtG8 != null && blackKingAtG8 instanceof King
					&& blackKingAtG8.getColor().equals("Black");

			return whiteCastling && blackCastling && whiteKingCorrect && blackKingCorrect;
		} catch (Exception e) {
			System.out.println("  Error: " + e.getMessage());
			return false;
		}
	}
}
