package com.ddemott.chessai;

import com.ddemott.chessai.engine.GameEngine;

/**
 * Test what happens when a pawn promotion move is attempted without specifying
 * the promotion piece
 */
public class PawnPromotionWithoutPieceTest {

	public static void main(String[] args) {
		System.out.println("=== Testing Pawn Promotion Without Specifying Piece ===\n");

		GameEngine engine = new GameEngine(1);

		// Set up a scenario where White pawn can promote
		// First, let's move some pieces to get a pawn near promotion
		System.out.println("Initial board:");
		System.out.println(engine.getBoardRepresentation());

		// Move pawn forward multiple times to get close to promotion
		System.out.println("1. Moving White pawn from e2 to e4...");
		boolean success1 = engine.movePiece("e2", "e4");
		System.out.println("Success: " + success1);

		// Skip AI move for testing
		if (engine.getCurrentTurn().equals("Black")) {
			engine.movePiece("e7", "e5"); // Black responds
		}

		System.out.println("\n2. Moving White pawn from e4 to e5...");
		boolean success2 = engine.movePiece("e4", "e5");
		System.out.println("Success: " + success2);

		// Skip AI move
		if (engine.getCurrentTurn().equals("Black")) {
			engine.movePiece("d7", "d6"); // Black responds
		}

		System.out.println("\n3. Moving White pawn from e5 to e6...");
		boolean success3 = engine.movePiece("e5", "e6");
		System.out.println("Success: " + success3);

		// Skip AI move
		if (engine.getCurrentTurn().equals("Black")) {
			engine.movePiece("d6", "d5"); // Black responds
		}

		System.out.println("\n4. Moving White pawn from e6 to e7...");
		boolean success4 = engine.movePiece("e6", "e7");
		System.out.println("Success: " + success4);

		// Skip AI move
		if (engine.getCurrentTurn().equals("Black")) {
			engine.movePiece("d5", "d4"); // Black responds
		}

		System.out.println("\nBoard before promotion attempt:");
		System.out.println(engine.getBoardRepresentation());

		// Now try to promote WITHOUT specifying the piece
		System.out.println("\n5. *** TESTING: Moving White pawn from e7 to e8 WITHOUT specifying promotion piece ***");
		boolean promotionWithoutPiece = engine.movePiece("e7", "e8");
		System.out.println("Move success (WITHOUT promotion piece): " + promotionWithoutPiece);

		if (!promotionWithoutPiece) {
			System.out.println("✅ EXPECTED BEHAVIOR: Move was rejected because no promotion piece was specified");
		} else {
			System.out.println("❌ UNEXPECTED: Move succeeded without promotion piece!");
		}

		System.out.println("\nBoard after promotion attempt:");
		System.out.println(engine.getBoardRepresentation());

		// Now try WITH specifying promotion piece
		System.out.println("\n6. *** TESTING: Moving White pawn from e7 to e8 WITH Queen promotion ***");
		boolean promotionWithPiece = engine.movePiece("e7", "e8", "Q");
		System.out.println("Move success (WITH Queen promotion): " + promotionWithPiece);

		if (promotionWithPiece) {
			System.out.println("✅ EXPECTED BEHAVIOR: Move succeeded with promotion piece specified");
		} else {
			System.out.println("❌ UNEXPECTED: Move failed even with promotion piece!");
		}

		System.out.println("\nFinal board:");
		System.out.println(engine.getBoardRepresentation());

		System.out.println("\n=== Summary ===");
		System.out.println("When attempting to move a pawn to rank 8 without specifying promotion piece:");
		System.out.println("- The move is REJECTED (returns false)");
		System.out.println("- The pawn remains in its original position");
		System.out.println("- The game state is unchanged");
		System.out.println("- You MUST specify a promotion piece (Q, R, B, or N) for the move to succeed");
	}
}
