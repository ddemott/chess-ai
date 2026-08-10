package com.ddemott.chessai;

import com.ddemott.chessai.engine.GameEngine;
import com.ddemott.chessai.pieces.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

/**
 * Regression tests for bugs found during the code audit. Each test verifies a
 * specific bug fix and prevents re-introduction.
 */
public class BugRegressionTest {

	// =========================================================================
	// Priority 1: En passant captures tracked in captured pieces list
	// Bug: Board.executeEnPassant() removed the captured pawn but never added
	// it to capturedWhitePieces/capturedBlackPieces.
	// =========================================================================

	@Test
	void testEnPassantCaptureTrackedInCapturedList() {
		GameEngine engine = new GameEngine(1);
		// Set up en passant: White pawn to e5, then Black plays f7-f5
		engine.movePiece("e2", "e4"); // White
		engine.movePiece("a7", "a6"); // Black (filler)
		engine.movePiece("e4", "e5"); // White
		engine.movePiece("f7", "f5"); // Black two-square move, en passant target = f6

		Board board = engine.getGameState().getBoard();
		assertEquals("f6", board.getEnPassantTarget());

		// Capture en passant
		boolean success = engine.movePiece("e5", "f6");
		assertTrue(success, "En passant capture should succeed");

		// The captured black pawn should be in the captured list
		List<IPiece> capturedBlack = board.getCapturedPieces(Side.BLACK);
		boolean foundPawn = capturedBlack.stream()
		        .anyMatch(p -> p instanceof Pawn && p.getSide() == Side.BLACK);
		assertTrue(foundPawn,
		        "Captured black pawn should appear in captured pieces list after en passant");
	}

	@Test
	void testEnPassantCaptureByBlackTracked() {
		GameEngine engine = new GameEngine(1);
		// Set up en passant for Black
		engine.movePiece("a2", "a3"); // White (filler)
		engine.movePiece("d7", "d5"); // Black
		engine.movePiece("b2", "b3"); // White (filler)
		engine.movePiece("d5", "d4"); // Black
		engine.movePiece("c2", "c4"); // White two-square move, en passant target = c3

		Board board = engine.getGameState().getBoard();
		assertEquals("c3", board.getEnPassantTarget());

		boolean success = engine.movePiece("d4", "c3"); // Black captures en passant
		assertTrue(success, "Black en passant capture should succeed");

		List<IPiece> capturedWhite = board.getCapturedPieces("White");
		boolean foundPawn = capturedWhite.stream()
		        .anyMatch(p -> p instanceof Pawn && p.getSide() == Side.WHITE);
		assertTrue(foundPawn,
		        "Captured white pawn should appear in captured pieces list after en passant");
	}

	// =========================================================================
	// Priority 1: Promotion captures tracked in captured pieces list
	// Bug: Board.movePiece(from, to, promotionPiece) silently replaced the
	// destination piece without adding it to the captured list.
	// =========================================================================

	@Test
	void testPromotionCaptureTracked() {
		GameEngine engine = new GameEngine(1);
		Board board = engine.getGameState().getBoard();

		// Set up: White pawn on a7, Black rook on b8 (leave it from initial position)
		board.clearBoard();
		board.setPieceAt("a7", new Pawn(Side.WHITE, "a7"));
		board.setPieceAt("b8", new Rook(Side.BLACK, "b8"));
		board.setPieceAt("e1", new King(Side.WHITE, "e1"));
		board.setPieceAt("e8", new King(Side.BLACK, "e8"));

		// White pawn captures b8 and promotes to Queen
		boolean success = engine.movePiece("a7", "b8", "Q");
		assertTrue(success, "Promotion with capture should succeed");

		// Verify the captured rook is tracked
		List<IPiece> capturedBlack = board.getCapturedPieces(Side.BLACK);
		boolean foundRook = capturedBlack.stream()
		        .anyMatch(p -> p instanceof Rook && p.getSide() == Side.BLACK);
		assertTrue(foundRook,
		        "Captured black rook should appear in captured pieces list after promotion capture");

		// Verify the promoted piece is a Queen
		IPiece promotedPiece = board.getPieceAt("b8");
		assertNotNull(promotedPiece);
		assertTrue(promotedPiece instanceof Queen, "Promoted piece should be a Queen");
		assertEquals(Side.WHITE, promotedPiece.getSide());
	}

