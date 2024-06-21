package com.ddemott.chessai.ai;

public class MoveResult {
    private final int value;
    private final String move;

    public MoveResult(int value, String move) {
        this.value = value;
        this.move = move;
    }

    public int getValue() {
        return value;
    }

    public String getMove() {
        return move;
    }
}
