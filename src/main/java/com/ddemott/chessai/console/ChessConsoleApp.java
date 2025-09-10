package com.ddemott.chessai.console;

import com.ddemott.chessai.Board;

public class ChessConsoleApp {
    public static void main(String[] args) {
        Board board = new Board();
        ChessConsole console = new ChessConsole(board);
        console.startGame();
    }
}
