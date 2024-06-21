package com.ddemott.chessai.pieces;

import com.ddemott.chessai.Board;

import java.util.List;

public interface IPiece {
    String getColor();
    String getPosition();
    void setPosition(String position);
    boolean isValidMove(String newPosition, Board board);
    List<String> getAllPossibleMoves(Board board);
    int getValue();
    IPiece clonePiece();
}
