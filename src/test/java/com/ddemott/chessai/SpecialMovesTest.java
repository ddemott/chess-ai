package com.ddemott.chessai;

import com.ddemott.chessai.engine.GameEngine;
import com.ddemott.chessai.pieces.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for special chess moves including castling, en passant, and promotion.
 */
public class SpecialMovesTest {

	@Test
	void testKingsideCastling() {
		GameEngine engine = new GameEngine(1);
		clearBoard(engine);

		// Set up for kingside castling
		engine.getGameState().getBoard().setPieceAt("e1", new King("White", "e1"));
		engine.getGameState().getBoard().setPieceAt("h1", new Rook("White", "h1"));
		engine.getGameState().getBoard().setPieceAt("e8", new King("Black", "e8")); // Need opponent king

		engine.getGameState().setCurrentTurn("White");

		// Test castling
		boolean castlingResult = engine.movePiece("e1", "g1");
		assertTrue(castlingResult, "Kingside castling should be allowed");

		// Verify king and rook positions after castling
		assertNull(engine.getGameState().getBoard().getPieceAt("e1"), "Original king square should be empty");
		assertNull(engine.getGameState().getBoard().getPieceAt("h1"), "Original rook square should be empty");
		assertTrue(engine.getGameState().getBoard().getPieceAt("g1") instanceof King, "King should be on g1");
		assertTrue(engine.getGameState().getBoard().getPieceAt("f1") instanceof Rook, "Rook should be on f1");
	}

	@Test
	void testQueensideCastling() {
		GameEngine engine = new GameEngine(1);
		clearBoard(engine);

		// Set up for queenside castling
		engine.getGameState().getBoard().setPieceAt("e1", new King("White", "e1"));
		engine.getGameState().getBoard().setPieceAt("a1", new Rook("White", "a1"));
		engine.getGameState().getBoard().setPieceAt("e8", new King("Black", "e8")); // Need opponent king

		engine.getGameState().setCurrentTurn("White");

		// Test castling
		boolean castlingResult = engine.movePiece("e1", "c1");
		assertTrue(castlingResult, "Queenside castling should be allowed");

		// Verify king and rook positions after castling
		assertNull(engine.getGameState().getBoard().getPieceAt("e1"), "Original king square should be empty");
		assertNull(engine.getGameState().getBoard().getPieceAt("a1"), "Original rook square should be empty");
		assertTrue(engine.getGameState().getBoard().getPieceAt("c1") instanceof King, "King should be on c1");
		assertTrue(engine.getGameState().getBoard().getPieceAt("d1") instanceof Rook, "Rook should be on d1");
	}

	@Test
	void testCastlingIllegalWhenPiecesBetween() {
		GameEngine engine = new GameEngine(1);
		clearBoard(engine);

		// Set up with pieces between king and rook
		engine.getGameState().getBoard().setPieceAt("e1", new King("White", "e1"));
		engine.getGameState().getBoard().setPieceAt("h1", new Rook("White", "h1"));
		engine.getGameState().getBoard().setPieceAt("f1", new Bishop("White", "f1")); // Piece in between
		engine.getGameState().getBoard().setPieceAt("e8", new King("Black", "e8")); // Need opponent king

		engine.getGameState().setCurrentTurn("White");

		// Test castling should fail
		boolean castlingResult = engine.movePiece("e1", "g1");
		assertFalse(castlingResult, "Kingside castling should not be allowed with pieces between");
	}

	@Test
	void testCastlingIllegalWhenKingInCheck() {
		GameEngine engine = new GameEngine(1);
		clearBoard(engine);

		// Set up with king in check
		engine.getGameState().getBoard().setPieceAt("e1", new King("White", "e1"));
		engine.getGameState().getBoard().setPieceAt("h1", new Rook("White", "h1"));
		// Ensure e8 is empty or contains a captureable piece if testing capture
		// promotions
		// For basic promotion test, leave e8 empty
		engine.getGameState().getBoard().setPieceAt("e2", new Rook("Black", "e2")); // Rook checking the king

		engine.getGameState().setCurrentTurn("White");

		// Test castling should fail
		boolean castlingResult = engine.movePiece("e1", "g1");
		assertFalse(castlingResult, "Castling should not be allowed when king is in check");
	}

	@Test
	void testCastlingIllegalWhenSquareAttacked() {
		GameEngine engine = new GameEngine(1);
		clearBoard(engine);

		// Set up with f1 square under attack
		engine.getGameState().getBoard().setPieceAt("e1", new King("White", "e1"));
		engine.getGameState().getBoard().setPieceAt("h1", new Rook("White", "h1"));
		// e8 intentionally left empty to allow a straight promotion
		engine.getGameState().getBoard().setPieceAt("f8", new Rook("Black", "f8")); // Rook attacking f1

		engine.getGameState().setCurrentTurn("White");

		// Test castling should fail
		boolean castlingResult = engine.movePiece("e1", "g1");
		assertFalse(castlingResult, "Castling should not be allowed when squares between are attacked");
	}

