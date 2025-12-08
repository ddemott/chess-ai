package com.ddemott.chessai;

import com.ddemott.chessai.pieces.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class StateAdvancedMethodsTest {
	@Test
	public void testClone() {
		State state = new State();
		Board board = state.getBoard();
		board.setPieceAt("e2", new Pawn("White", "e2"));
		State cloned = state.clone();
		assertNotSame(state, cloned);
		assertNotSame(state.getBoard(), cloned.getBoard());
		assertTrue(cloned.getBoard().getPieceAt("e2") instanceof Pawn);
	}

	@Test
	public void testGetMoveHistory() {
		State state = new State();
		assertNotNull(state.getMoveHistory());
	}

	@Test
	public void testUndoRedoLastMove() {
		State state = new State();
		Board board = state.getBoard();
		board.clearBoard();
		board.setPieceAt("e2", new Pawn("White", "e2"));
		state.setCurrentTurn("White");
		state.movePiece("e2", "e4");
		assertTrue(state.undoLastMove());
		assertTrue(board.getPieceAt("e2") instanceof Pawn);
		assertNull(board.getPieceAt("e4"));
		assertTrue(state.redoLastMove());
		assertTrue(board.getPieceAt("e4") instanceof Pawn);
		assertNull(board.getPieceAt("e2"));
	}

	@Test
	public void testIsStalemate() {
		State state = new State();
		Board board = state.getBoard();
		board.clearBoard();
		// Adjusted stalemate: Black king on h8, White queen on f7, White king on f6,
		// Black to move
		board.setPieceAt("h8", new King("Black", "h8"));
		board.setPieceAt("f7", new Queen("White", "f7"));
		board.setPieceAt("f6", new King("White", "f6"));
		state.setCurrentTurn("Black");
		// Debug output removed
		assertTrue(state.isStalemate("Black"));
		assertFalse(state.isStalemate("White"));
	}

	@Test
	public void testIsThreefoldRepetition() {
		State state = new State();
		Board board = state.getBoard();
		board.clearBoard();
		// Use only kings and a knight for a simple repetition
		board.setPieceAt("e1", new King("White", "e1"));
		board.setPieceAt("e8", new King("Black", "e8"));
		board.setPieceAt("g1", new Knight("White", "g1"));
		state.setCurrentTurn("White");
		// Move knight back and forth to repeat position
		for (int i = 0; i < 3; i++) {
			state.movePiece("g1", "f3");
			state.setCurrentTurn("Black");
			state.movePiece("e8", "d8");
			state.setCurrentTurn("White");
			state.movePiece("f3", "g1");
			state.setCurrentTurn("Black");
			state.movePiece("d8", "e8");
			state.setCurrentTurn("White");
		}
		// Debug output removed
		assertTrue(state.isThreefoldRepetition());
	}

	@Test
	public void testIsFiftyMoveRule() {
		State state = new State();
		Board board = state.getBoard();
		board.clearBoard();
		board.setPieceAt("e1", new King("White", "e1"));
		board.setPieceAt("e8", new King("Black", "e8"));
		board.setPieceAt("a1", new Rook("White", "a1"));
		board.setPieceAt("h1", new Rook("White", "h1"));
		state.setCurrentTurn("White");
		// Move rook back and forth 100 times (100 halfmoves)
		for (int i = 0; i < 50; i++) {
			state.movePiece("a1", "a2");
			state.setCurrentTurn("Black");
			state.movePiece("h1", "h2");
			state.setCurrentTurn("White");
			state.movePiece("a2", "a1");
			state.setCurrentTurn("Black");
			state.movePiece("h2", "h1");
			state.setCurrentTurn("White");
		}
		// Debug output removed
		assertTrue(state.isFiftyMoveRule());
	}

	@Test
	public void testIsGameOver() {
		State state = new State();
		Board board = state.getBoard();
		board.clearBoard();
		board.setPieceAt("a1", new Rook("White", "a1"));
		board.setPieceAt("h1", new Rook("White", "h1"));
		state.setCurrentTurn("White");
		// Perform 50 full moves (100 halfmoves) to trigger fifty-move rule
		for (int i = 0; i < 50; i++) {
			state.movePiece("a1", "a2");
			state.setCurrentTurn("Black");
			state.movePiece("h1", "h2");
			state.setCurrentTurn("White");
			state.movePiece("a2", "a1");
			state.setCurrentTurn("Black");
			state.movePiece("h2", "h1");
			state.setCurrentTurn("White");
		}
		assertTrue(state.isGameOver());
	}
}
