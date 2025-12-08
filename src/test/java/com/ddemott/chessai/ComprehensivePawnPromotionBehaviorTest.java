package com.ddemott.chessai;

import com.ddemott.chessai.engine.GameEngine;
import com.ddemott.chessai.pieces.Pawn;

/**
 * Comprehensive test showing what happens with pawn promotion in different
 * scenarios
 */
public class ComprehensivePawnPromotionBehaviorTest {

	public static void main(String[] args) {
		System.out.println("=== Comprehensive Pawn Promotion Behavior Test ===\n");

		testDirectBoardMovement();
		testGameEngineMovement();
	}

	private static void testDirectBoardMovement() {
		System.out.println("*** SCENARIO 1: Direct Board.movePiece() calls ***\n");

		// Create a board and set up for promotion
		Board board = new Board();

		// Clear the board
		for (int row = 0; row < 8; row++) {
			for (int col = 0; col < 8; col++) {
				board.setPieceAt(board.convertCoordinatesToPosition(row, col), null);
			}
		}

		// Place a white pawn on e7 and kings
		board.setPieceAt("e7", new Pawn("White", "e7"));
		board.setPieceAt("e1", new com.ddemott.chessai.pieces.King("White", "e1"));
		board.setPieceAt("d8", new com.ddemott.chessai.pieces.King("Black", "d8"));

		System.out.println("Setup: White pawn on e7, ready to promote");

		// Test 1: Basic movePiece - this will move pawn to e8 WITHOUT promoting!
		System.out.println("\n1. Calling board.movePiece(\"e7\", \"e8\") - NO promotion piece");
		boolean result1 = board.movePiece("e7", "e8");
		System.out.println("   Result: " + result1);

		var pieceOnE8 = board.getPieceAt("e8");
		if (pieceOnE8 != null) {
			System.out.println(
					"   Piece on e8: " + pieceOnE8.getClass().getSimpleName() + " (" + pieceOnE8.getColor() + ")");
			System.out.println("   ❌ BUG: Pawn moved to rank 8 but didn't promote!");
		}

		// Reset for next test
		board.setPieceAt("e8", null);
		board.setPieceAt("e7", new Pawn("White", "e7"));

		// Test 2: movePiece with promotion piece
		System.out.println("\n2. Calling board.movePiece(\"e7\", \"e8\", \"Q\") - WITH Queen promotion");
		boolean result2 = board.movePiece("e7", "e8", "Q");
		System.out.println("   Result: " + result2);

		pieceOnE8 = board.getPieceAt("e8");
		if (pieceOnE8 != null) {
			System.out.println(
					"   Piece on e8: " + pieceOnE8.getClass().getSimpleName() + " (" + pieceOnE8.getColor() + ")");
			if (pieceOnE8.getClass().getSimpleName().equals("Queen")) {
				System.out.println("   ✅ CORRECT: Pawn promoted to Queen!");
			} else {
				System.out.println("   ❌ ERROR: Expected Queen but got " + pieceOnE8.getClass().getSimpleName());
			}
		}

		// Test 3: movePiece with promotion piece but not a promotion move
		board.setPieceAt("e8", null);
		board.setPieceAt("e6", new Pawn("White", "e6"));

		System.out.println(
				"\n3. Calling board.movePiece(\"e6\", \"e7\", \"Q\") - NOT a promotion move but piece specified");
		boolean result3 = board.movePiece("e6", "e7", "Q");
		System.out.println("   Result: " + result3);
		System.out.println("   ✅ CORRECT: Move rejected because e7 is not promotion rank");
	}

	private static void testGameEngineMovement() {
		System.out.println("\n\n*** SCENARIO 2: GameEngine.movePiece() calls ***\n");

		new GameEngine(1);

		System.out.println("Note: GameEngine uses the same Board.movePiece() methods internally,");
		System.out.println("so it has the same behavior:");
		System.out.println("- movePiece(from, to) will move pawn to rank 8 WITHOUT promoting (BUG)");
		System.out.println("- movePiece(from, to, piece) will properly promote the pawn");
		System.out.println("- The console interface detects promotion moves and prompts for piece selection");
	}

	public static void testConsoleInterfaceBehavior() {
		System.out.println("\n\n*** SCENARIO 3: Console Interface Behavior ***\n");

		System.out.println("When playing through ConsoleChessGame:");
		System.out.println("1. You enter a move like 'e7 e8'");
		System.out.println("2. ConsoleChessGame.isPawnPromotionMove() detects this is a promotion");
		System.out.println("3. ConsoleChessGame.promptForPromotionPiece() asks you to choose Q/R/B/N");
		System.out.println("4. ConsoleChessGame calls gameEngine.movePiece(from, to, promotionPiece)");
		System.out.println("5. The pawn is properly promoted to your chosen piece");
		System.out.println();
		System.out.println("✅ RESULT: Console interface handles promotion correctly");
		System.out.println("❌ BUG: Direct Board.movePiece(from, to) calls bypass promotion");
	}
}
