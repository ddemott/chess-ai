package com.ddemott.chessai;

import com.ddemott.chessai.pieces.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

import com.ddemott.chessai.ai.AIStrategy;

class DummyAIStrategy implements AIStrategy {
	@Override
	public String calculateBestMove(State state, String color) {
		return "e2 e4";
	}
}

public class StateCoreMethodsTest {
	@Test
	public void testSetAIStrategyAndGetBestMove() {
		State state = new State();
		state.setAIStrategy(new DummyAIStrategy());
		String bestMove = state.getBestMove();
		assertEquals("e2 e4", bestMove);
	}

	@Test
	public void testMovePiece_noPromotion() {
		State state = new State();
		Board board = state.getBoard();
		board.clearBoard();
		board.setPieceAt("e2", new Pawn("White", "e2"));
		state.setCurrentTurn("White");
		boolean result = state.movePiece("e2", "e4");
		assertTrue(result);
		assertNull(board.getPieceAt("e2"));
		assertTrue(board.getPieceAt("e4") instanceof Pawn);
	}

	@Test
	public void testMovePiece_withPromotion() {
		State state = new State();
		Board board = state.getBoard();
		board.clearBoard();
		board.setPieceAt("e7", new Pawn("White", "e7"));
		state.setCurrentTurn("White");
		boolean result = state.movePiece("e7", "e8", "Q");
		assertTrue(result);
		assertTrue(board.getPieceAt("e8") instanceof Queen);
	}

	@Test
	public void testGetAllPossibleMoves() {
		State state = new State();
		Board board = state.getBoard();
		board.clearBoard();
		board.setPieceAt("e2", new Pawn("White", "e2"));
		board.setPieceAt("d2", new Pawn("White", "d2"));
		List<String> moves = state.getAllPossibleMoves("White");
		assertNotNull(moves);
		assertTrue(moves.stream().anyMatch(m -> m.contains("e3")));
		assertTrue(moves.stream().anyMatch(m -> m.contains("d3")));
	}
}
