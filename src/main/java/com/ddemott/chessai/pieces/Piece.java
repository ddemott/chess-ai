package com.ddemott.chessai.pieces;

import com.ddemott.chessai.Board;
import com.ddemott.chessai.Side;

public abstract class Piece implements IPiece {
	protected Side side;
	protected String position;
	protected boolean hasMoved;

	// New constructor using Enum
	public Piece(Side side, String position) {
		this.side = side;
		this.position = position;
		this.hasMoved = false;
	}

	// Legacy constructor for backward compatibility during refactor
	// We can remove this later, but it helps avoid compilation errors in tests
	// immediately
	public Piece(String color, String position) {
		this.side = color.equalsIgnoreCase("White") ? Side.WHITE : Side.BLACK;
		this.position = position;
		this.hasMoved = false;
	}

	@Override
	public Side getSide() {
		return side;
	}

	@Override
	public String getColor() {
		return side.toString();
	}

	@Override
	public String getPosition() {
		return position;
	}

	@Override
	public void setPosition(String position) {
		this.position = position;
		// setHasMoved(true) should probably be called by the board when actually
		// moving,
		// but the original code had it here. I'll keep the logic but move the
		// assignment.
	}

	@Override
	public boolean hasMoved() {
		return hasMoved;
	}

	@Override
	public void setHasMoved(boolean hasMoved) {
		this.hasMoved = hasMoved;
	}

	@Override
	public abstract boolean isValidMove(String newPosition, Board board);

	@Override
	public abstract int getValue();

	@Override
	public abstract IPiece clonePiece();

	@Override
	public abstract char getSymbol();
}