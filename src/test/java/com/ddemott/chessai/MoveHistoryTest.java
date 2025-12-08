package com.ddemott.chessai;

import com.ddemott.chessai.engine.GameEngine;

/**
 * Test class to verify the Move History & Notation functionality
 */
public class MoveHistoryTest {

	public static void main(String[] args) {
		System.out.println("=== Testing Move History & Notation ===\n");

		GameEngine engine = new GameEngine(3);

		// Test 1: Initial state
		System.out.println("Test 1: Initial game state");
		System.out.println("Move count: " + engine.getMoveHistory().getMoveCount());
		System.out.println("Can undo: " + engine.canUndo());
		System.out.println("Can redo: " + engine.canRedo());
		System.out.println();

		// Test 2: Make some moves and check history
		System.out.println("Test 2: Making moves and checking history");
		System.out.println("Making move: e2 e4");
		engine.movePiece("e2", "e4");

		System.out.println("Making AI move...");
		engine.makeAIMove();

		System.out.println("Making move: g1 f3");
		engine.movePiece("g1", "f3");

		System.out.println("Making AI move...");
		engine.makeAIMove();

		System.out.println("\nMove History:");
		System.out.println(engine.getMoveListDisplay());

		Move lastMove = engine.getLastMove();
		if (lastMove != null) {
			System.out.println("Last move: " + lastMove.getAlgebraicNotation());
			System.out.println("From: " + lastMove.getFrom() + " To: " + lastMove.getTo());
			System.out.println("Player: " + lastMove.getPlayerColor());
		}

		// Test 3: Test undo functionality
		System.out.println("\nTest 3: Testing undo functionality");
		System.out.println("Moves before undo: " + engine.getMoveHistory().getMoveCount());
		System.out.println("Can undo: " + engine.canUndo());

		if (engine.canUndo()) {
			engine.undoLastMove();
			System.out.println("After one undo - moves: " + engine.getMoveHistory().getMoveCount());
			System.out.println("Current turn: " + engine.getCurrentTurn());
		}

		// Test 4: Test redo functionality
		System.out.println("\nTest 4: Testing redo functionality");
		System.out.println("Can redo: " + engine.canRedo());

		if (engine.canRedo()) {
			engine.redoLastMove();
			System.out.println("After redo - moves: " + engine.getMoveHistory().getMoveCount());
			System.out.println("Current turn: " + engine.getCurrentTurn());
		}

		// Test 5: Export PGN
		System.out.println("\nTest 5: PGN Export");
		String pgn = engine.exportGameToPGN("TestPlayer", "ChessAI", "*");
		System.out.println("PGN Export:");
		System.out.println(pgn);

		// Test 6: Save to file
		System.out.println("\nTest 6: Save to PGN file");
		boolean saved = engine.saveGameToPGNFile("test_game.pgn", "TestPlayer", "ChessAI", "*");
		System.out.println("Saved to file: " + saved);

		System.out.println("\n=== All tests completed! ===");
	}
}
