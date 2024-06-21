package com.ddemott.chessai.ai;

import com.ddemott.chessai.Board;
import com.ddemott.chessai.State;
import com.ddemott.chessai.Evaluation;

import java.util.List;

/**
 * Implements the Minimax algorithm with alpha-beta pruning for the chess AI.
 */
public class MinMaxStrategy implements AIStrategy {

    private int maxDepth;
    private Evaluation evaluation;

    public MinMaxStrategy(int maxDepth) {
        this.maxDepth = maxDepth;
        this.evaluation = new Evaluation(); // Initialize the evaluation object
    }

    @Override
    public String calculateBestMove(State state, String color) {
        MoveResult result = minMax(state, maxDepth, Integer.MIN_VALUE, Integer.MAX_VALUE, color, true);
        return result != null ? result.getMove() : null;
    }

    private MoveResult minMax(State state, int depth, int alpha, int beta, String color, boolean maximizingPlayer) {
        if (depth == 0) {
            int evaluationScore = evaluation.evaluateBoard(state.getBoard(), color);
            return new MoveResult(evaluationScore, null);
        }

        List<String> possibleMoves = state.getAllPossibleMoves(color);
        if (possibleMoves.isEmpty()) {
            int evaluationScore = evaluation.evaluateBoard(state.getBoard(), color);
            return new MoveResult(evaluationScore, null);
        }

        MoveResult bestMove = new MoveResult(maximizingPlayer ? Integer.MIN_VALUE : Integer.MAX_VALUE, null);

        for (String move : possibleMoves) {
            State newState = state.clone();
            String[] positions = move.split(" ");
            if (positions.length != 2) {
                continue;
            }
            newState.movePiece(positions[0], positions[1]);

            MoveResult result = minMax(newState, depth - 1, alpha, beta, toggleColor(color), !maximizingPlayer);

            if (maximizingPlayer) {
                if (result.getValue() > bestMove.getValue()) {
                    bestMove = new MoveResult(result.getValue(), move);
                }
                alpha = Math.max(alpha, result.getValue());
            } else {
                if (result.getValue() < bestMove.getValue()) {
                    bestMove = new MoveResult(result.getValue(), move);
                }
                beta = Math.min(beta, result.getValue());
            }

            if (beta <= alpha) {
                break; // Alpha-beta pruning
            }
        }

        return bestMove;
    }

    private String toggleColor(String color) {
        return color.equals("White") ? "Black" : "White";
    }
}