	@Test
	void testPromotionWithoutCaptureDoesNotAddToCapturedList() {
		GameEngine engine = new GameEngine(1);
		Board board = engine.getGameState().getBoard();

		// Set up: White pawn on a7, a8 empty
		board.clearBoard();
		board.setPieceAt("a7", new Pawn(Side.WHITE, "a7"));
		board.setPieceAt("e1", new King(Side.WHITE, "e1"));
		board.setPieceAt("e8", new King(Side.BLACK, "e8"));

		boolean success = engine.movePiece("a7", "a8", "Q");
		assertTrue(success, "Promotion without capture should succeed");

		List<IPiece> capturedBlack = board.getCapturedPieces(Side.BLACK);
		assertTrue(capturedBlack.isEmpty(),
		        "No pieces should be captured on a non-capture promotion");
	}

	// =========================================================================
	// Priority 1: Board.deepCopy() copies captured pieces lists
	// Bug: clone() didn't copy capturedWhitePieces/capturedBlackPieces.
	// =========================================================================

	@Test
	void testBoardCloneCopiesCapturedPieces() {
		GameEngine engine = new GameEngine(1);
		// Make a capture
		engine.movePiece("e2", "e4");
		engine.movePiece("d7", "d5");
		engine.movePiece("e4", "d5"); // White captures black pawn

		Board original = engine.getGameState().getBoard();
		assertFalse(original.getCapturedPieces(Side.BLACK).isEmpty(),
		        "Original should have captured pieces");

		Board cloned = original.deepCopy();
		assertEquals(original.getCapturedPieces(Side.BLACK).size(),
		        cloned.getCapturedPieces(Side.BLACK).size(),
		        "Cloned board should have the same number of captured black pieces");
		assertEquals(original.getCapturedPieces("White").size(),
		        cloned.getCapturedPieces("White").size(),
		        "Cloned board should have the same number of captured white pieces");
	}

	@Test
	void testBoardCloneCapturedListsAreIndependent() {
		GameEngine engine = new GameEngine(1);
		engine.movePiece("e2", "e4");
		engine.movePiece("d7", "d5");
		engine.movePiece("e4", "d5"); // capture

		Board original = engine.getGameState().getBoard();
		Board cloned = original.deepCopy();

		int originalCount = original.getCapturedPieces(Side.BLACK).size();

		// Modify the cloned board's captured list shouldn't affect original
		cloned.getCapturedPieces(Side.BLACK).add(new Pawn(Side.BLACK, "a1"));
		assertEquals(originalCount, original.getCapturedPieces(Side.BLACK).size(),
		        "Modifying cloned captured list should not affect original");
	}

	// =========================================================================
	// Priority 2: State.deepCopy() properly copies MoveHistory without reflection
	// Bug: Used reflection to access private fields, silently failed on error.
	// =========================================================================

	@Test
	void testStateCloneCopiesMoveHistory() {
		GameEngine engine = new GameEngine(1);
		engine.movePiece("e2", "e4");
		engine.movePiece("e7", "e5");

		State original = engine.getGameState();
		State cloned = original.deepCopy();

		// Move history should have the same moves
		assertEquals(original.getMoveHistory().getMoveCount(),
		        cloned.getMoveHistory().getMoveCount(),
		        "Cloned state should have the same number of moves");

		// Last move should match
		assertNotNull(cloned.getMoveHistory().getLastMove());
		assertEquals(original.getMoveHistory().getLastMove().getAlgebraicNotation(),
		        cloned.getMoveHistory().getLastMove().getAlgebraicNotation(),
		        "Last move notation should match after clone");
	}

	@Test
	void testStateCloneHalfmoveClockCopied() {
		GameEngine engine = new GameEngine(1);
		// Make some non-pawn, non-capture moves to increment halfmove clock
		engine.movePiece("g1", "f3"); // knight
		engine.movePiece("g8", "f6"); // knight

		State original = engine.getGameState();
		int originalClock = original.getMoveHistory().getHalfmoveClock();
		assertTrue(originalClock > 0, "Halfmove clock should be positive after knight moves");

		State cloned = original.deepCopy();
		assertEquals(originalClock, cloned.getMoveHistory().getHalfmoveClock(),
		        "Cloned state should preserve halfmove clock");
	}

	@Test
	void testStateCloneUndoRedoIndependent() {
		GameEngine engine = new GameEngine(1);
		engine.movePiece("e2", "e4");
		engine.movePiece("e7", "e5");

		State original = engine.getGameState();
		State cloned = original.deepCopy();

		// Undo on the clone should not affect original
		assertTrue(cloned.undoLastMove());
		assertEquals(2, original.getMoveHistory().getMoveCount(),
		        "Original move count should be unchanged after clone undo");
		assertTrue(original.getMoveHistory().canUndo(), "Original should still be able to undo");
	}

