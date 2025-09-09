package com.ddemott.chessai;

import org.junit.jupiter.api.Test;
import com.ddemott.chessai.Board;

public class BoardDisplayTest {
    @Test
    public void displayInitialBoard() {
        Board board = new Board();
        board.printBoard(); // Should print the initial chess board to the console
    }
}
