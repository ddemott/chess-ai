package com.ddemott.chessai.console;

import com.ddemott.chessai.engine.GameEngine;
import com.ddemott.chessai.Move;
import com.ddemott.chessai.ai.AIDifficulty;

/**
 * AI player controller for automated move selection
 */
public class AIPlayerController implements PlayerController {
	private final AIDifficulty difficulty;

	public AIPlayerController(AIDifficulty difficulty) {
		this.difficulty = difficulty;
	}

	@Override
	public Move selectMove(GameEngine gameEngine) {
		gameEngine.setAIDifficulty(difficulty);
		com.ddemott.chessai.ai.MoveResult result = gameEngine.getBestMoveWithScore();
		String aiMove = result.move();
		int score = result.value();
		if (aiMove != null) {
			String[] aiPositions = aiMove.split(" ");
			if (aiPositions.length == 2) {
				var board = gameEngine.getGameState().getBoard();
				var movingPiece = board.getPieceAt(aiPositions[0]);
				var capturedPiece = board.getPieceAt(aiPositions[1]);
				int moveNumber = gameEngine.getGameState().getMoveHistory().getMoves().size() / 2 + 1;
				String playerColor = gameEngine.getCurrentTurn();
				Move move = new Move(aiPositions[0], aiPositions[1], movingPiece, capturedPiece, "", moveNumber,
						playerColor, false, false, false, false, null);
				move.setScore(score);
				return move;
			}
		}
		return null;
	}
}
