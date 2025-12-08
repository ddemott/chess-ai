package com.ddemott.chessai.pieces;

import java.util.ArrayList;
import java.util.List;

import com.ddemott.chessai.Board;
import com.ddemott.chessai.GameConstants;
import com.ddemott.chessai.Side;

public class Bishop extends Piece {

	public Bishop(Side side, String position) {
		super(side, position);
	}

	// Legacy constructor
	public Bishop(String color, String position) {
		super(color, position);
	}

	@Override
	public boolean isValidMove(String destination, Board board) {
		String currentPosition = getPosition();
		if (destination == null || destination.length() != 2) {
			return false;
		}
		int[] startCoords = board.convertPositionToCoordinates(currentPosition);
		int[] endCoords = board.convertPositionToCoordinates(destination);
		if (startCoords == null || endCoords == null) {
			return false;
		}
		// Check for valid diagonal move: absolute difference between file and rank
		// should be equal
		if (Math.abs(endCoords[1] - startCoords[1]) == Math.abs(endCoords[0] - startCoords[0])) {
			// Check if path is clear
			if (isPathClear(startCoords, endCoords, board)) {
				// Destination must be empty or contain opponent's piece
				IPiece destPiece = board.getPieceAt(destination);
				if (destPiece == null || destPiece.getSide() != side) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean isPathClear(int[] currentCoords, int[] newCoords, Board board) {
		int dx = Integer.signum(newCoords[0] - currentCoords[0]);
		int dy = Integer.signum(newCoords[1] - currentCoords[1]);
		int x = currentCoords[0] + dx;
		int y = currentCoords[1] + dy;

		while (x != newCoords[0] || y != newCoords[1]) {
			if (board.getPieceAt(board.convertCoordinatesToPosition(x, y)) != null) {
				return false; // Path is blocked
			}
			x += dx;
			y += dy;
		}

		return true; // Path is clear
	}

	@Override
	public int getValue() {
		return GameConstants.BISHOP_VALUE;
	}

	@Override
	public IPiece clonePiece() {
		Bishop cloned = new Bishop(side, position);
		cloned.setHasMoved(this.hasMoved());
		return cloned;
	}

	@Override
	public char getSymbol() {
		return 'B';
	}

	@Override
	public List<String> getAllPossibleMoves(Board board) {
		List<String> possibleMoves = new ArrayList<>();
		String currentPosition = getPosition();
		int[] currentCoords = board.convertPositionToCoordinates(currentPosition);

		int[][] directions = {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}};

		for (int[] direction : directions) {
			int row = currentCoords[0];
			int col = currentCoords[1];
			while (true) {
				row += direction[0];
				col += direction[1];
				if (row >= 0 && row < 8 && col >= 0 && col < 8) {
					String newPosition = board.convertCoordinatesToPosition(row, col);
					if (isValidMove(newPosition, board)) {
						possibleMoves.add(currentPosition + " " + newPosition);
						if (board.getPieceAt(newPosition) != null)
							break;
					} else {
						break;
					}
				} else {
					break;
				}
			}
		}
		return possibleMoves;
	}
}