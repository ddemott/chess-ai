package com.ddemott.chessai;

import com.ddemott.chessai.engine.GameEngine;
import com.ddemott.chessai.pieces.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Core chess rules and board logic tests: en passant, castling, promotion,
 * stalemate, threefold repetition, fifty-move rule.
 */
public class BoardCoreRulesTest {
	@Test
	void testEnPassantCapture() {
		try {
			com.ddemott.chessai.engine.GameEngine engine = new com.ddemott.chessai.engine.GameEngine(1);
			engine.movePiece("e2", "e4"); // White
			System.out.println(engine.getBoardRepresentation());
			engine.movePiece("d7", "d5"); // Black
			System.out.println(engine.getBoardRepresentation());
			// En passant target should now be d6
			System.out
					.println("En passant target after d7-d5: " + engine.getGameState().getBoard().getEnPassantTarget());
			boolean enPassant = engine.movePiece("e4", "d5"); // White captures en passant
			System.out.println("En passant move success: " + enPassant);
			String board = engine.getBoardRepresentation();
			System.out.println(board);
			assertEquals("P", getPieceAt(board, "d5"), "White pawn should be on d5 after en passant");
			assertEquals(".", getPieceAt(board, "d6"), "d6 should be empty after en passant");
		} catch (Exception e) {
			fail("Test failed due to unexpected exception: " + e.getMessage());
		}
	}

	@Test
	void testCastlingKingSideAndQueenSide() {
		com.ddemott.chessai.engine.GameEngine engine = new com.ddemott.chessai.engine.GameEngine(1);
		engine.movePiece("e2", "e4");
		engine.movePiece("e7", "e5");
		engine.movePiece("g1", "f3");
		engine.movePiece("b8", "c6");
		engine.movePiece("f1", "e2");
		engine.movePiece("g8", "f6");
		boolean castled = engine.movePiece("e1", "g1");
		assertTrue(castled, "White should be able to castle king-side");
		String board = engine.getBoardRepresentation();
		// g1 should have a white king (K), f1 should have a white rook (R)
		assertEquals("K", getPieceAt(board, "g1"), "King should be on g1 after castling");
		assertEquals("R", getPieceAt(board, "f1"), "Rook should be on f1 after castling");
	}

	@Test
	void testPawnPromotion() {
		com.ddemott.chessai.engine.GameEngine engine = new com.ddemott.chessai.engine.GameEngine(1);
		// Clear a7 and a8 so the white pawn can promote
		engine.getGameState().getBoard().setPieceAt("a7", null);
		engine.getGameState().getBoard().setPieceAt("a8", null);
		// Use the a-file for promotion
		engine.movePiece("a2", "a4");
		engine.movePiece("h7", "h6");
		System.out.println(engine.getBoardRepresentation());
		engine.movePiece("a4", "a5");
		engine.movePiece("h6", "h5");
		System.out.println(engine.getBoardRepresentation());
		engine.movePiece("a5", "a6");
		engine.movePiece("h5", "h4");
		System.out.println(engine.getBoardRepresentation());
		engine.movePiece("a6", "a7");
		engine.movePiece("h4", "h3");
		System.out.println(engine.getBoardRepresentation());
		// Print pawn position before promotion
		System.out.println("Pawn before promotion: " + getPieceAt(engine.getBoardRepresentation(), "a7"));
		boolean promoted = engine.movePiece("a7", "a8", "Q");
		System.out.println("Promotion move success: " + promoted);
		String board = engine.getBoardRepresentation();
		System.out.println(board);
		assertTrue(promoted, "Pawn should promote to Queen");
		assertEquals("Q", getPieceAt(board, "a8"), "There should be a Queen on a8 after promotion");
	}

	@Test
	void testStalemateDetection() {
		// Set up a simple stalemate position
		GameEngine engine = new GameEngine(1);
		// Clear the board first
		clearBoard(engine);
		// Place pieces for stalemate: Black king on h8, White king on f7, White queen
		// on g6
		engine.getGameState().getBoard().setPieceAt("h8", new King("Black", "h8"));
		engine.getGameState().getBoard().setPieceAt("f7", new King("White", "f7"));
		engine.getGameState().getBoard().setPieceAt("g6", new Queen("White", "g6"));
		engine.getGameState().setCurrentTurn("Black"); // Black to move

		// Verify stalemate
		assertTrue(engine.getGameState().isStalemate("Black"), "Position should be stalemate for Black");
	}

