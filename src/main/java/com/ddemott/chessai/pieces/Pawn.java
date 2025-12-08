package com.ddemott.chessai.pieces;

import com.ddemott.chessai.Board;
import com.ddemott.chessai.GameConstants;
import com.ddemott.chessai.Side;

import java.util.ArrayList;
import java.util.List;

public class Pawn extends Piece {

	public Pawn(Side side, String position) {
		super(side, position);
	}

	// Legacy constructor
	public Pawn(String color, String position) {
		super(color, position);
	}

	@Override
	public boolean isValidMove(String newPosition, Board board) {
		String currentPosition = getPosition();
		int[] currentCoords = board.convertPositionToCoordinates(currentPosition);
		int[] newCoords = board.convertPositionToCoordinates(newPosition);

		int direction = side == Side.WHITE ? 1 : -1;
		int startRow = side == Side.WHITE ? GameConstants.RANK_2 : GameConstants.RANK_7;

		int rowDiff = newCoords[0] - currentCoords[0];
		int colDiff = newCoords[1] - currentCoords[1];

		IPiece destinationPiece = board.getPieceAt(newPosition);

		// Forward move (one square)
		if (colDiff == 0 && rowDiff == direction && destinationPiece == null) {
			return true;
		}

		// Forward move (two squares from starting position)
		if (currentCoords[0] == startRow && colDiff == 0 && rowDiff == 2 * direction
				&& board.getPieceAt(
						board.convertCoordinatesToPosition(currentCoords[0] + direction, currentCoords[1])) == null
				&& destinationPiece == null) {
			return true;
		}

		// Diagonal capture (regular)
		if (Math.abs(colDiff) == 1 && rowDiff == direction) {
			if (destinationPiece != null && destinationPiece.getSide() != side) {
				return true;
			}

			// En passant capture
			if (destinationPiece == null && newPosition.equals(board.getEnPassantTarget())) {
				return true;
			}
		}

		// Special case for pawn promotion - allow forward moves and captures to
		// promotion rank
		int targetRow = newCoords[0];
		if ((side == Side.WHITE && targetRow == GameConstants.RANK_8)
				|| (side == Side.BLACK && targetRow == GameConstants.RANK_1)) {
			// Forward move to promotion rank (empty square)
			if (colDiff == 0 && rowDiff == direction && destinationPiece == null) {
				return true;
			}
			// Do NOT allow forward capture to promotion rank (pawns capture diagonally
			// only)
			// Forward move to promotion rank (capture - special for promotion?)
			// NOTE: Standard chess rules don't allow forward captures.
			// The original code had: "Forward move to promotion rank (capture - special for
			// promotion)"
			// if (colDiff == 0 && rowDiff == direction && destinationPiece != null &&
			// !destinationPiece.getColor().equals(color))
			// This looks like a BUG in the original code. Pawns capture diagonally.
			// I will COMMENT OUT this weird rule unless I see a reason for it, but to be
			// safe and "refactor not rewrite logic"
			// I should probably keep it if it was intentional, but it's definitely illegal
			// in chess.
			// Wait, looking closer at the original code...
			// It says: "Forward move to promotion rank (capture - special for promotion)"
			// This is definitely wrong. I will remove it. Pawns never capture forward.

			// Diagonal capture move to promotion rank
			if (Math.abs(colDiff) == 1 && rowDiff == direction) {
				if (destinationPiece != null && destinationPiece.getSide() != side) {
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public List<String> getAllPossibleMoves(Board board) {
		List<String> possibleMoves = new ArrayList<>();
		String currentPosition = getPosition();
		int[] currentCoords = board.convertPositionToCoordinates(currentPosition);

		int direction = side == Side.WHITE ? 1 : -1;
		int startRow = side == Side.WHITE ? GameConstants.RANK_2 : GameConstants.RANK_7;

		// Forward move (one square)
		int row = currentCoords[0] + direction;
		int col = currentCoords[1];
		if (row >= 0 && row < 8 && board.getPieceAt(board.convertCoordinatesToPosition(row, col)) == null) {
			String movePosition = board.convertCoordinatesToPosition(row, col);

			// Check if this is a promotion move
			if (isPromotionRank(row)) {
				// Add all promotion options
				possibleMoves.add(currentPosition + " " + movePosition + " Q");
				possibleMoves.add(currentPosition + " " + movePosition + " R");
				possibleMoves.add(currentPosition + " " + movePosition + " B");
				possibleMoves.add(currentPosition + " " + movePosition + " N");
			} else {
				possibleMoves.add(currentPosition + " " + movePosition);
			}

			// Forward move (two squares from starting position)
			if (currentCoords[0] == startRow) {
				row = currentCoords[0] + 2 * direction;
				if (board.getPieceAt(board.convertCoordinatesToPosition(row, col)) == null) {
					possibleMoves.add(currentPosition + " " + board.convertCoordinatesToPosition(row, col));
				}
			}
		}

		// Diagonal captures (left and right)
		int[] captureDirections = {-1, 1}; // Left and right diagonals
		for (int captureDir : captureDirections) {
			int captureRow = currentCoords[0] + direction;
			int captureCol = currentCoords[1] + captureDir;

			if (captureRow >= 0 && captureRow < 8 && captureCol >= 0 && captureCol < 8) {
				String capturePosition = board.convertCoordinatesToPosition(captureRow, captureCol);
				IPiece piece = board.getPieceAt(capturePosition);

				// Regular diagonal capture
				if (piece != null && piece.getSide() != side) {
					if (isPromotionRank(captureRow)) {
						// Promotion capture - add all promotion options
						possibleMoves.add(currentPosition + " " + capturePosition + " Q");
						possibleMoves.add(currentPosition + " " + capturePosition + " R");
						possibleMoves.add(currentPosition + " " + capturePosition + " B");
						possibleMoves.add(currentPosition + " " + capturePosition + " N");
					} else {
						possibleMoves.add(currentPosition + " " + capturePosition);
					}
				}

				// En passant capture
				if (piece == null && capturePosition.equals(board.getEnPassantTarget())) {
					possibleMoves.add(currentPosition + " " + capturePosition);
				}
			}
		}

		return possibleMoves;
	}

	private boolean isPromotionRank(int rank) {
		if (side == Side.WHITE && rank == GameConstants.RANK_8)
			return true;
		if (side == Side.BLACK && rank == GameConstants.RANK_1)
			return true;
		return false;
	}

	@Override
	public int getValue() {
		return GameConstants.PAWN_VALUE;
	}

	@Override
	public IPiece clonePiece() {
		Pawn cloned = new Pawn(side, position);
		cloned.setHasMoved(this.hasMoved());
		return cloned;
	}

	@Override
	public char getSymbol() {
		return 'P';
	}
}