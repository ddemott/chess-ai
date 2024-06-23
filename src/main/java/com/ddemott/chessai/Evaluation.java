package com.ddemott.chessai;

import com.ddemott.chessai.pieces.IPiece;
import com.ddemott.chessai.pieces.King;
import com.ddemott.chessai.pieces.Pawn;

import java.util.List;

/**
 * This class is responsible for evaluating the board state in a chess game.
 * It includes methods to evaluate the overall position, check and checkmate status, and King safety.
 */
public class Evaluation {

    /**
     * Evaluates the board state and returns a score based on the given color's perspective.
     * 
     * @param board The board to evaluate.
     * @param color The color to evaluate from (e.g., "White" or "Black").
     * @return The evaluation score.
     */
    public int evaluateBoard(Board board, String color) {
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

                    // Add bonus for piece safety
                    if (isPieceGuarded(piece, board)) {
                        totalValue += piece.getColor().equals(color) ? 0.2 * value : -0.2 * value;
                    } else {
                        totalValue -= piece.getColor().equals(color) ? 0.2 * value : -0.2 * value;
                    }
                }
            }
        }

        // Additional evaluation for King safety
        totalValue += evaluateKingSafety(board, color);

        // Check and checkmate evaluation
        if (isInCheck(board, toggleColor(color))) {
            totalValue += 200; // Opponent's King is in check, add a moderate positive score
        }
        if (isCheckmate(board, toggleColor(color))) {
            totalValue += 10000; // Opponent's King is in checkmate, add a very large positive score
        }
        if (isInCheck(board, color)) {
            totalValue -= 200; // Own King is in check, add a moderate negative score
        }
        if (isCheckmate(board, color)) {
            totalValue -= 10000; // Own King is in checkmate, add a very large negative score
        }

        return totalValue;
    }

    /**
     * Checks if the King of the given color is in check.
     * 
     * @param board The board to evaluate.
     * @param color The color of the King to check.
     * @return True if the King is in check, false otherwise.
     */
    public boolean isInCheck(Board board, String color) {
        IPiece king = findKing(board, color);
        if (king != null) {
            String kingPosition = king.getPosition();
            // Check all opponent's moves to see if any can attack the king's position
            List<String> opponentMoves = board.getAllPossibleMoves(toggleColor(color));
            for (String move : opponentMoves) {
                String[] positions = move.split(" ");
                if (positions.length == 2 && positions[1].equals(kingPosition)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Checks if the King of the given color is in checkmate.
     * 
     * @param board The board to evaluate.
     * @param color The color of the King to check.
     * @return True if the King is in checkmate, false otherwise.
     */
    public boolean isCheckmate(Board board, String color) {
        if (isInCheck(board, color)) {
            List<String> possibleMoves = board.getAllPossibleMoves(color);
            for (String move : possibleMoves) {
                Board newState = board.clone();
                String[] positions = move.split(" ");
                newState.movePiece(positions[0], positions[1]);
                if (!isInCheck(newState, color)) {
                    return false; // If there's at least one move that escapes check, it's not checkmate
                }
            }
            return true; // No moves can escape check, so it's checkmate
        }
        return false;
    }

    /**
     * Toggles the color between "White" and "Black".
     * 
     * @param color The current color.
     * @return The toggled color.
     */
    public String toggleColor(String color) {
        return color.equals("White") ? "Black" : "White";
    }

    /**
     * Finds the King piece of the given color on the board.
     * 
     * @param board The board to search.
     * @param color The color of the King to find.
     * @return The King piece if found, null otherwise.
     */
    public IPiece findKing(Board board, String color) {
        IPiece[][] pieces = board.getBoardArray();
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                IPiece piece = pieces[row][col];
                if (piece instanceof King && piece.getColor().equals(color)) {
                    return piece;
                }
            }
        }
        return null;
    }

    /**
     * Checks if a piece is guarded by any friendly pieces.
     * 
     * @param piece The piece to check.
     * @param board The board to evaluate.
     * @return True if the piece is guarded, false otherwise.
     */
    public boolean isPieceGuarded(IPiece piece, Board board) {
        List<String> possibleGuardians = piece.getAllPossibleMoves(board);

        for (String move : possibleGuardians) {
            String[] positions = move.split(" ");
            if (positions.length == 2) {
                IPiece guardian = board.getPieceAt(positions[1]);
                if (guardian != null && guardian.getColor().equals(piece.getColor())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Evaluates the safety of the King, including factors like castling status and pawn shield.
     * 
     * @param board The board to evaluate.
     * @param color The color of the King to evaluate.
     * @return The King safety score.
     */
    public int evaluateKingSafety(Board board, String color) {
        int kingSafetyScore = 0;
        IPiece king = findKing(board, color);
        if (king != null) {
            String kingPosition = king.getPosition();
            int[] coords = board.convertPositionToCoordinates(kingPosition);

            // Example: Add penalties for exposed King
            // Penalize if King is not castled and exposed in the center
            if (coords[1] == 4 || coords[1] == 3) {
                kingSafetyScore -= 50; // Arbitrary penalty value
            }

            // Add bonuses if King is castled and well-protected
            if (isCastled(kingPosition, board)) {
                kingSafetyScore += 30; // Arbitrary bonus value
            }

            // Additional penalties or bonuses for pawn shield, distance from edges, etc.
            kingSafetyScore += evaluatePawnShield(king, board);
        }
        return kingSafetyScore;
    }

    /**
     * Checks if the King is castled based on its position.
     * 
     * @param kingPosition The position of the King.
     * @param board The board to evaluate.
     * @return True if the King is castled, false otherwise.
     */
    public boolean isCastled(String kingPosition, Board board) {
        return kingPosition.equals("g1") || kingPosition.equals("c1") || kingPosition.equals("g8") || kingPosition.equals("c8");
    }

    /**
     * Evaluates the presence of a pawn shield in front of the King.
     * 
     * @param king The King piece to evaluate.
     * @param board The board to evaluate.
     * @return The pawn shield score.
     */
    public int evaluatePawnShield(IPiece king, Board board) {
        int pawnShieldScore = 0;
        String kingPosition = king.getPosition();
        int[] coords = board.convertPositionToCoordinates(kingPosition);
        String color = king.getColor();

        // Check the three squares in front of the castled King for pawns
        if (coords[1] == 6 || coords[1] == 2) {
            int row = color.equals("White") ? 1 : 6;
            int colStart = coords[1] - 1;
            int colEnd = coords[1] + 1;

            for (int col = colStart; col <= colEnd; col++) {
                IPiece piece = board.getPieceAt(board.convertCoordinatesToPosition(row, col));
                if (piece instanceof Pawn && piece.getColor().equals(color)) {
                    pawnShieldScore += 10; // Arbitrary bonus value
                }
            }
        }
        return pawnShieldScore;
    }
}
