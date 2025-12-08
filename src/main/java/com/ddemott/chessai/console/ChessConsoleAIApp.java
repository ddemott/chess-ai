package com.ddemott.chessai.console;

import com.ddemott.chessai.Board;
import com.ddemott.chessai.ai.ChessAI;

public class ChessConsoleAIApp {
	public static void main(String[] args) {
		Board board = new Board();
		ChessAI ai = new ChessAI(4); // Depth 4 for stronger AI
		ChessConsoleAI console = new ChessConsoleAI(board, ai, "Black");
		console.startGame();
	}
}
