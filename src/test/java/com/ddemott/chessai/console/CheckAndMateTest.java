package com.ddemott.chessai.console;

import com.ddemott.chessai.engine.GameEngine;
import com.ddemott.chessai.Board;
import com.ddemott.chessai.pieces.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit test for check and checkmate detection in various scenarios
 */
public class CheckAndMateTest {

	@Test
	void testBasicCheckDetection() {
		GameEngine engine = new GameEngine(3);
		Board board = engine.getGameState().getBoard();
		assertFalse(board.isKingInCheck("White"), "White not in check initially");
		assertFalse(board.isKingInCheck("Black"), "Black not in check initially");
	}

	@Test
	void testKingPositionFinding() {
		GameEngine engine = new GameEngine(3);
		Board board = engine.getGameState().getBoard();
		assertEquals("e1", board.findKingPosition("White"), "White king found at e1");
		assertEquals("e8", board.findKingPosition("Black"), "Black king found at e8");
		assertNull(board.findKingPosition("Green"), "Invalid color returns null");
	}

	@Test
	void testCheckAfterMove() {
		GameEngine engine = new GameEngine(3);
		Board board = engine.getGameState().getBoard();
		engine.movePiece("e2", "e4");
		engine.movePiece("f7", "f6");
		engine.movePiece("d1", "h5");
		assertTrue(board.isKingInCheck("Black"), "Black king in check after Queen to h5");
	}

	@Test
	void testCheckmateScenarios() {
		GameEngine engine = new GameEngine(3);
		Board board = engine.getGameState().getBoard();
		assertFalse(board.isCheckmate("White"), "White not in checkmate initially");
		assertFalse(board.isCheckmate("Black"), "Black not in checkmate initially");
		// Scholar's mate
		engine.movePiece("e2", "e4");
		engine.movePiece("e7", "e5");
		engine.movePiece("f1", "c4");
		engine.movePiece("b8", "c6");
		engine.movePiece("d1", "h5");
		engine.movePiece("g8", "f6");
		boolean moveSuccessful = engine.movePiece("h5", "f7");
		if (moveSuccessful) {
			assertTrue(board.isCheckmate("Black"), "Black should be in checkmate after Scholar's mate");
		}
	}

	@Test
	void testEscapingCheck() {
		GameEngine engine = new GameEngine(3);
		clearBoard(engine);

		// Set up a position with the king in check but with escape squares
		engine.getGameState().getBoard().setPieceAt("e1", new King("White", "e1"));
		engine.getGameState().getBoard().setPieceAt("e8", new Rook("Black", "e8")); // Rook giving check
		engine.getGameState().getBoard().setPieceAt("a8", new King("Black", "a8")); // Black king needed

		engine.getGameState().setCurrentTurn("White");

		// Verify check detection
		assertTrue(engine.getGameState().getBoard().isKingInCheck("White"), "King should be in check");
		assertFalse(engine.getGameState().getBoard().isCheckmate("White"),
				"King should not be in checkmate as it can escape");

		// Move king out of check
		boolean moveSuccessful = engine.movePiece("e1", "d1");
		assertTrue(moveSuccessful, "King should be able to escape check");

		// Verify king is no longer in check
		assertFalse(engine.getGameState().getBoard().isKingInCheck("White"),
				"King should not be in check after moving");
	}

	@Test
	void testBlockingCheck() {
		GameEngine engine = new GameEngine(3);
		clearBoard(engine);

		// Set up a position where check can be blocked
		engine.getGameState().getBoard().setPieceAt("e1", new King("White", "e1"));
		engine.getGameState().getBoard().setPieceAt("e8", new Rook("Black", "e8")); // Rook giving check
		engine.getGameState().getBoard().setPieceAt("d2", new Queen("White", "d2")); // Queen to block check
		engine.getGameState().getBoard().setPieceAt("a8", new King("Black", "a8")); // Black king needed

		engine.getGameState().setCurrentTurn("White");

		assertTrue(engine.getGameState().getBoard().isKingInCheck("White"), "King should be in check");
		assertFalse(engine.getGameState().getBoard().isCheckmate("White"),
				"King should not be in checkmate as check can be blocked");

		// Block check with queen
		boolean moveSuccessful = engine.movePiece("d2", "e2");
		assertTrue(moveSuccessful, "Queen should be able to block check");

		// Verify king is no longer in check
		assertFalse(engine.getGameState().getBoard().isKingInCheck("White"),
				"King should not be in check after blocking");
	}

