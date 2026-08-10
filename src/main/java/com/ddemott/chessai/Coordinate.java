package com.ddemott.chessai;

/**
 * Represents a coordinate on the chess board (row, col). Row 0 = Rank 1, Row 7
 * = Rank 8. Col 0 = File 'a', Col 7 = File 'h'.
 */
public record Coordinate(int row, int col) {

	public Coordinate {
		if (row < 0 || row >= GameConstants.BOARD_SIZE || col < 0
		        || col >= GameConstants.BOARD_SIZE) {
			throw new IllegalArgumentException("Coordinate out of bounds: " + row + "," + col);
		}
	}

	/**
	 * Converts an algebraic notation string (e.g., "e4") to a Coordinate.
	 *
	 * @param position
	 *            The position string.
	 * @return The Coordinate, or null if invalid.
	 */
	public static Coordinate fromString(String position) {
		if (position == null) {
			return null;
		}
		position = position.toLowerCase();
		if (position.length() != 2) {
			return null;
		}

		char column = position.charAt(0);
		int row = position.charAt(1) - '1';
		int col = column - 'a';

		if (row < 0 || row >= GameConstants.BOARD_SIZE || col < 0
		        || col >= GameConstants.BOARD_SIZE) {
			return null;
		}

		return new Coordinate(row, col);
	}

	/**
	 * Converts this coordinate to algebraic notation (e.g., "e4").
	 *
	 * @return The position string.
	 */
	public String toString() {
		char file = (char) ('a' + col);
		int rank = row + 1;
		return "" + file + rank;
	}
}
