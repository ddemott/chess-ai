package com.ddemott.chessai.pieces;

import com.ddemott.chessai.Board;

public abstract class Piece implements IPiece {
    protected String color;
    protected String position;

    public Piece(String color, String position) {
        this.color = color;
        this.position = position;
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
    }

    @Override
    public abstract boolean isValidMove(String newPosition, Board board);

    @Override
    public abstract int getValue();

    @Override
    public abstract IPiece clonePiece();
}
