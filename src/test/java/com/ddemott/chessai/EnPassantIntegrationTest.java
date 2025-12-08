package com.ddemott.chessai;

import com.ddemott.chessai.engine.GameEngine;

/**
 * Integration test showing en passant in a real game scenario
 */
public class EnPassantIntegrationTest {

	public static void main(String[] args) {
		System.out.println("=== En Passant Integration Test ===");

		// Create a game engine
		GameEngine engine = new GameEngine(3);

		System.out.println("Initial board:");
		System.out.println(engine.getBoardRepresentation());

		// Play some moves to set up en passant scenario
		System.out.println("Setting up en passant scenario...");

		// Move white pawn e2-e4
		System.out.println("White plays: e2 e4");
		engine.movePiece("e2", "e4");
		System.out.println(engine.getBoardRepresentation());

		// Move black pawn somewhere else
		System.out.println("Black plays: a7 a6");
		engine.movePiece("a7", "a6");
		System.out.println(engine.getBoardRepresentation());

		// Move white pawn e4-e5
		System.out.println("White plays: e4 e5");
		engine.movePiece("e4", "e5");
		System.out.println(engine.getBoardRepresentation());

		// Move black pawn f7-f5 (creating en passant opportunity)
		System.out.println("Black plays: f7 f5 (two-square pawn move - creates en passant opportunity)");
		boolean pawnTwoSquareMove = engine.movePiece("f7", "f5");
		System.out.println("Move successful: " + pawnTwoSquareMove);
		System.out.println(engine.getBoardRepresentation());

		// Check en passant target
		Board board = engine.getGameState().getBoard();
		System.out.println("En passant target: " + board.getEnPassantTarget());

		// White captures en passant
		System.out.println("White captures en passant: e5 f6");
		boolean enPassantCapture = engine.movePiece("e5", "f6");
		System.out.println("En passant capture successful: " + enPassantCapture);

		if (enPassantCapture) {
			System.out.println("Board after en passant capture:");
			System.out.println(engine.getBoardRepresentation());
		}

		// Verify final positions
		System.out.println("=== Verification ===");
		Board finalBoard = engine.getGameState().getBoard();
		boolean whitePawnAtF6 = finalBoard.getPieceAt("f6") != null
				&& finalBoard.getPieceAt("f6").getClass().getSimpleName().equals("Pawn")
				&& finalBoard.getPieceAt("f6").getColor().equals("White");
		boolean blackPawnRemoved = finalBoard.getPieceAt("f5") == null;
		boolean enPassantTargetCleared = finalBoard.getEnPassantTarget() == null;

		System.out.println("White pawn at f6: " + whitePawnAtF6);
		System.out.println("Black pawn removed from f5: " + blackPawnRemoved);
		System.out.println("En passant target cleared: " + enPassantTargetCleared);

		if (enPassantCapture && whitePawnAtF6 && blackPawnRemoved && enPassantTargetCleared) {
			System.out.println("✓ EN PASSANT INTEGRATION TEST PASSED!");
		} else {
			System.out.println("✗ En passant integration test failed");
		}
	}
}
