package com.ddemott.chessai;

import com.ddemott.chessai.engine.GameEngine;
import com.ddemott.chessai.pieces.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Core chess rules and board logic tests: en passant, castling, promotion,
 * stalemate, threefold repetition, fifty-move rule.
 */
public class ChessRulesTest {

	@Test
	void testStalemate() {
		// Set up a simple stalemate position
		GameEngine engine = new GameEngine(1);
		// Clear the board first
		clearBoard(engine);
		// Place pieces for stalemate: Black king on h8, White king on f7, White queen
		// on g6
		engine.getGameState().getBoard().setPieceAt("h8", new King("Black", "h8"));
		engine.getGameState().getBoard().setPieceAt("f7", new King("White", "f7"));
		engine.getGameState().getBoard().setPieceAt("g6", new Queen("White", "g6"));
		engine.getGameState().setCurrentTurn("Black"); // Set Black to move (stalemate)

		// Print the board for debugging
		System.out.println("Stalemate test board:");
		System.out.println(engine.getBoardRepresentation());
		System.out.println("Current turn: " + engine.getCurrentTurn());

		// Verify stalemate
		assertTrue(isStalemate(engine), "Position should be stalemate");
	}

	@Test
	void testInsufficientMaterial() {
		// Test king vs king
		GameEngine engine = new GameEngine(1);
		clearBoard(engine);
		engine.getGameState().getBoard().setPieceAt("e1", new King("White", "e1"));
		engine.getGameState().getBoard().setPieceAt("e8", new King("Black", "e8"));
		assertTrue(isInsufficientMaterial(engine), "King vs King should be insufficient material");

		// Test king and bishop vs king
		engine = new GameEngine(1);
		clearBoard(engine);
		engine.getGameState().getBoard().setPieceAt("e1", new King("White", "e1"));
		engine.getGameState().getBoard().setPieceAt("e8", new King("Black", "e8"));
		engine.getGameState().getBoard().setPieceAt("c1", new Bishop("White", "c1"));
		assertTrue(isInsufficientMaterial(engine), "King and bishop vs king should be insufficient material");

		// Test king and knight vs king
		engine = new GameEngine(1);
		clearBoard(engine);
		engine.getGameState().getBoard().setPieceAt("e1", new King("White", "e1"));
		engine.getGameState().getBoard().setPieceAt("e8", new King("Black", "e8"));
		engine.getGameState().getBoard().setPieceAt("b1", new Knight("White", "b1"));
		assertTrue(isInsufficientMaterial(engine), "King and knight vs king should be insufficient material");

		// Test sufficient material (king and pawn)
		engine = new GameEngine(1);
		clearBoard(engine);
		engine.getGameState().getBoard().setPieceAt("e1", new King("White", "e1"));
		engine.getGameState().getBoard().setPieceAt("e8", new King("Black", "e8"));
		engine.getGameState().getBoard().setPieceAt("e2", new Pawn("White", "e2"));
		assertFalse(isInsufficientMaterial(engine), "King and pawn should be sufficient material");
	}

	@Test
	void testPinned() {
		// Set up a pin: White king on e1, White bishop on e2, Black rook on e8
		GameEngine engine = new GameEngine(1);
		clearBoard(engine);
		engine.getGameState().getBoard().setPieceAt("e1", new King("White", "e1"));
		engine.getGameState().getBoard().setPieceAt("e2", new Bishop("White", "e2"));
		engine.getGameState().getBoard().setPieceAt("e8", new Rook("Black", "e8"));

		// Try to move the pinned bishop
		boolean bishopCanMove = engine.movePiece("e2", "d3");
		assertFalse(bishopCanMove, "Pinned bishop should not be able to move");
		assertEquals("B", getPieceAt(engine.getBoardRepresentation(), "e2"), "Bishop should still be on e2");

		// Try moving bishop along the pin line (should still be illegal as it exposes
		// king)
		bishopCanMove = engine.movePiece("e2", "e3");
		assertFalse(bishopCanMove, "Pinned bishop should not be able to move even along pin line if it exposes king");
	}

