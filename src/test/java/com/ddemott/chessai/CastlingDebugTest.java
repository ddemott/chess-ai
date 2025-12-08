package com.ddemott.chessai;

import com.ddemott.chessai.pieces.*;

/**
 * Debug specific castling test cases
 */
public class CastlingDebugTest {

	public static void main(String[] args) {
		System.out.println("=== Castling Debug Test ===");

		// Debug Test 7: Castling while in check
		System.out.println("Debug Test 7: Castling While in Check");
		debugCastlingWhileInCheck();

		System.out.println("\nDebug Test 8: Castling Through Attacked Square");
		debugCastlingThroughAttackedSquare();
	}

	private static void debugCastlingWhileInCheck() {
		try {
			Board board = new Board();

			// Clear pieces for castling
			board.setPieceAt("f1", null);
			board.setPieceAt("g1", null);

			// *** KEY FIX: Need to clear the pawn path for attack to work ***
			board.setPieceAt("e2", null); // Remove white pawn
			board.setPieceAt("e7", null); // Remove black pawn

			// Place black rook attacking the king
			board.setPieceAt("e8", new Rook("Black", "e8"));

			System.out.println("  Board state after setup:");
			System.out.println("  White King at e1: " + board.getPieceAt("e1"));
			System.out.println("  Black Rook at e8: " + board.getPieceAt("e8"));
			System.out.println("  Pawn at e2: " + board.getPieceAt("e2"));
			System.out.println("  Pawn at e7: " + board.getPieceAt("e7"));

			// Check if king is in check
			boolean kingInCheck = board.isKingInCheck("White");
			System.out.println("  Is White king in check? " + kingInCheck);

			// Check if e1 is under attack
			boolean e1UnderAttack = board.isSquareUnderAttack("e1", "White");
			System.out.println("  Is e1 under attack? " + e1UnderAttack);

			// Try to castle while in check - should fail
			boolean castlingAttempted = board.movePiece("e1", "g1");
			System.out.println("  Castling attempt result: " + castlingAttempted);
			System.out.println("  Expected: false (should fail)");

		} catch (Exception e) {
			System.out.println("  Error: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private static void debugCastlingThroughAttackedSquare() {
		try {
			Board board = new Board();

			// Clear pieces for castling
			board.setPieceAt("f1", null);
			board.setPieceAt("g1", null);

			// *** KEY FIX: Need to clear the pawn path for attack to work ***
			board.setPieceAt("f2", null); // Remove white pawn
			board.setPieceAt("f7", null); // Remove black pawn

			// Place black rook attacking f1 (square king passes through)
			board.setPieceAt("f8", new Rook("Black", "f8"));

			System.out.println("  Board state after setup:");
			System.out.println("  White King at e1: " + board.getPieceAt("e1"));
			System.out.println("  Black Rook at f8: " + board.getPieceAt("f8"));
			System.out.println("  Pawn at f2: " + board.getPieceAt("f2"));
			System.out.println("  Pawn at f7: " + board.getPieceAt("f7"));

			// Check if f1 is under attack
			boolean f1UnderAttack = board.isSquareUnderAttack("f1", "White");
			System.out.println("  Is f1 under attack? " + f1UnderAttack);

			// Check if g1 is under attack
			boolean g1UnderAttack = board.isSquareUnderAttack("g1", "White");
			System.out.println("  Is g1 under attack? " + g1UnderAttack);

			// Try to castle through attacked square - should fail
			boolean castlingAttempted = board.movePiece("e1", "g1");
			System.out.println("  Castling attempt result: " + castlingAttempted);
			System.out.println("  Expected: false (should fail)");

		} catch (Exception e) {
			System.out.println("  Error: " + e.getMessage());
			e.printStackTrace();
		}
	}
}