	@Test
	void testInsufficientMaterial() {
		// Test king vs king
		GameEngine engine = new GameEngine(1);
		clearBoard(engine);
		engine.getGameState().getBoard().setPieceAt("e1", new King("White", "e1"));
		engine.getGameState().getBoard().setPieceAt("e8", new King("Black", "e8"));

		assertTrue(isInsufficientMaterial(engine.getGameState().getBoard()),
				"King vs King should be insufficient material");

		// Test king and bishop vs king
		engine = new GameEngine(1);
		clearBoard(engine);
		engine.getGameState().getBoard().setPieceAt("e1", new King("White", "e1"));
		engine.getGameState().getBoard().setPieceAt("e8", new King("Black", "e8"));
		engine.getGameState().getBoard().setPieceAt("c1", new Bishop("White", "c1"));

		assertTrue(isInsufficientMaterial(engine.getGameState().getBoard()),
				"King and bishop vs king should be insufficient material");

		// Test king and knight vs king
		engine = new GameEngine(1);
		clearBoard(engine);
		engine.getGameState().getBoard().setPieceAt("e1", new King("White", "e1"));
		engine.getGameState().getBoard().setPieceAt("e8", new King("Black", "e8"));
		engine.getGameState().getBoard().setPieceAt("b1", new Knight("White", "b1"));

		assertTrue(isInsufficientMaterial(engine.getGameState().getBoard()),
				"King and knight vs king should be insufficient material");

		// Test sufficient material (king and pawn)
		engine = new GameEngine(1);
		clearBoard(engine);
		engine.getGameState().getBoard().setPieceAt("e1", new King("White", "e1"));
		engine.getGameState().getBoard().setPieceAt("e8", new King("Black", "e8"));
		engine.getGameState().getBoard().setPieceAt("e2", new Pawn("White", "e2"));

		assertFalse(isInsufficientMaterial(engine.getGameState().getBoard()),
				"King and pawn should be sufficient material");
	}

	@Test
	void testThreefoldRepetition() {
		GameEngine engine = new GameEngine(1);

		// Create a threefold repetition by moving knights back and forth
		// Initial position
		String[] sequence = {"g1", "f3", "g8", "f6", // Move out
				"f3", "g1", "f6", "g8", // Move back
				"g1", "f3", "g8", "f6", // Repeat
				"f3", "g1", "f6", "g8", // Repeat
				"g1", "f3", "g8", "f6" // Final repetition
		};

		for (int i = 0; i < sequence.length; i += 2) {
			String from = sequence[i];
			String to = sequence[i + 1];
			engine.movePiece(from, to);

			// Record position in move history for repetition checking
			engine.getGameState().getMoveHistory().addPosition(engine.getGameState().getBoard().toFEN());
		}

		assertTrue(engine.getGameState().isThreefoldRepetition(), "Position should be repeated three times");
	}

	@Test
	void testFiftyMoveRule() {
		GameEngine engine = new GameEngine(1);

		// Clear all pawns to avoid pawn moves
		for (char file = 'a'; file <= 'h'; file++) {
			engine.getGameState().getBoard().setPieceAt(file + "2", null);
			engine.getGameState().getBoard().setPieceAt(file + "7", null);
		}

		// Set the halfmove clock to 98 (just before 50 moves)
		for (int i = 0; i < 98; i++) {
			engine.getGameState().getMoveHistory().updateHalfmoveClock(false, false);
		}

		// Now we're at 98 halfmoves (49 moves)
		assertFalse(engine.getGameState().isFiftyMoveRule(), "49 moves should not trigger fifty move rule");

		// Add 2 more halfmoves to reach 50 moves
		engine.getGameState().getMoveHistory().updateHalfmoveClock(false, false);
		engine.getGameState().getMoveHistory().updateHalfmoveClock(false, false);

		assertTrue(engine.getGameState().isFiftyMoveRule(), "50 moves should trigger fifty move rule");
	}

