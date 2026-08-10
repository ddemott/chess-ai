package com.ddemott.chessai;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import com.ddemott.chessai.pieces.*;

public class BugReproductionTest {

	@Test
	public void testKingEscapeToH2() {
		Board board = new Board();
		board.clearBoard();

		// Set up the specific position from the game
		// White pieces
		board.setPieceAt("g1", new King(Side.WHITE, "g1"));
		board.setPieceAt("h1", new Rook(Side.WHITE, "h1"));
		board.setPieceAt("h3", new Pawn(Side.WHITE, "h3"));
		// Add other white pieces just in case, though likely irrelevant
		board.setPieceAt("b1", new Rook(Side.WHITE, "b1"));
		board.setPieceAt("c1", new Bishop(Side.WHITE, "c1")); // Diagram shows B at c1

		// Black pieces
		board.setPieceAt("f1", new Queen(Side.BLACK, "f1")); // The checker
		board.setPieceAt("e3", new Knight(Side.BLACK, "e3")); // Protects f1? No, e3 attacks g2, f1?
		// e3 to f1: e->f(1), 3->1(2). (1,2). Yes, Knight at e3 protects f1.

		board.setPieceAt("h8", new Rook(Side.BLACK, "h8")); // Potential attacker of h-file
		board.setPieceAt("b8", new Rook(Side.BLACK, "b8"));

		// Assert initial state
		assertTrue(board.isKingInCheck(Side.WHITE),
		        "White King should be in check from Queen at f1");

		// Verify threats on h2
		boolean h2UnderAttack = board.isSquareUnderAttack("h2", Side.WHITE);
		System.out.println("Is h2 under attack? " + h2UnderAttack);

		// Debug attackers if true
		if (h2UnderAttack) {
			debugAttackers(board, "h2", Side.BLACK);
		}

		// Verify King move
		IPiece king = board.getPieceAt("g1");
		assertNotNull(king, "King should be at g1");

		boolean canMoveToH2 = king.isValidMove("h2", board);
		System.out.println("Can King move to h2? " + canMoveToH2);

		assertTrue(canMoveToH2, "King should be able to escape to h2");
	}

	private void debugAttackers(Board board, String square, Side attackerSide) {
		System.out.println("Checking attackers for " + square + " from side " + attackerSide);
		for (int r = 0; r < 8; r++) {
			for (int c = 0; c < 8; c++) {
				IPiece p = board.getPieceAt(board.convertCoordinatesToPosition(r, c));
				if (p != null && p.getSide() == attackerSide) {
					// Temporarily move piece to check attack (logic from Board.isSquareUnderAttack)
					String originalPos = p.getPosition();
					String arrayPos = board.convertCoordinatesToPosition(r, c);
					p.setPosition(arrayPos);

					boolean attacks = false;
					if (p instanceof Pawn) {
						// Manual pawn check logic
						int[] target = board.convertPositionToCoordinates(square);
						int dir = p.getSide() == Side.WHITE ? 1 : -1;
						int rd = target[0] - r;
						int cd = target[1] - c;
						if (rd == dir && Math.abs(cd) == 1)
							attacks = true;
					} else {
						attacks = p.isValidMove(square, board);
					}

					if (attacks) {
						System.out.println("Attacker found: " + p.getClass().getSimpleName()
						        + " at " + originalPos);
					}
					p.setPosition(originalPos);
				}
			}
		}
	}
}
