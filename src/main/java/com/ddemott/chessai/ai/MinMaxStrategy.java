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
		MoveResult result = minMax(state, maxDepth, Integer.MIN_VALUE, Integer.MAX_VALUE, side,
		        true);
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
		MoveResult result = minMax(state, maxDepth, Integer.MIN_VALUE, Integer.MAX_VALUE, side,
		        true);
		return result != null ? result.move() : null;
	}

	private MoveResult minMax(State state, int depth, int alpha, int beta, Side side,
	        boolean maximizingPlayer) {
		// Penalize threefold repetition and fifty-move rule as a draw
		if (state.isThreefoldRepetition() || state.isFiftyMoveRule()) {
			return new MoveResult(GameConstants.DRAW_SCORE, null);
		}
		if (depth == 0) {
			int evaluationScore = evaluation.evaluateBoard(state.getBoard(), side);
			return new MoveResult(evaluationScore, null);
		}

		List<String> possibleMoves = state.getAllPossibleMoves(side);
		if (possibleMoves.isEmpty()) {
			if (state.getBoard().isKingInCheck(side)) {
				// Checkmate
				return new MoveResult(maximizingPlayer
				        ? -GameConstants.CHECKMATE_SCORE
				        : GameConstants.CHECKMATE_SCORE, null);
			} else {
				// Stalemate
				return new MoveResult(GameConstants.DRAW_SCORE, null);
			}
		}

		// Move Ordering: Evaluate captures first for better Alpha-Beta pruning
		possibleMoves.sort((m1, m2) -> {
			boolean c1 = isCapture(state, m1);
			boolean c2 = isCapture(state, m2);
			return Boolean.compare(c2, c1); // true (capture) comes before false
		});

		MoveResult bestMove = new MoveResult(
		        maximizingPlayer ? Integer.MIN_VALUE : Integer.MAX_VALUE, null);

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

			if (!newState.movePiece(positions[0], positions[1], promotionPiece)) {
				continue;
			}

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

			MoveResult result = minMax(newState, depth - 1, alpha, beta, side.flip(),
			        !maximizingPlayer);

			if (maximizingPlayer) {
				if (result.value() > bestMove.value() || bestMove.move() == null) {
					bestMove = new MoveResult(result.value(), move);
				}
				alpha = Math.max(alpha, result.value());
			} else {
				if (result.value() < bestMove.value() || bestMove.move() == null) {
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

	private boolean isCapture(State state, String move) {
		String[] parts = move.split(" ");
		if (parts.length >= 2) {
			// Standard capture
			if (state.getBoard().getPieceAt(parts[1]) != null) {
				return true;
			}
			// En Passant capture check
			// (If destination is empty but it's a diagonal pawn move, it's likely en
			// passant,
			// though verifying 'enPassantTarget' is more robust if accessible.
			// For simple ordering, destination check catches most major captures.)
		}
		return false;
	}
}