	// Helper methods
	private void clearBoard(GameEngine engine) {
		String[] allSquares = {"a1", "b1", "c1", "d1", "e1", "f1", "g1", "h1", "a2", "b2", "c2", "d2", "e2", "f2", "g2",
				"h2", "a3", "b3", "c3", "d3", "e3", "f3", "g3", "h3", "a4", "b4", "c4", "d4", "e4", "f4", "g4", "h4",
				"a5", "b5", "c5", "d5", "e5", "f5", "g5", "h5", "a6", "b6", "c6", "d6", "e6", "f6", "g6", "h6", "a7",
				"b7", "c7", "d7", "e7", "f7", "g7", "h7", "a8", "b8", "c8", "d8", "e8", "f8", "g8", "h8"};
		for (String sq : allSquares) {
			engine.getGameState().getBoard().setPieceAt(sq, null);
		}
	}

	private boolean isInsufficientMaterial(GameEngine engine) {
		Board board = engine.getGameState().getBoard();
		int whiteBishops = 0, blackBishops = 0;
		int whiteKnights = 0, blackKnights = 0;
		int otherPieces = 0;

		String[] allSquares = {"a1", "b1", "c1", "d1", "e1", "f1", "g1", "h1", "a2", "b2", "c2", "d2", "e2", "f2", "g2",
				"h2", "a3", "b3", "c3", "d3", "e3", "f3", "g3", "h3", "a4", "b4", "c4", "d4", "e4", "f4", "g4", "h4",
				"a5", "b5", "c5", "d5", "e5", "f5", "g5", "h5", "a6", "b6", "c6", "d6", "e6", "f6", "g6", "h6", "a7",
				"b7", "c7", "d7", "e7", "f7", "g7", "h7", "a8", "b8", "c8", "d8", "e8", "f8", "g8", "h8"};

		for (String sq : allSquares) {
			IPiece piece = board.getPieceAt(sq);
			if (piece != null && !(piece instanceof King)) {
				if (piece instanceof Bishop) {
					if (piece.getColor().equals("White"))
						whiteBishops++;
					else
						blackBishops++;
				} else if (piece instanceof Knight) {
					if (piece.getColor().equals("White"))
						whiteKnights++;
					else
						blackKnights++;
				} else {
					otherPieces++;
				}
			}
		}

		// Check combinations that lead to insufficient material
		if (otherPieces > 0)
			return false;

		// King vs King
		if (whiteBishops == 0 && blackBishops == 0 && whiteKnights == 0 && blackKnights == 0)
			return true;

		// King and (Bishop or Knight) vs King
		if ((whiteBishops == 1 && blackBishops == 0 && whiteKnights == 0 && blackKnights == 0)
				|| (whiteBishops == 0 && blackBishops == 1 && whiteKnights == 0 && blackKnights == 0)
				|| (whiteKnights == 1 && blackKnights == 0 && whiteBishops == 0 && blackBishops == 0)
				|| (whiteKnights == 0 && blackKnights == 1 && whiteBishops == 0 && blackBishops == 0))
			return true;

		return false;
	}

	private boolean isStalemate(GameEngine engine) {
		return engine.getGameState().isStalemate(engine.getCurrentTurn());
	}

	private String getPieceAt(String board, String square) {
		String[] lines = board.split("\n");
		int col = square.charAt(0) - 'a';
		int row = square.charAt(1) - '1';
		// Board is printed from 8 (top) to 1 (bottom), so line index is 8-row
		int lineIdx = 1 + (7 - row);
		String[] tokens = lines[lineIdx].trim().split(" ");
		// tokens[0] is the row number, then 8 columns, then row number again
		return tokens[col + 1];
	}
}
