package com.ddemott.chessai.ai;

import com.ddemott.chessai.State;
import com.ddemott.chessai.Evaluation;
import com.ddemott.chessai.GameConstants;
import com.ddemott.chessai.Side;

import java.util.List;

/**
 * Implements the Minimax algorithm with alpha-beta pruning for the chess AI.
 */
public class MinMaxStrategy implements AIStrategy {
	// Expose both move and score for display
	public MoveResult calculateBestMoveWithScore(State state, String color) {
		Side side = color.equalsIgnoreCase("White") ? Side.WHITE : Side.BLACK;
		MoveResult result = minMax(state, maxDepth, Integer.MIN_VALUE, Integer.MAX_VALUE, side, true);
		return result;
	}

	private int maxDepth;
	private Evaluation evaluation;

	public MinMaxStrategy(int maxDepth) {
		this.maxDepth = maxDepth;
		this.evaluation = new Evaluation(); // Initialize the evaluation object
	}

	@Override
	public String calculateBestMove(State state, String color) {
		Side side = color.equalsIgnoreCase("White") ? Side.WHITE : Side.BLACK;
		MoveResult result = minMax(state, maxDepth, Integer.MIN_VALUE, Integer.MAX_VALUE, side, true);
		return result != null ? result.move() : null;
	}

	private MoveResult minMax(State state, int depth, int alpha, int beta, Side side, boolean maximizingPlayer) {
		// Penalize threefold repetition as a draw
		if (state.isThreefoldRepetition()) {
			// Major negative score for repetition (draw)
			int repetitionPenalty = maximizingPlayer ? -GameConstants.CHECKMATE_SCORE : GameConstants.CHECKMATE_SCORE;
			return new MoveResult(repetitionPenalty, null);
		}
		if (depth == 0) {
			int evaluationScore = evaluation.evaluateBoard(state.getBoard(), side);
			return new MoveResult(evaluationScore, null);
		}

		List<String> possibleMoves = state.getAllPossibleMoves(side);
		if (possibleMoves.isEmpty()) {
			int evaluationScore = evaluation.evaluateBoard(state.getBoard(), side);
			return new MoveResult(evaluationScore, null);
		}

		MoveResult bestMove = new MoveResult(maximizingPlayer ? Integer.MIN_VALUE : Integer.MAX_VALUE, null);

		for (String move : possibleMoves) {
			State newState = state.clone();
			String[] positions = move.split(" ");

			// Handle promotion moves: "e7 e8 Q"
			String promotionPiece = null;
			if (positions.length == 3) {
				promotionPiece = positions[2];
			} else if (positions.length != 2) {
				continue;
			}

			newState.movePiece(positions[0], positions[1], promotionPiece);

			// Ensure the move does not leave the King in check
			// Note: State.movePiece checks for validity but maybe not full check validation
			// for *resulting* state
			// if we are using the simple movePiece.
			// But wait, standard State.movePiece already checks if move is valid and
			// updates turn.
			// The issue is that the AI might generate pseudo-legal moves.
			// Let's rely on Board.isKingInCheck(side) which is robust now.
			if (newState.getBoard().isKingInCheck(side)) {
				continue;
			}

			MoveResult result = minMax(newState, depth - 1, alpha, beta, side.flip(), !maximizingPlayer);

			if (maximizingPlayer) {
				if (result.value() > bestMove.value()) {
					bestMove = new MoveResult(result.value(), move);
				}
				alpha = Math.max(alpha, result.value());
			} else {
				if (result.value() < bestMove.value()) {
					bestMove = new MoveResult(result.value(), move);
				}
				beta = Math.min(beta, result.value());
			}

			if (beta <= alpha) {
				break; // Alpha-beta pruning
			}
		}

		return bestMove;
	}
}