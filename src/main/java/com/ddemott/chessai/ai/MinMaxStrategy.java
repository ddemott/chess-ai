package com.ddemott.chessai.ai;

import com.ddemott.chessai.Board;
import com.ddemott.chessai.State;
import com.ddemott.chessai.pieces.IPiece;

import java.util.List;

public class MinMaxStrategy implements AIStrategy {

    private int maxDepth;

    public MinMaxStrategy(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    @Override
    public String calculateBestMove(State state, String color) {
        MoveResult result = minMax(state, maxDepth, Integer.MIN_VALUE, Integer.MAX_VALUE, color, true);
        if (result == null || result.getMove() == null) {
            System.out.println("No valid move found for AI.");
            return null;
        }
        System.out.println("AI selected move for " + color + ": " + result.getMove());
        return result.getMove();
    }

    private MoveResult minMax(State state, int depth, int alpha, int beta, String color, boolean maximizingPlayer) {
        if (depth == 0) {
            return new MoveResult(evaluateBoard(state.getBoard(), color), null);
        }

        List<String> possibleMoves = state.getAllPossibleMoves(color);
        if (possibleMoves.isEmpty()) {
            return new MoveResult(evaluateBoard(state.getBoard(), color), null);
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

    private int evaluateBoard(Board board, String color) {
        int totalValue = 0;
        IPiece[][] pieces = board.getBoardArray();

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                IPiece piece = pieces[row][col];
                if (piece != null) {
                    int value = piece.getValue();
                    if (!piece.getColor().equals(color)) {
                        value = -value;
                    }
                    totalValue += value;
                }
            }
        }
        return totalValue;
    }

    private String toggleColor(String color) {
        return color.equals("White") ? "Black" : "White";
    }
}
