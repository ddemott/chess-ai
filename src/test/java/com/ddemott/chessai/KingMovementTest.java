package com.ddemott.chessai;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import com.ddemott.chessai.pieces.King;

public class KingMovementTest {

	@Test
	public void testValidSingleSquareMoves() {
		Board board = new Board();
		board.clearBoard();
		King king = new King("White", "e4");
		board.setPieceAt("e4", king);
		king.setPosition("e4");
		// All adjacent squares
		String[] moves = {"d3", "e3", "f3", "d4", "f4", "d5", "e5", "f5"};
		for (String move : moves) {
			assertTrue(king.isValidMove(move, board), "King should be able to move to " + move);
		}
	}

	@Test
	public void testInvalidKingMoves() {
		Board board = new Board();
		board.clearBoard();
		King king = new King("White", "e4");
		board.setPieceAt("e4", king);
		king.setPosition("e4");
		// Invalid moves (more than one square)
		String[] invalidMoves = {"e6", "g4", "c4", "e2"};
		for (String move : invalidMoves) {
			assertFalse(king.isValidMove(move, board), "King should NOT be able to move to " + move);
		}
	}
}
