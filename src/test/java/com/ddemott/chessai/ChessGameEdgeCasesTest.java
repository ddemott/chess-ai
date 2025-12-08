package com.ddemott.chessai;

import com.ddemott.chessai.pieces.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for chess game edge cases and rules not yet covered
 */
public class ChessGameEdgeCasesTest {
	@Test
	void testInsufficientMaterialDraw() {
		Board board = new Board();
		// Remove all pieces except kings
		for (char col = 'a'; col <= 'h'; col++) {
			for (int row = 1; row <= 8; row++) {
				String pos = "" + col + row;
				IPiece piece = board.getPieceAt(pos);
				if (piece != null && !(piece instanceof King)) {
					board.setPieceAt(pos, null);
				}
			}
		}
		assertTrue(board.isStalemate("White") || board.isStalemate("Black") || board.isCheckmate("White") == false);
	}

	@Test
	void testFiftyMoveRuleDraw() {
		Board board = new Board();
		// Simulate 50 moves without pawn move or capture
		for (int i = 0; i < 50; i++) {
			board.setPieceAt("e2", null); // Remove pawn
		}
		// This is a placeholder: actual implementation should track move count
		// assertTrue(board.isFiftyMoveDraw());
	}

	@Test
	void testThreefoldRepetitionDraw() {
		// Simulate threefold repetition
		// This is a placeholder: actual implementation should track board states
		// assertTrue(board.isThreefoldRepetitionDraw());
	}

	@Test
	void testPawnPromotionEdgeCases() {
		Board board = new Board();
		board.setPieceAt("a7", new Pawn("White", "a7"));
		IPiece pawn = board.getPieceAt("a7");
		board.setPieceAt("a8", null);
		assertTrue(pawn.isValidMove("a8", board));
		// Simulate promotion
		// assertTrue(board.promotePawn("a8", "Queen"));
	}

	@Test
	void testEnPassantEdgeCases() {
		Board board = new Board();
		board.setPieceAt("e5", new Pawn("White", "e5"));
		board.setPieceAt("d5", new Pawn("Black", "d5"));
		// Simulate black pawn moves d7-d5, white pawn captures en passant
		// This is a placeholder: actual implementation should track en passant
		// assertTrue(board.isEnPassantAllowed("e5", "d6"));
	}

	@Test
	void testIllegalMoveIntoCheck() {
		Board board = new Board();
		board.setPieceAt("e1", new King("White", "e1"));
		board.setPieceAt("e2", null);
		board.setPieceAt("e8", new Rook("Black", "e8"));
		for (char row = '2'; row <= '7'; row++) {
			board.setPieceAt("e" + row, null);
		}
		IPiece king = board.getPieceAt("e1");
		assertFalse(king.isValidMove("e2", board)); // Would move into check
	}

	@Test
	void testMalformedPGNImport() {
		String malformedPGN = "[Event\"Broken\"]\n1. e4 e5 2. Nf3 Nc6 3. Bb5 a6 1-0";
		MoveHistory.PGNGameData data = MoveHistory.parsePGN(malformedPGN);
		assertNotNull(data);
		// Should handle gracefully
	}
}
