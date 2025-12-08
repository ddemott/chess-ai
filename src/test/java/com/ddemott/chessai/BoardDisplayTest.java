package com.ddemott.chessai;

import org.junit.jupiter.api.Test;

public class BoardDisplayTest {
	@Test
	public void displayInitialBoard() {
		Board board = new Board();
		System.out.println(board.getBoardRepresentation()); // Should print the initial chess board to the console
	}
}