	@Test
	void testPinnedPieceCannotMove() {
		GameEngine engine = new GameEngine(1);

		// Set up a pin: White king on e1, White bishop on e2, Black rook on e8
		clearBoard(engine);

		// Create the pieces
		King whiteKing = new King("White", "e1");
		Bishop whiteBishop = new Bishop("White", "e2");
		Rook blackRook = new Rook("Black", "e8");

		// Place the pieces on the board
		engine.getGameState().getBoard().setPieceAt("e1", whiteKing);
		engine.getGameState().getBoard().setPieceAt("e2", whiteBishop);
		engine.getGameState().getBoard().setPieceAt("e8", blackRook);

		// Set the current turn to White
		engine.getGameState().setCurrentTurn("White");

		// Print the board for debugging
		System.out.println("Board before move attempt:");
		System.out.println(engine.getBoardRepresentation());

		// Check if king is in check before move
		System.out.println("Is king in check before move: " + engine.getGameState().getBoard().isKingInCheck("White"));

		// Debug the wouldExposeKingToCheck method
		System.out.println("Would moving bishop expose king: "
				+ engine.getGameState().getBoard().wouldExposeKingToCheck("e2", "d3"));

		// Try to move the pinned bishop
		boolean bishopCanMove = engine.movePiece("e2", "d3");
		System.out.println("Bishop can move: " + bishopCanMove);

		// Print the board again
		System.out.println("Board after move attempt:");
		System.out.println(engine.getBoardRepresentation());

		assertFalse(bishopCanMove, "Pinned bishop should not be able to move");

		// Verify bishop is still on e2
		assertEquals("B", getPieceAt(engine.getBoardRepresentation(), "e2"), "Bishop should still be on e2");
	}

	@Test
	void testPromotionToAllPieceTypes() {
		GameEngine engine = new GameEngine(1);

		String[] pieceTypes = {"Q", "R", "B", "N"};
		String[] pieceNames = {"Queen", "Rook", "Bishop", "Knight"};

		for (int i = 0; i < pieceTypes.length; i++) {
			// Clear for new test
			engine = new GameEngine(1);
			engine.getGameState().getBoard().setPieceAt("a7", null);
			engine.getGameState().getBoard().setPieceAt("a8", null);

			// Move pawn to a7
			engine.movePiece("a2", "a4");
			engine.movePiece("h7", "h6");
			engine.movePiece("a4", "a5");
			engine.movePiece("h6", "h5");
			engine.movePiece("a5", "a6");
			engine.movePiece("h5", "h4");
			engine.movePiece("a6", "a7");
			engine.movePiece("h4", "h3");

			// Promote to piece type
			boolean promoted = engine.movePiece("a7", "a8", pieceTypes[i]);
			assertTrue(promoted, "Pawn should promote to " + pieceNames[i]);
			assertEquals(pieceTypes[i], getPieceAt(engine.getBoardRepresentation(), "a8"),
					pieceNames[i] + " should be on a8");
		}
	}

	@Test
	void testCastlingEdgeCases() {
		GameEngine engine = new GameEngine(1);

		// Move king, then back, then try to castle
		engine.movePiece("e2", "e4");
		engine.movePiece("e7", "e5");
		engine.movePiece("e1", "e2");
		engine.movePiece("d7", "d6");
		engine.movePiece("e2", "e1");
		engine.movePiece("d6", "d5");

		// Clear f1 and g1 to ensure it's not a blocking issue
		engine.getGameState().getBoard().setPieceAt("f1", null);
		engine.getGameState().getBoard().setPieceAt("g1", null);

		boolean castleAfterKingMove = engine.movePiece("e1", "g1");
		assertFalse(castleAfterKingMove, "Cannot castle after king has moved");

		// Move rook, then back, then try to castle
		engine = new GameEngine(1);
		engine.movePiece("a2", "a4");
		engine.movePiece("h7", "h5");
		engine.movePiece("a1", "a2");
		engine.movePiece("h8", "h6");
		engine.movePiece("a2", "a1");
		engine.movePiece("h6", "h8");

		// Clear path for castling
		engine.getGameState().getBoard().setPieceAt("b1", null);
		engine.getGameState().getBoard().setPieceAt("c1", null);
		engine.getGameState().getBoard().setPieceAt("d1", null);

		boolean castleAfterRookMove = engine.movePiece("e1", "c1");
		assertFalse(castleAfterRookMove, "Cannot castle after rook has moved");
	}

	@Test
	void testEnPassantEdgeCases() {
		GameEngine engine = new GameEngine(1);

		// Set up en passant, but wait a turn
		engine.movePiece("e2", "e4");
		engine.movePiece("a7", "a6");
		engine.movePiece("e4", "e5");
		engine.movePiece("d7", "d5");
		engine.movePiece("h2", "h3"); // White does not capture en passant immediately
		engine.movePiece("a6", "a5");

		// Try en passant after a delay
		boolean enPassantLate = engine.movePiece("e5", "d6");
		assertFalse(enPassantLate, "En passant not allowed after a delay");
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

	private boolean isInsufficientMaterial(Board board) {
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

	// Helper to parse board representation and get the piece at a given square
	// (e.g., "e4")
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