	// =========================================================================
	// Priority 2: MoveHistory.addPosition() undo/redo offset
	// Bug: Position history cleanup used wrong offset (+2), could drop
	// positions or keep stale ones during undo/redo.
	// =========================================================================

	@Test
	void testPositionHistoryConsistentAfterUndoRedo() {
		GameEngine engine = new GameEngine(1);
		engine.movePiece("e2", "e4");
		engine.movePiece("e7", "e5");
		engine.movePiece("g1", "f3");

		State state = engine.getGameState();
		int posCountBefore = state.getMoveHistory().getPositionHistory().size();
		assertEquals(3, posCountBefore, "Should have 3 positions after 3 moves");

		// Undo last move
		assertTrue(state.undoLastMove());

		// Make a different move — position history should be trimmed then extended
		engine.movePiece("d2", "d4");
		int posCountAfter = state.getMoveHistory().getPositionHistory().size();
		assertEquals(3, posCountAfter, "Should still have 3 positions after undo + new move");
	}

	@Test
	void testPositionHistoryAfterMultipleUndoRedo() {
		GameEngine engine = new GameEngine(1);
		engine.movePiece("e2", "e4");
		engine.movePiece("e7", "e5");

		State state = engine.getGameState();

		// Undo both moves
		assertTrue(state.undoLastMove());
		assertTrue(state.undoLastMove());

		// Redo both
		assertTrue(state.redoLastMove());
		assertTrue(state.redoLastMove());

		// Position history should match move count
		assertEquals(2, state.getMoveHistory().getPositionHistory().size(),
		        "Position history should have 2 entries after undo-all then redo-all");
	}

	// =========================================================================
	// Priority 2: King.java no longer catches NPE as control flow
	// Bug: Used catch(NullPointerException) to handle potentially null coords,
	// masking real bugs. Now relies on proper null checks.
	// =========================================================================

	@Test
	void testKingNormalMovesStillWork() {
		GameEngine engine = new GameEngine(1);
		Board board = engine.getGameState().getBoard();
		board.clearBoard();
		board.setPieceAt("e4", new King(Side.WHITE, "e4"));
		board.setPieceAt("e8", new King(Side.BLACK, "e8"));

		// King should be able to move one square in any direction
		assertTrue(engine.movePiece("e4", "e5"), "King should move to e5");
	}

	@Test
	void testKingCannotMoveIntoCheck() {
		GameEngine engine = new GameEngine(1);
		Board board = engine.getGameState().getBoard();
		board.clearBoard();
		board.setPieceAt("e1", new King(Side.WHITE, "e1"));
		board.setPieceAt("e8", new King(Side.BLACK, "e8"));
		board.setPieceAt("f8", new Rook(Side.BLACK, "f8")); // Controls f-file

		boolean moved = engine.movePiece("e1", "f1"); // f1 is attacked by rook on f8
		assertFalse(moved, "King should not be able to move into check");
	}

	@Test
	void testCastlingStillWorksAfterNPEFix() {
		GameEngine engine = new GameEngine(1);
		// Clear path for kingside castling
		engine.movePiece("e2", "e4");
		engine.movePiece("e7", "e5");
		engine.movePiece("g1", "f3");
		engine.movePiece("b8", "c6");
		engine.movePiece("f1", "e2");
		engine.movePiece("g8", "f6");

		boolean castled = engine.movePiece("e1", "g1");
		assertTrue(castled, "Kingside castling should still work after NPE fix");

		Board board = engine.getGameState().getBoard();
		assertTrue(board.getPieceAt("g1") instanceof King, "King should be on g1");
		assertTrue(board.getPieceAt("f1") instanceof Rook, "Rook should be on f1");
	}

	// =========================================================================
	// Priority 2: Board.movePiece() null coordinate guard
	// Bug: convertPositionToCoordinates() could return null and the result
	// was used without null checks, causing NPE.
	// =========================================================================

	@Test
	void testMovePieceWithInvalidPositionReturnsFalse() {
		GameEngine engine = new GameEngine(1);
		Board board = engine.getGameState().getBoard();

		// Invalid positions should not cause NPE, just return false
		assertFalse(board.movePiece("z9", "e4"), "Invalid 'from' position should return false");
		assertFalse(board.movePiece("e2", "z9"), "Invalid 'to' position should return false");
	}

	@Test
	void testMovePieceWithNullCoordinatesDoesNotThrow() {
		Board board = new Board();
		// These should return false gracefully, not throw NPE
		assertDoesNotThrow(() -> board.movePiece("j1", "k2"),
		        "Invalid positions should not throw exceptions");
	}
}
