package com.ddemott.chessai.interfaces;

public interface IChessGameObserver {
    void onBoardUpdate(String boardRepresentation);

    void onMoveUpdate(String move);

    void onGameEnd(String result);
}
