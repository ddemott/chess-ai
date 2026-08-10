package com.ddemott.chessai;

import com.ddemott.chessai.engine.GameEngine;
import com.ddemott.chessai.pieces.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit 5 test for undo/redo board integrity, including special moves
 * (castling, en passant, promotion). Converted from main() harness per test
 * conventions in CLAUDE.md.
 */
public class UndoRedoBoardIntegrityTest {

	private GameEngine engine;

	@BeforeEach
	void setUp() {
		engine = new GameEngine(3);
	}

	private void clearBoard() {
		engine.getGameState().getBoard().clearBoard();
	}

	@Test
	void testBoardIntegrityAfterCapture() {
		String initialBoard = engine.getBoardRepresentation();

		assertTrue(engine.movePiece("e2", "e4"));
		assertTrue(engine.movePiece("d7", "d5"));
		assertTrue(engine.movePiece("e4", "d5"));

		assertTrue(engine.undoLastMove());
		assertTrue(engine.undoLastMove());
		assertTrue(engine.undoLastMove());

		String finalBoard = engine.getBoardRepresentation();
		assertEquals(initialBoard.trim(), finalBoard.trim(),
		        "Board state fully restored after undo sequence");
	}

	@Test
	void testPiecePositionConsistency() {
		assertTrue(engine.movePiece("g1", "f3"));

		IPiece knightAtF3 = engine.getGameState().getBoard().getPieceAt("f3");
		IPiece atG1 = engine.getGameState().getBoard().getPieceAt("g1");
		assertNotNull(knightAtF3, "Knight should be at f3");
		assertNull(atG1, "g1 should be empty after move");

		assertTrue(engine.undoLastMove());

		IPiece knightBackAtG1 = engine.getGameState().getBoard().getPieceAt("g1");
		IPiece atF3 = engine.getGameState().getBoard().getPieceAt("f3");
		assertNotNull(knightBackAtG1, "Knight should be restored to g1");
		assertNull(atF3, "f3 should be empty after undo");

		assertTrue(engine.redoLastMove());
		assertNotNull(engine.getGameState().getBoard().getPieceAt("f3"),
		        "Knight back at f3 after redo");
	}

	@Test
	void testTurnConsistency() {
		assertEquals("White", engine.getCurrentTurn(), "Initial turn should be White");

		assertTrue(engine.movePiece("e2", "e4"));
		assertEquals("Black", engine.getCurrentTurn());

		engine.makeAIMove();
		assertEquals("White", engine.getCurrentTurn());

		assertTrue(engine.undoLastMove());
		assertEquals("Black", engine.getCurrentTurn(), "Turn reverts correctly after undo AI");

		assertTrue(engine.undoLastMove());
		assertEquals("White", engine.getCurrentTurn(), "Turn reverts correctly after undo player");

		assertTrue(engine.redoLastMove());
		assertEquals("Black", engine.getCurrentTurn(), "Turn after redo player");

		assertTrue(engine.redoLastMove());
		assertEquals("White", engine.getCurrentTurn(), "Turn after redo AI");
	}

	@Test
	void testUndoRedoCastling() {
		clearBoard();
		engine.getGameState().getBoard().setPieceAt("e1", new King("White", "e1"));
		engine.getGameState().getBoard().setPieceAt("h1", new Rook("White", "h1"));
		engine.getGameState().getBoard().setPieceAt("e8", new King("Black", "e8"));
		engine.getGameState().setCurrentTurn("White");

		boolean castled = engine.movePiece("e1", "g1");
		assertTrue(castled, "Kingside castling should succeed");

		assertTrue(engine.undoLastMove(), "Undo castling should succeed");

		// Verify restoration
		assertTrue(engine.getGameState().getBoard().getPieceAt("e1") instanceof King,
		        "King restored to e1");
		assertTrue(engine.getGameState().getBoard().getPieceAt("h1") instanceof Rook,
		        "Rook restored to h1");
		assertNull(engine.getGameState().getBoard().getPieceAt("g1"), "g1 empty after undo");
		assertNull(engine.getGameState().getBoard().getPieceAt("f1"), "f1 empty after undo");

		// Verify can castle again
		assertTrue(engine.movePiece("e1", "g1"), "Castling should be possible again after undo");
	}

	@Test
	void testUndoRedoEnPassant() {
		clearBoard();
		engine.getGameState().getBoard().setPieceAt("e5", new Pawn("White", "e5"));
		engine.getGameState().getBoard().setPieceAt("d7", new Pawn("Black", "d7"));
		engine.getGameState().getBoard().setPieceAt("e1", new King("White", "e1"));
		engine.getGameState().getBoard().setPieceAt("e8", new King("Black", "e8"));
		engine.getGameState().setCurrentTurn("Black");

		assertTrue(engine.movePiece("d7", "d5")); // triggers en passant target

		engine.getGameState().setCurrentTurn("White");
		boolean enPassantCaptured = engine.movePiece("e5", "d6");
		assertTrue(enPassantCaptured, "En passant should succeed");

		assertTrue(engine.undoLastMove(), "Undo en passant should succeed");

		assertNotNull(engine.getGameState().getBoard().getPieceAt("d5"),
		        "Captured pawn restored on undo");
		assertNotNull(engine.getGameState().getBoard().getPieceAt("e5"), "White pawn restored");
	}

	@Test
	void testUndoRedoPromotion() {
		clearBoard();
		engine.getGameState().getBoard().setPieceAt("d7", new Pawn("White", "d7"));
		engine.getGameState().getBoard().setPieceAt("e8", new Rook("Black", "e8")); // capture
		                                                                            // target
		engine.getGameState().getBoard().setPieceAt("e1", new King("White", "e1"));
		engine.getGameState().getBoard().setPieceAt("a8", new King("Black", "a8"));
		engine.getGameState().setCurrentTurn("White");

		boolean promoted = engine.movePiece("d7", "e8", "Q");
		assertTrue(promoted, "Promotion with capture should succeed");
		assertTrue(engine.getGameState().getBoard().getPieceAt("e8") instanceof Queen,
		        "Should be Queen after promotion");

		assertTrue(engine.undoLastMove(), "Undo promotion should succeed");

		assertTrue(engine.getGameState().getBoard().getPieceAt("d7") instanceof Pawn,
		        "Pawn restored on undo");
		assertNotNull(engine.getGameState().getBoard().getPieceAt("e8"),
		        "Captured piece restored on undo");
	}
}
