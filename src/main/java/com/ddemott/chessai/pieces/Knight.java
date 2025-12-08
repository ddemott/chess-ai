package com.ddemott.chessai.pieces;

import com.ddemott.chessai.Board;
import com.ddemott.chessai.GameConstants;
import com.ddemott.chessai.Side;

import java.util.ArrayList;
import java.util.List;

public class Knight extends Piece {

	public Knight(Side side, String position) {
		super(side, position);
	}

	// Legacy constructor
	public Knight(String color, String position) {
		super(color, position);
	}

	@Override
	public boolean isValidMove(String newPosition, Board board) {
		String currentPosition = getPosition();
		int[] currentCoords = board.convertPositionToCoordinates(currentPosition);
		int[] newCoords = board.convertPositionToCoordinates(newPosition);

		int rowDiff = Math.abs(newCoords[0] - currentCoords[0]);
		int colDiff = Math.abs(newCoords[1] - currentCoords[1]);

		if ((rowDiff == 2 && colDiff == 1) || (rowDiff == 1 && colDiff == 2)) {
			IPiece destinationPiece = board.getPieceAt(newPosition);
			return destinationPiece == null || destinationPiece.getSide() != side;
		}

		return false;
	}

	@Override
	public List<String> getAllPossibleMoves(Board board) {
		List<String> possibleMoves = new ArrayList<>();
		String currentPosition = getPosition();
		int[] currentCoords = board.convertPositionToCoordinates(currentPosition);

		int[][] directions = {{2, 1}, {2, -1}, {-2, 1}, {-2, -1}, {1, 2}, {1, -2}, {-1, 2}, {-1, -2}};

		for (int[] direction : directions) {
			int row = currentCoords[0] + direction[0];
			int col = currentCoords[1] + direction[1];
			if (row >= 0 && row < 8 && col >= 0 && col < 8) {
				String newPosition = board.convertCoordinatesToPosition(row, col);
				if (isValidMove(newPosition, board)) {
					possibleMoves.add(currentPosition + " " + newPosition);
				}
			}
		}

		return possibleMoves;
	}

	@Override
	public int getValue() {
		return GameConstants.KNIGHT_VALUE;
	}

	@Override
	public IPiece clonePiece() {
		Knight cloned = new Knight(side, position);
		cloned.setHasMoved(this.hasMoved());
		return cloned;
	}

	@Override
	public char getSymbol() {
		return 'N';
	}
}