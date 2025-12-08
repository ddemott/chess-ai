package com.ddemott.chessai;

/**
 * Simple castling demo
 */
public class SimpleCastlingDemo {

	public static void main(String[] args) {
		System.out.println("=== Simple Castling Demo ===");

		Board board = new Board();

		System.out.println("Initial board:");
		System.out.println(board.getBoardRepresentation());

		// Clear pieces manually for kingside castling
		board.setPieceAt("f1", null); // Remove bishop
		board.setPieceAt("g1", null); // Remove knight

		System.out.println("After clearing f1 and g1:");
		System.out.println(board.getBoardRepresentation());

		// Check king hasn't moved
		System.out.println("King has moved: " + board.getPieceAt("e1").hasMoved());
		System.out.println("Rook has moved: " + board.getPieceAt("h1").hasMoved());

		// Try castling
		System.out.println("Attempting castling e1 to g1...");
		boolean success = board.movePiece("e1", "g1");

		System.out.println("Castling successful: " + success);

		if (success) {
			System.out.println("Final board:");
			System.out.println(board.getBoardRepresentation());

			System.out.println("King at g1: " + (board.getPieceAt("g1") != null));
			System.out.println("Rook at f1: " + (board.getPieceAt("f1") != null));
			System.out.println("✓ SIMPLE CASTLING DEMO PASSED!");
		} else {
			System.out.println("✗ Castling failed");
		}
	}
}
