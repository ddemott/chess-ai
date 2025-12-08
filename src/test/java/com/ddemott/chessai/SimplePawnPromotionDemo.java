package com.ddemott.chessai;

import com.ddemott.chessai.pieces.*;

/**
 * Simple demonstration of pawn promotion functionality
 */
public class SimplePawnPromotionDemo {

	public static void main(String[] args) {
		System.out.println("=== Pawn Promotion Demonstration ===");

		Board board = new Board();
		board.clearBoard();

		// Set up White pawn about to promote
		board.setPieceAt("e7", new Pawn("White", "e7"));
		board.setPieceAt("f8", new Rook("Black", "f8")); // Enemy piece to capture

		System.out.println("Initial setup:");
		System.out.println("White pawn at e7, Black rook at f8");
		printSimpleBoard(board);

		System.out.println("Step 1: White pawn moves to e8 and promotes to Queen");
		boolean success1 = board.movePiece("e7", "e8", "Q");
		System.out.println("Move successful: " + success1);
		printSimpleBoard(board);

		// Set up another scenario
		board.clearBoard();
		board.setPieceAt("a7", new Pawn("White", "a7"));
		board.setPieceAt("b8", new Knight("Black", "b8"));

		System.out.println("Step 2: White pawn captures on b8 and promotes to Knight");
		boolean success2 = board.movePiece("a7", "b8", "N");
		System.out.println("Capture promotion successful: " + success2);
		printSimpleBoard(board);

		// Test algebraic notation
		System.out.println("Step 3: Testing algebraic notation");
		String notation1 = board.getPromotionNotation("e7", "e8", "Q");
		String notation2 = board.getPromotionNotation("a7", "b8", "N");
		System.out.println("Non-capture promotion notation: " + notation1);
		System.out.println("Capture promotion notation: " + notation2);

		System.out.println("\n✅ Pawn promotion demonstration completed successfully!");
	}

	private static void printSimpleBoard(Board board) {
		System.out.println("  a b c d e f g h");
		for (int row = 7; row >= 0; row--) {
			System.out.print((row + 1) + " ");
			for (int col = 0; col < 8; col++) {
				String position = board.convertCoordinatesToPosition(row, col);
				var piece = board.getPieceAt(position);
				if (piece == null) {
					System.out.print(". ");
				} else {
					char symbol = piece.getClass().getSimpleName().charAt(0);
					if (piece.getColor().equals("White")) {
						symbol = Character.toUpperCase(symbol);
					} else {
						symbol = Character.toLowerCase(symbol);
					}
					System.out.print(symbol + " ");
				}
			}
			System.out.println((row + 1));
		}
		System.out.println("  a b c d e f g h\n");
	}
}
