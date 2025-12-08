package com.ddemott.chessai;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import com.ddemott.chessai.pieces.King;
import com.ddemott.chessai.pieces.Rook;

public class KingInvalidMoveTest {

	@Test
	public void testInvalidTwoSquareMove() {
		Board board = new Board();
		board.clearBoard();
		King king = new King("White", "e4");
		board.setPieceAt("e4", king);
		king.setPosition("e4");
		// Try to move two squares horizontally
		assertFalse(king.isValidMove("g4", board), "King should NOT be able to move two squares horizontally");
		// Try to move two squares vertically
		assertFalse(king.isValidMove("e6", board), "King should NOT be able to move two squares vertically");
	}

	@Test
	public void testMoveIntoCheck() {
		Board board = new Board();
		board.clearBoard();
		King king = new King("White", "e4");
		board.setPieceAt("e4", king);
		king.setPosition("e4");
		// Place a black rook attacking e5
		Rook rook = new Rook("Black", "e8");
		board.setPieceAt("e8", rook);
		rook.setPosition("e8");
		// King tries to move into check
		assertFalse(king.isValidMove("e5", board), "King should NOT be able to move into check");
	}
}
