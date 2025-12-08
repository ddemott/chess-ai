package com.ddemott.chessai;

import com.ddemott.chessai.pieces.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class StateTest {
	@Test
	public void testGetBoard() {
		State state = new State();
		assertNotNull(state.getBoard());
		assertTrue(state.getBoard() instanceof Board);
	}

	@Test
	public void testGetSetCurrentTurn() {
		State state = new State();
		state.setCurrentTurn("Black");
		assertEquals("Black", state.getCurrentTurn());
		state.setCurrentTurn("White");
		assertEquals("White", state.getCurrentTurn());
	}

	@Test
	public void testMovePieceWithPromotion() {
		State state = new State();
		Board board = state.getBoard();
		board.clearBoard();
		// Place white pawn on e7
		board.setPieceAt("e7", new Pawn("White", "e7"));
		state.setCurrentTurn("White");
		// Move pawn to e8 and promote to Queen
		boolean result = state.movePiece("e7", "e8", "Q");
		assertTrue(result);
		IPiece promoted = board.getPieceAt("e8");
		assertTrue(promoted instanceof Queen);
		assertEquals("White", promoted.getColor());
	}

	@Test
	public void testMovePieceWithPromotion_invalidPromotion() {
		State state = new State();
		Board board = state.getBoard();
		board.clearBoard();
		// Place white pawn on e7
		board.setPieceAt("e7", new Pawn("White", "e7"));
		state.setCurrentTurn("White");
		// Try to promote to an invalid piece
		boolean result = state.movePiece("e7", "e8", "X");
		assertFalse(result);
		IPiece promoted = board.getPieceAt("e8");
		assertFalse(promoted instanceof Queen);
	}
}
