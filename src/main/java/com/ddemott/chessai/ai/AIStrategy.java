package com.ddemott.chessai.ai;

import com.ddemott.chessai.State;

public interface AIStrategy {
    String calculateBestMove(State state, String color);
}
