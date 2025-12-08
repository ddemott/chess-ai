package com.ddemott.chessai;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import com.ddemott.chessai.pieces.Knight;
import com.ddemott.chessai.pieces.Pawn;

public class KnightMovementTest {

	@Test
	public void testValidLShapedMoves() {
		Board board = new Board();
		board.clearBoard();
		Knight knight = new Knight("White", "d4");
		board.setPieceAt("d4", knight);
		knight.setPosition("d4");
		String[] validMoves = {"c6", "e6", "f5", "f3", "e2", "c2", "b3", "b5"};
		for (String move : validMoves) {
			assertTrue(knight.isValidMove(move, board), "Knight should be able to move to " + move);
		}
	}

	@Test
	public void testInvalidMoves() {
		Board board = new Board();
		board.clearBoard();
		Knight knight = new Knight("White", "d4");
		board.setPieceAt("d4", knight);
		knight.setPosition("d4");
		String[] invalidMoves = {"d5", "d6", "d3", "d2", "c4", "e4", "b4", "f4", "c5", "e5", "c3", "e3"};
		for (String move : invalidMoves) {
			assertFalse(knight.isValidMove(move, board), "Knight should NOT be able to move to " + move);
		}
	}

	@Test
	public void testJumpingOverPieces() {
		Board board = new Board();
		board.clearBoard();
		Knight knight = new Knight("White", "d4");
		Pawn pawn1 = new Pawn("White", "d5");
		Pawn pawn2 = new Pawn("Black", "e4");
		board.setPieceAt("d4", knight);
		knight.setPosition("d4");
		board.setPieceAt("d5", pawn1);
		pawn1.setPosition("d5");
		board.setPieceAt("e4", pawn2);
		pawn2.setPosition("e4");
		// Knight should still be able to move to c6 (jumping over pawns)
		assertTrue(knight.isValidMove("c6", board), "Knight should be able to jump over pieces to c6");
	}

	@Test
	public void testCaptureOpponentPiece() {
		Board board = new Board();
		board.clearBoard();
		Knight knight = new Knight("White", "d4");
		Pawn pawn = new Pawn("Black", "c6");
		board.setPieceAt("d4", knight);
		knight.setPosition("d4");
		board.setPieceAt("c6", pawn);
		pawn.setPosition("c6");
		assertTrue(knight.isValidMove("c6", board), "Knight should be able to capture opponent's piece at c6");
	}

	@Test
	public void testCannotCaptureOwnPiece() {
		Board board = new Board();
		board.clearBoard();
		Knight knight = new Knight("White", "d4");
		Pawn pawn = new Pawn("White", "c6");
		board.setPieceAt("d4", knight);
		knight.setPosition("d4");
		board.setPieceAt("c6", pawn);
		pawn.setPosition("c6");
		assertFalse(knight.isValidMove("c6", board), "Knight should NOT be able to capture own piece at c6");
	}
}
