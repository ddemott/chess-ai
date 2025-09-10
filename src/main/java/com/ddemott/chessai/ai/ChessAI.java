package com.ddemott.chessai.ai;

import com.ddemott.chessai.Board;
import com.ddemott.chessai.pieces.IPiece;
import java.util.List;
import java.util.Random;

public class ChessAI {
    private int maxDepth;
    // private Random random = new Random();

    public ChessAI(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    public String[] getBestMove(Board board, String aiColor) {
        double bestValue = Double.NEGATIVE_INFINITY;
        String[] bestMove = null;
        List<String> moves = board.getAllPossibleMoves(aiColor);
        for (String move : moves) {
            Board cloned = board.clone();
            String[] parts = move.split(" ");
            cloned.movePiece(parts[0], parts[1]);
            double value = minimax(cloned, maxDepth - 1, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, false, aiColor);
            if (value > bestValue || bestMove == null) {
                bestValue = value;
                bestMove = parts;
            }
        }
        // If multiple moves have the same value, pick randomly
        return bestMove;
    }

    private double minimax(Board board, int depth, double alpha, double beta, boolean maximizingPlayer, String aiColor) {
        String opponentColor = aiColor.equals("White") ? "Black" : "White";
        if (depth == 0 || board.isCheckmate(aiColor) || board.isCheckmate(opponentColor) || board.isStalemate(aiColor) || board.isStalemate(opponentColor)) {
            return evaluateBoard(board, aiColor);
        }
        List<String> moves = board.getAllPossibleMoves(maximizingPlayer ? aiColor : opponentColor);
        if (maximizingPlayer) {
            double maxEval = Double.NEGATIVE_INFINITY;
            for (String move : moves) {
                Board cloned = board.clone();
                String[] parts = move.split(" ");
                cloned.movePiece(parts[0], parts[1]);
                double eval = minimax(cloned, depth - 1, alpha, beta, false, aiColor);
                maxEval = Math.max(maxEval, eval);
                alpha = Math.max(alpha, eval);
                if (beta <= alpha) break;
            }
            return maxEval;
        } else {
            double minEval = Double.POSITIVE_INFINITY;
            for (String move : moves) {
                Board cloned = board.clone();
                String[] parts = move.split(" ");
                cloned.movePiece(parts[0], parts[1]);
                double eval = minimax(cloned, depth - 1, alpha, beta, true, aiColor);
                minEval = Math.min(minEval, eval);
                beta = Math.min(beta, eval);
                if (beta <= alpha) break;
            }
            return minEval;
        }
    }

    private double evaluateBoard(Board board, String aiColor) {
        double score = 0.0;
        IPiece[][] arr = board.getBoardArray();
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                IPiece piece = arr[row][col];
                if (piece != null) {
                    double value = getPieceValue(piece);
                    score += piece.getColor().equals(aiColor) ? value : -value;
                }
            }
        }

    // ...existing code...

        // Add bonus for material advantage (captured pieces)
        String opponentColor = aiColor.equals("White") ? "Black" : "White";
        List<IPiece> capturedOpponentPieces = board.getCapturedPieces(opponentColor);
        for (IPiece captured : capturedOpponentPieces) {
            score += getPieceValue(captured) * 0.5; // Bonus for each captured piece
        }

        return score;
    }

    private double getPieceValue(IPiece piece) {
    int value = piece.getValue();
    if (value >= 100) return 100.0;
    return (double) value;
    }
}
