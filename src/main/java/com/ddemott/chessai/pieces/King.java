package com.ddemott.chessai.pieces;

import java.util.ArrayList;
import java.util.List;

import com.ddemott.chessai.Board;
import com.ddemott.chessai.GameConstants;
import com.ddemott.chessai.Side;

public class King extends Piece {

	public King(Side side, String position) {
		super(side, position);
	}

	// Legacy constructor
	public King(String color, String position) {
		super(color, position);
	}

	@Override
	public boolean isValidMove(String newPosition, Board board) {
		int[] currentCoords = board.convertPositionToCoordinates(this.getPosition());
		String currentPosition = this.getPosition();
		int[] newCoords = board.convertPositionToCoordinates(newPosition);
		if (currentCoords == null || newCoords == null) {
			return false;
		}
		// Ensure move is within board boundaries
		if (newCoords[0] < 0 || newCoords[0] > 7 || newCoords[1] < 0 || newCoords[1] > 7) {
			return false;
		}
		int rowDiff = Math.abs(newCoords[0] - currentCoords[0]);
		int colDiff = Math.abs(newCoords[1] - currentCoords[1]);

		// Only allow single-square moves (not castling)
		if ((rowDiff <= 1 && colDiff <= 1) && (rowDiff + colDiff > 0)) {
			// Destination must be empty or contain opponent's piece
			IPiece dest = board.getPieceAt(newPosition);
			if (dest != null && dest.getSide() == side) {
				return false;
			}
			// Prevent moving into check - simulate the move on a cloned board (to handle
			// captures correctly)
			String destSquare = newPosition;
			try {
				Board clonedBoard = board.clone();
				IPiece clonedKing = clonedBoard.getPieceAt(currentPosition);
				if (clonedKing != null) {
					clonedKing.setPosition(destSquare);
					clonedBoard.setPieceAt(destSquare, clonedKing);
					clonedBoard.setPieceAt(currentPosition, null);
					if (clonedBoard.isSquareUnderAttack(destSquare, side.toString())) {
						return false;
					}
				} else {
					// Fallback - if we can't simulate, be conservative and disallow
					if (board.isSquareUnderAttack(destSquare, side.toString())) {
						return false;
					}
				}
			} catch (NullPointerException e) {
				return false;
			}
			return true;
		}

		// Castling move: move two squares horizontally and no vertical movement, only
		// from starting rank
		if (!this.hasMoved() && rowDiff == 0 && colDiff == 2
				&& (this.getPosition().equals("e1") || this.getPosition().equals("e8"))) {
			// Convert board coordinates to square notation, e.g., e1
			String kingFile = String.valueOf((char) ('a' + currentCoords[1]));
			String kingRank = String.valueOf(currentCoords[0] + 1);
			// Castling is illegal if the king is in check
			try {
				if (board.isSquareUnderAttack(kingFile + kingRank, side.toString())) {
					return false;
				}
			} catch (NullPointerException e) {
				return false;
			}
			if (newCoords[1] > currentCoords[1]) { // Kingside castling
				// Compute squares between king and rook
				String square1File = String.valueOf((char) ('a' + currentCoords[1] + 1));
				String square2File = String.valueOf((char) ('a' + currentCoords[1] + 2));
				String rankStr = kingRank;
				String square1Pos = square1File + rankStr;
				String square2Pos = square2File + rankStr;
				// Check that the squares are empty
				if (board.getPieceAt(square1Pos) != null || board.getPieceAt(square2Pos) != null) {
					return false;
				}
				// Replace castling validation to inline try-catch for isSquareUnderAttack:
				boolean kingsideSafe;
				try {
					kingsideSafe = !board.isSquareUnderAttack("f" + rankStr, side.toString())
							&& !board.isSquareUnderAttack("g" + rankStr, side.toString());
				} catch (NullPointerException e) {
					kingsideSafe = false;
				}
				if (!kingsideSafe) {
					return false;
				}
			} else { // Queenside castling
				String square1File = String.valueOf((char) ('a' + currentCoords[1] - 1));
				String square2File = String.valueOf((char) ('a' + currentCoords[1] - 2));
				String square3File = String.valueOf((char) ('a' + currentCoords[1] - 3));
				String rankStr = kingRank;
				String square1Pos = square1File + rankStr;
				String square2Pos = square2File + rankStr;
				String square3Pos = square3File + rankStr;
				// Check that the squares between king and rook are empty
				if (board.getPieceAt(square1Pos) != null || board.getPieceAt(square2Pos) != null
						|| board.getPieceAt(square3Pos) != null) {
					return false;
				}
				// In castling validation, replace direct calls to board.isSquareUnderAttack
				// with safeUnderAttack to avoid issues
				boolean queensideSafe;
				try {
					queensideSafe = !board.isSquareUnderAttack("d" + rankStr, side.toString())
							&& !board.isSquareUnderAttack("c" + rankStr, side.toString());
				} catch (NullPointerException e) {
					queensideSafe = false;
				}
				if (!queensideSafe) {
					return false;
				}
			}

			// Additional validation: the rook involved must not have moved
			String rookPos = newCoords[1] > currentCoords[1]
					? ("h" + kingRank) // kingside rook
					: ("a" + kingRank); // queenside rook
			com.ddemott.chessai.pieces.IPiece rook = board.getPieceAt(rookPos);
			if (rook == null || !(rook instanceof com.ddemott.chessai.pieces.Rook) || rook.getSide() != side
					|| rook.hasMoved()) {
				return false;
			}
			return true;
		}

		return false;
	}

	@Override
	public int getValue() {
		return GameConstants.KING_VALUE;
	}

	@Override
	public IPiece clonePiece() {
		King cloned = new King(side, position);
		cloned.setHasMoved(this.hasMoved());
		return cloned;
	}

	@Override
	public char getSymbol() {
		return 'K';
	}

	@Override
	public List<String> getAllPossibleMoves(Board board) {
		List<String> possibleMoves = new ArrayList<>();
		String currentPosition = getPosition();
		int[] currentCoords = board.convertPositionToCoordinates(currentPosition);

		// Standard moves
		int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}, {1, 1}, {1, -1}, {-1, 1}, {-1, -1}};

		for (int[] direction : directions) {
			int row = currentCoords[0] + direction[0];
			int col = currentCoords[1] + direction[1];
			if (row >= 0 && row < 8 && col >= 0 && col < 8) {
				String newMove = board.convertCoordinatesToPosition(row, col);
				if (isValidMove(newMove, board)) {
					possibleMoves.add(currentPosition + " " + newMove);
				}
			}
		}

		// Castling moves (hardcoded checks based on standard positions)
		// Note: isValidMove handles the logic validation
		if (side == Side.WHITE) {
			if (currentPosition.equals("e1")) {
				if (isValidMove("g1", board))
					possibleMoves.add("e1 g1");
				if (isValidMove("c1", board))
					possibleMoves.add("e1 c1");
			}
		} else {
			if (currentPosition.equals("e8")) {
				if (isValidMove("g8", board))
					possibleMoves.add("e8 g8");
				if (isValidMove("c8", board))
					possibleMoves.add("e8 c8");
			}
		}

		return possibleMoves;
	}
}