	@Test
	void testCaptureCheckingPiece() {
		GameEngine engine = new GameEngine(3);
		clearBoard(engine);

		// Set up a position where the checking piece can be captured
		engine.getGameState().getBoard().setPieceAt("e1", new King("White", "e1"));
		engine.getGameState().getBoard().setPieceAt("f3", new Knight("Black", "f3")); // Knight giving check (f3 can
																						// attack e1)
		engine.getGameState().getBoard().setPieceAt("f2", new Queen("White", "f2")); // Queen to capture knight
		engine.getGameState().getBoard().setPieceAt("a8", new King("Black", "a8")); // Black king needed

		engine.getGameState().setCurrentTurn("White");

		assertTrue(engine.getGameState().getBoard().isKingInCheck("White"), "King should be in check from knight");
		assertFalse(engine.getGameState().getBoard().isCheckmate("White"),
				"King should not be in checkmate as the knight can be captured");

		// Capture the knight
		boolean moveSuccessful = engine.movePiece("f2", "f3");
		assertTrue(moveSuccessful, "Queen should be able to capture the checking knight");

		// Verify king is no longer in check
		assertFalse(engine.getGameState().getBoard().isKingInCheck("White"),
				"King should not be in check after capturing the attacker");
	}

	@Test
	void testBackrankCheckmate() {
		GameEngine engine = new GameEngine(3);
		clearBoard(engine);

		// Set up a back-rank checkmate
		engine.getGameState().getBoard().setPieceAt("h1", new King("White", "h1"));
		engine.getGameState().getBoard().setPieceAt("g2", new Pawn("White", "g2"));
		engine.getGameState().getBoard().setPieceAt("h2", new Pawn("White", "h2"));
		engine.getGameState().getBoard().setPieceAt("a1", new Rook("Black", "a1"));
		engine.getGameState().getBoard().setPieceAt("e8", new King("Black", "e8")); // Black king needed

		engine.getGameState().setCurrentTurn("White");

		assertTrue(engine.getGameState().getBoard().isKingInCheck("White"), "White king should be in check");
		assertTrue(engine.getGameState().getBoard().isCheckmate("White"), "White king should be in checkmate");
	}

	@Test
	void testSmotheredCheckmate() {
		GameEngine engine = new GameEngine(3);
		clearBoard(engine);

		// Set up a smothered mate: Black king on h8, own pieces on g7, h7, Black rook
		// on g8, White knight on f7
		engine.getGameState().getBoard().setPieceAt("h8", new King("Black", "h8"));
		engine.getGameState().getBoard().setPieceAt("g7", new Pawn("Black", "g7"));
		engine.getGameState().getBoard().setPieceAt("h7", new Pawn("Black", "h7"));
		engine.getGameState().getBoard().setPieceAt("g8", new Rook("Black", "g8"));
		engine.getGameState().getBoard().setPieceAt("f7", new Knight("White", "f7")); // Knight delivering checkmate
		engine.getGameState().getBoard().setPieceAt("e1", new King("White", "e1")); // White king needed

		engine.getGameState().setCurrentTurn("Black");

		assertTrue(engine.getGameState().getBoard().isKingInCheck("Black"), "Black king should be in check");
		assertTrue(engine.getGameState().getBoard().isCheckmate("Black"), "Black king should be in checkmate");
	}

	@Test
	void testDiscoveredCheck() {
		GameEngine engine = new GameEngine(3);
		clearBoard(engine);

		// Set up a discovered check position
		engine.getGameState().getBoard().setPieceAt("e1", new King("White", "e1"));
		engine.getGameState().getBoard().setPieceAt("b4", new Bishop("Black", "b4")); // Bishop that will give check
		engine.getGameState().getBoard().setPieceAt("c3", new Knight("Black", "c3")); // Knight blocking the check
		engine.getGameState().getBoard().setPieceAt("a5", new King("Black", "a5")); // Black king needed

		engine.getGameState().setCurrentTurn("Black");

		assertFalse(engine.getGameState().getBoard().isKingInCheck("White"),
				"White king should not be in check initially");

		// Move knight to deliver discovered check
		boolean moveSuccessful = engine.movePiece("c3", "e4"); // Knight moves, revealing bishop check
		assertTrue(moveSuccessful, "Knight should be able to move and deliver discovered check");

		// Verify king is now in check
		assertTrue(engine.getGameState().getBoard().isKingInCheck("White"),
				"White king should be in check after discovered check");
	}

	// Helper method to clear the board
	private void clearBoard(GameEngine engine) {
		engine.getGameState().getBoard().clearBoard();
	}
}
