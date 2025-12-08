package com.ddemott.chessai.pieces;

import com.ddemott.chessai.Board;
import com.ddemott.chessai.Side;

import java.util.List;

public interface IPiece {
	Side getSide();
	String getColor(); // Deprecated, kept for backward compatibility if needed, or we remove it?
						// Let's keep it but make it return side.toString() for now to minimize breakage
						// in tests
	String getPosition();
	void setPosition(String position);
	boolean isValidMove(String newPosition, Board board);
	List<String> getAllPossibleMoves(Board board);
	int getValue();
	IPiece clonePiece();
	boolean hasMoved();
	void setHasMoved(boolean hasMoved);
	char getSymbol();
}