	@Test
	void testEnPassantCapture() {
		GameEngine engine = new GameEngine(1);
		clearBoard(engine);

		// Set up en passant scenario
		engine.getGameState().getBoard().setPieceAt("e5", new Pawn("White", "e5"));
		engine.getGameState().getBoard().setPieceAt("d7", new Pawn("Black", "d7"));
		engine.getGameState().getBoard().setPieceAt("e1", new King("White", "e1"));
		// e8 intentionally left empty to allow a forward pawn promotion

		engine.getGameState().setCurrentTurn("Black");

		// Move black pawn two squares forward (trigger en passant)
		boolean moveResult = engine.movePiece("d7", "d5");
		assertTrue(moveResult, "Pawn should be able to move two squares");

		// White's turn - capture en passant
		engine.getGameState().setCurrentTurn("White");
		boolean captureResult = engine.movePiece("e5", "d6");
		assertTrue(captureResult, "En passant capture should be allowed");

		// Verify positions
		assertNull(engine.getGameState().getBoard().getPieceAt("e5"), "Original white pawn square should be empty");
		assertNull(engine.getGameState().getBoard().getPieceAt("d5"), "Black pawn should be captured");
		assertTrue(engine.getGameState().getBoard().getPieceAt("d6") instanceof Pawn, "White pawn should be on d6");
	}

	@Test
	void testEnPassantOnlyValidForOneTurn() {
		GameEngine engine = new GameEngine(1);
		clearBoard(engine);

		// Set up en passant scenario
		engine.getGameState().getBoard().setPieceAt("e5", new Pawn("White", "e5"));
		engine.getGameState().getBoard().setPieceAt("d7", new Pawn("Black", "d7"));
		engine.getGameState().getBoard().setPieceAt("a5", new Pawn("White", "a5")); // Another pawn to move
		engine.getGameState().getBoard().setPieceAt("e1", new King("White", "e1"));
		// e8 intentionally left empty to allow a straight promotion

		engine.getGameState().setCurrentTurn("Black");

		// Move black pawn two squares forward (trigger en passant)
		engine.movePiece("d7", "d5");

		// White's turn - but move a different pawn
		engine.getGameState().setCurrentTurn("White");
		engine.movePiece("a5", "a6");

		// Black's turn - make another move
		engine.getGameState().setCurrentTurn("Black");
		engine.movePiece("e8", "d8");

		// White's turn - now try en passant (should fail)
		engine.getGameState().setCurrentTurn("White");
		boolean captureResult = engine.movePiece("e5", "d6");
		assertFalse(captureResult, "En passant should not be allowed after a turn has passed");
	}

	@Test
	void testPawnPromotion() {
		GameEngine engine = new GameEngine(1);
		clearBoard(engine);

		// Set up pawn promotion scenario
		engine.getGameState().getBoard().setPieceAt("e7", new Pawn("White", "e7"));
		engine.getGameState().getBoard().setPieceAt("e1", new King("White", "e1"));
		// Leave e8 empty to allow forward promotion

		engine.getGameState().setCurrentTurn("White");

		// Promote pawn to queen
		boolean promotionResult = engine.movePiece("e7", "e8", "Q");
		assertTrue(promotionResult, "Pawn promotion should be allowed");

		// Verify promotion
		assertTrue(engine.getGameState().getBoard().getPieceAt("e8") instanceof Queen,
				"Pawn should be promoted to queen");
		assertEquals("White", engine.getGameState().getBoard().getPieceAt("e8").getColor(),
				"Promoted piece should be white");
	}

	@Test
	void testPawnPromotionToKnight() {
		try {
			GameEngine engine = new GameEngine(1);
			clearBoard(engine);

			// Set up pawn promotion scenario
			engine.getGameState().getBoard().setPieceAt("e7", new Pawn("White", "e7"));
			engine.getGameState().getBoard().setPieceAt("e1", new King("White", "e1"));
			// For promotion to knight test, keep e8 empty for a simple forward promotion

			engine.getGameState().setCurrentTurn("White");

			// Promote pawn to knight
			boolean promotionResult = engine.movePiece("e7", "e8", "N");
			assertTrue(promotionResult, "Pawn promotion to knight should be allowed");

			// Verify promotion
			assertTrue(engine.getGameState().getBoard().getPieceAt("e8") instanceof Knight,
					"Pawn should be promoted to knight");
			assertEquals("White", engine.getGameState().getBoard().getPieceAt("e8").getColor(),
					"Promoted piece should be white");
		} catch (Exception e) {
			fail("Test failed due to unexpected exception: " + e.getMessage());
		}
	}

	@Test
	void testPawnPromotionWithCapture() {
		GameEngine engine = new GameEngine(1);
		clearBoard(engine);

		// Set up pawn promotion with capture scenario
		engine.getGameState().getBoard().setPieceAt("d7", new Pawn("White", "d7"));
		engine.getGameState().getBoard().setPieceAt("e8", new Rook("Black", "e8")); // Piece to capture
		engine.getGameState().getBoard().setPieceAt("e1", new King("White", "e1"));
		engine.getGameState().getBoard().setPieceAt("a8", new King("Black", "a8"));

		engine.getGameState().setCurrentTurn("White");

		// Promote pawn to queen with capture
		boolean promotionResult = engine.movePiece("d7", "e8", "Q");
		assertTrue(promotionResult, "Pawn promotion with capture should be allowed");

		// Verify promotion and capture
		assertTrue(engine.getGameState().getBoard().getPieceAt("e8") instanceof Queen,
				"Pawn should be promoted to queen");
		assertEquals("White", engine.getGameState().getBoard().getPieceAt("e8").getColor(),
				"Promoted piece should be white");
	}

	// Helper method to clear the board
	private void clearBoard(GameEngine engine) {
		engine.getGameState().getBoard().clearBoard();
	}
}
