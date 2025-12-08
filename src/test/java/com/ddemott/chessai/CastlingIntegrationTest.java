package com.ddemott.chessai;

import com.ddemott.chessai.engine.GameEngine;

/**
 * Integration test showing castling in a real game scenario
 */
public class CastlingIntegrationTest {

	public static void main(String[] args) {
		System.out.println("=== Castling Integration Test ===");

		// Create a game engine
		GameEngine engine = new GameEngine(3);

		System.out.println("Initial board:");
		System.out.println(engine.getBoardRepresentation());

		// Clear pieces to allow castling
		System.out.println("Clearing pieces for castling...");

		// Use the game engine's board to directly clear pieces (simulating moves that
		// would clear the path)
		// In a real game, these pieces would be moved by actual gameplay

		// Alternative approach: Get the board and clear the pieces directly for
		// demonstration
		Board board = engine.getGameState().getBoard();
		board.setPieceAt("f1", null); // Clear bishop
		board.setPieceAt("g1", null); // Clear knight

		System.out.println("After clearing pieces:");
		System.out.println(engine.getBoardRepresentation());

		// Try castling
		System.out.println("Attempting kingside castling (e1 to g1)...");
		boolean castlingSuccessful = engine.movePiece("e1", "g1");

		System.out.println("Castling successful: " + castlingSuccessful);

		if (castlingSuccessful) {
			System.out.println("Board after castling:");
			System.out.println(engine.getBoardRepresentation());
		}

		// Verify final positions
		System.out.println("=== Verification ===");
		Board finalBoard = engine.getGameState().getBoard();
		boolean kingAtG1 = finalBoard.getPieceAt("g1") != null
				&& finalBoard.getPieceAt("g1").getClass().getSimpleName().equals("King");
		boolean rookAtF1 = finalBoard.getPieceAt("f1") != null
				&& finalBoard.getPieceAt("f1").getClass().getSimpleName().equals("Rook");
		boolean nothingAtE1 = finalBoard.getPieceAt("e1") == null;
		boolean nothingAtH1 = finalBoard.getPieceAt("h1") == null;

		System.out.println("King at g1: " + kingAtG1);
		System.out.println("Rook at f1: " + rookAtF1);
		System.out.println("Nothing at e1: " + nothingAtE1);
		System.out.println("Nothing at h1: " + nothingAtH1);

		if (castlingSuccessful && kingAtG1 && rookAtF1 && nothingAtE1 && nothingAtH1) {
			System.out.println("✓ CASTLING INTEGRATION TEST PASSED!");
		} else {
			System.out.println("✗ Castling integration test failed");
		}
	}
}
