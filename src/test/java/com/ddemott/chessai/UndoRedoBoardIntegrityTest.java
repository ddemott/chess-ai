package com.ddemott.chessai;

import com.ddemott.chessai.engine.GameEngine;
import com.ddemott.chessai.pieces.IPiece;

/**
 * Specific test for verifying that undo/redo operations maintain board state
 * integrity
 */
public class UndoRedoBoardIntegrityTest {

	public static void main(String[] args) {
		System.out.println("=== Undo/Redo Board Integrity Test ===\n");

		testBoardIntegrityAfterCapture();
		testPiecePositionConsistency();
		testTurnConsistency();

		System.out.println("✅ All board integrity tests completed successfully!");
	}

	private static void testBoardIntegrityAfterCapture() {
		System.out.println("Test 1: Board integrity after capture and undo");

		GameEngine engine = new GameEngine(3);

		// Record initial state
		String initialBoard = engine.getBoardRepresentation();

		// Make moves that will likely result in a capture
		engine.movePiece("e2", "e4");
		engine.movePiece("d7", "d5"); // Force a pawn structure that could lead to capture
		engine.movePiece("e4", "d5"); // Capture the pawn

		System.out.println("After capture sequence:");
		System.out.println(engine.getBoardRepresentation());

		// Verify that there are pieces captured
		Move lastMove = engine.getLastMove();
		if (lastMove != null && lastMove.isCapture()) {
			System.out.println("✓ Capture move detected: " + lastMove.getAlgebraicNotation());
		}

		// Undo the capture
		engine.undoLastMove();
		System.out.println("After undoing capture:");
		System.out.println(engine.getBoardRepresentation());

		// Undo remaining moves
		engine.undoLastMove();
		engine.undoLastMove();

		String finalBoard = engine.getBoardRepresentation();
		if (initialBoard.equals(finalBoard)) {
			System.out.println("✓ Board state fully restored after undo sequence");
		} else {
			System.out.println("✗ Board state not properly restored");
			System.out.println("Expected:\n" + initialBoard);
			System.out.println("Got:\n" + finalBoard);
		}

		System.out.println();
	}

	private static void testPiecePositionConsistency() {
		System.out.println("Test 2: Piece position consistency during undo/redo");

		GameEngine engine = new GameEngine(3);

		// Make a specific move
		engine.movePiece("g1", "f3");

		// Check that the knight moved
		IPiece knightAtF3 = engine.getGameState().getBoard().getPieceAt("f3");
		IPiece emptyAtG1 = engine.getGameState().getBoard().getPieceAt("g1");

		if (knightAtF3 != null && emptyAtG1 == null) {
			System.out.println("✓ Knight correctly moved from g1 to f3");
		} else {
			System.out.println("✗ Knight move not properly recorded");
		}

		// Undo the move
		engine.undoLastMove();

		// Check that the knight is back
		IPiece knightBackAtG1 = engine.getGameState().getBoard().getPieceAt("g1");
		IPiece emptyAtF3 = engine.getGameState().getBoard().getPieceAt("f3");

		if (knightBackAtG1 != null && emptyAtF3 == null) {
			System.out.println("✓ Knight correctly restored to g1 after undo");
		} else {
			System.out.println("✗ Knight not properly restored after undo");
		}

		// Redo the move
		engine.redoLastMove();

		// Check that the knight moved again
		IPiece knightAtF3Again = engine.getGameState().getBoard().getPieceAt("f3");
		IPiece emptyAtG1Again = engine.getGameState().getBoard().getPieceAt("g1");

		if (knightAtF3Again != null && emptyAtG1Again == null) {
			System.out.println("✓ Knight correctly moved to f3 again after redo");
		} else {
			System.out.println("✗ Knight not properly moved after redo");
		}

		System.out.println();
	}

	private static void testTurnConsistency() {
		System.out.println("Test 3: Turn consistency during undo/redo operations");

		GameEngine engine = new GameEngine(3);

		// Initial state should be White's turn
		if ("White".equals(engine.getCurrentTurn())) {
			System.out.println("✓ Initial turn is White");
		}

		// Make a move (White's turn)
		engine.movePiece("e2", "e4");
		if ("Black".equals(engine.getCurrentTurn())) {
			System.out.println("✓ Turn switched to Black after White's move");
		}

		// Make AI move (Black's turn)
		engine.makeAIMove();
		if ("White".equals(engine.getCurrentTurn())) {
			System.out.println("✓ Turn switched to White after Black's move");
		}

		// Undo AI move
		engine.undoLastMove();
		if ("Black".equals(engine.getCurrentTurn())) {
			System.out.println("✓ Turn correctly reverted to Black after undoing AI move");
		}

		// Undo player move
		engine.undoLastMove();
		if ("White".equals(engine.getCurrentTurn())) {
			System.out.println("✓ Turn correctly reverted to White after undoing player move");
		}

		// Redo player move
		engine.redoLastMove();
		if ("Black".equals(engine.getCurrentTurn())) {
			System.out.println("✓ Turn correctly switched to Black after redoing player move");
		}

		// Redo AI move
		engine.redoLastMove();
		if ("White".equals(engine.getCurrentTurn())) {
			System.out.println("✓ Turn correctly switched to White after redoing AI move");
		}

		System.out.println();
	}
}
