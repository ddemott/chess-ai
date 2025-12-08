package com.ddemott.chessai;

import com.ddemott.chessai.pieces.Pawn;

/**
 * Direct test of pawn promotion behavior without specifying promotion piece
 */
public class DirectPawnPromotionTest {

	public static void main(String[] args) {
		System.out.println("=== Direct Test: Pawn Promotion Without Piece Specification ===\n");

		// Create a board and manually place a white pawn on the 7th rank
		Board board = new Board();

		// Clear the board first
		for (int row = 0; row < 8; row++) {
			for (int col = 0; col < 8; col++) {
				board.setPieceAt(board.convertCoordinatesToPosition(row, col), null);
			}
		}

		// Place a white pawn on e7 (ready to promote)
		Pawn whitePawn = new Pawn("White", "e7");
		board.setPieceAt("e7", whitePawn);

		// Place kings to avoid issues (not on promotion squares)
		board.setPieceAt("e1", new com.ddemott.chessai.pieces.King("White", "e1"));
		board.setPieceAt("d8", new com.ddemott.chessai.pieces.King("Black", "d8"));

		System.out.println("Test setup - White pawn on e7, ready to promote:");
		System.out.println(board.toString());

		// Test 1: Try to move pawn to e8 WITHOUT specifying promotion piece
		System.out.println("\n*** TEST 1: Moving e7 to e8 WITHOUT promotion piece specification ***");
		boolean result1 = board.movePiece("e7", "e8");
		System.out.println("Move result: " + result1);

		if (!result1) {
			System.out.println("✅ CORRECT: Move was rejected - pawn still on e7");
		} else {
			System.out.println("❌ UNEXPECTED: Move succeeded without promotion piece!");
		}

		System.out.println("\nBoard after test 1:");
		System.out.println(board.toString());

		// Test 2: Try to move pawn to e8 WITH promotion piece specification
		System.out.println("\n*** TEST 2: Moving e7 to e8 WITH Queen promotion ***");
		boolean result2 = board.movePiece("e7", "e8", "Q");
		System.out.println("Move result: " + result2);

		if (result2) {
			System.out.println("✅ CORRECT: Move succeeded with promotion piece specified");
		} else {
			System.out.println("❌ UNEXPECTED: Move failed even with promotion piece!");
		}

		System.out.println("\nBoard after test 2:");
		System.out.println(board.toString());

		// Check what piece is on e8 now
		var pieceOnE8 = board.getPieceAt("e8");
		if (pieceOnE8 != null) {
			System.out.println(
					"Piece on e8: " + pieceOnE8.getClass().getSimpleName() + " (" + pieceOnE8.getColor() + ")");
		} else {
			System.out.println("No piece on e8");
		}

		System.out.println("\n=== CONCLUSION ===");
		System.out.println("When moving a pawn to rank 8 (promotion rank):");
		System.out.println("1. WITHOUT specifying promotion piece → Move is REJECTED");
		System.out.println("2. WITH specifying promotion piece → Move succeeds and pawn transforms");
		System.out.println("3. You MUST use the console interface or GameEngine promotion methods");
	}
}
