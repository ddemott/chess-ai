
package com.ddemott.chessai.console;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import com.ddemott.chessai.engine.GameEngine;
import com.ddemott.chessai.console.MoveValidator.MoveValidationResult;
import com.ddemott.chessai.console.MoveValidator.MoveError;

/**
 * JUnit 5 test suite for Enhanced Input/Output features
 */
public class EnhancedIOTest {

	@Test
	void testMoveValidation() {
		GameEngine engine = new GameEngine(3);
		// Test valid move
		MoveValidationResult result = MoveValidator.validateMove("e2", "e4", "White", engine.getGameState().getBoard());
		assertTrue(result.isValid(), "Valid move e2-e4");
		assertNull(result.getError(), "No error for valid move");

		// Test no piece at source
		result = MoveValidator.validateMove("e3", "e4", "White", engine.getGameState().getBoard());
		assertFalse(result.isValid(), "No piece at e3");
		assertEquals(MoveError.NO_PIECE_AT_SOURCE, result.getError(), "Correct error type");

		// Test wrong player piece
		result = MoveValidator.validateMove("e7", "e5", "White", engine.getGameState().getBoard());
		assertFalse(result.isValid(), "Wrong player piece");
		assertEquals(MoveError.WRONG_PLAYER_PIECE, result.getError(), "Correct error type");

		// Test invalid move format
		result = MoveValidator.validateMove("", "e4", "White", engine.getGameState().getBoard());
		assertFalse(result.isValid(), "Invalid format");
		assertEquals(MoveError.INVALID_FORMAT, result.getError(), "Correct error type");

		// Test out of bounds
		result = MoveValidator.validateMove("e2", "z9", "White", engine.getGameState().getBoard());
		assertFalse(result.isValid(), "Out of bounds");
		assertEquals(MoveError.OUT_OF_BOUNDS, result.getError(), "Correct error type");
	}

	// ...convert other test methods to @Test in the same way...

	@Test
	void testErrorMessages() {
		GameEngine engine = new GameEngine(3);
		EnhancedConsoleDisplay display = new EnhancedConsoleDisplay(engine.getGameState());
		// Test that error display doesn't crash
		assertDoesNotThrow(() -> display.displayInvalidMoveError("e2", "e5", "Test error message"),
				"Error message display should not throw");
		// Test move validation feedback
		String feedback = display.validateMoveWithFeedback("e3", "e4");
		assertNotNull(feedback, "Feedback for invalid move");
		assertTrue(feedback.toLowerCase().contains("no piece"), "Feedback mentions no piece");
	}

	@Test
	void testCheckDetection() {
		GameEngine engine = new GameEngine(3);
		// Initial position should have no check
		boolean whiteInCheck = engine.getGameState().getBoard().isKingInCheck("White");
		boolean blackInCheck = engine.getGameState().getBoard().isKingInCheck("Black");
		assertFalse(whiteInCheck, "White not in check initially");
		assertFalse(blackInCheck, "Black not in check initially");
		// Test king position finding
		String whiteKingPos = engine.getGameState().getBoard().findKingPosition("White");
		String blackKingPos = engine.getGameState().getBoard().findKingPosition("Black");
		assertEquals("e1", whiteKingPos, "White king at e1");
		assertEquals("e8", blackKingPos, "Black king at e8");
	}

	@Test
	void testCheckmateDetection() {
		GameEngine engine = new GameEngine(3);
		// Initial position should not be checkmate
		boolean whiteCheckmate = engine.getGameState().getBoard().isCheckmate("White");
		boolean blackCheckmate = engine.getGameState().getBoard().isCheckmate("Black");
		assertFalse(whiteCheckmate, "White not in checkmate initially");
		assertFalse(blackCheckmate, "Black not in checkmate initially");
	}

	@Test
	void testStalemateDetection() {
		GameEngine engine = new GameEngine(3);
		// Initial position should not be stalemate
		boolean whiteStalemate = engine.getGameState().getBoard().isStalemate("White");
		boolean blackStalemate = engine.getGameState().getBoard().isStalemate("Black");
		assertFalse(whiteStalemate, "White not in stalemate initially");
		assertFalse(blackStalemate, "Black not in stalemate initially");
	}

	@Test
	void testCapturedPiecesTracking() {
		GameEngine engine = new GameEngine(3);
		EnhancedConsoleDisplay display = new EnhancedConsoleDisplay(engine.getGameState());
		// Make a capture move
		engine.movePiece("e2", "e4");
		engine.movePiece("d7", "d5");
		// Get the pawn that will be captured
		var capturedPawn = engine.getGameState().getBoard().getPieceAt("d5");
		// Make capturing move
		boolean captureSuccess = engine.movePiece("e4", "d5");
		assertTrue(captureSuccess, "Capture move successful");
		// Test that captured piece can be added to display
		if (capturedPawn != null) {
			assertDoesNotThrow(() -> display.addCapturedPiece(capturedPawn),
					"Captured piece tracking should not throw");
		}
	}

	@Test
	void testMoveSuggestions() {
		GameEngine engine = new GameEngine(3);
		EnhancedConsoleDisplay display = new EnhancedConsoleDisplay(engine.getGameState());
		// Test generating suggestions
		var suggestions = display.generateMoveSuggestions("White");
		assertNotNull(suggestions, "Suggestions generated");
		// Test suggestion display doesn't crash
		assertDoesNotThrow(() -> display.displayMoveSuggestions("White"), "Move suggestions display should not throw");
		// Test move validator suggestions
		var validatorSuggestions = MoveValidator.generateMoveSuggestions("e2", engine.getGameState().getBoard(),
				"White");
		assertNotNull(validatorSuggestions, "Validator suggestions generated");
	}

	@Test
	void testDisplayFeatures() {
		GameEngine engine = new GameEngine(3);
		EnhancedConsoleDisplay display = new EnhancedConsoleDisplay(engine.getGameState());
		// Test enhanced board display
		assertDoesNotThrow((org.junit.jupiter.api.function.Executable) display::displayBoard,
				"Enhanced board display should not throw");
		// Test color enable/disable
		assertDoesNotThrow((org.junit.jupiter.api.function.Executable) () -> {
			display.disableColors();
			display.enableColors();
		}, "Color toggle should not throw");
	}

	// JUnit 5 assertions are now used; all custom assertion helpers and counters
	// removed.
}
