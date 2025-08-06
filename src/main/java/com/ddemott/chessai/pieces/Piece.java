package com.ddemott.chessai.pieces;

import com.ddemott.chessai.Board;

public abstract class Piece implements IPiece {
    protected String color;
    protected String position;
    protected boolean hasMoved;

    public Piece(String color, String position) {
        this.color = color;
        this.position = position;
        this.hasMoved = false;
    }

    @Override
    public String getColor() {
        return color;
    }

    @Override
    public String getPosition() {
        return position;
    }

    @Override
    public void setPosition(String position) {
        this.position = position;
        this.hasMoved = true;
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
}
