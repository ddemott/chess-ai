package com.ddemott.chessai;

import com.ddemott.chessai.pieces.IPiece;

/**
 * Represents a chess move with all necessary information for history tracking and notation.
 */
public class Move {
    private int score = 0;
    private final String from;
    private final String to;
    private final IPiece movingPiece;
    private final IPiece capturedPiece;
    private final String algebraicNotation;
    private final int moveNumber;
    private final String playerColor;
    private final boolean isCheck;
    private final boolean isCheckmate;
    private final boolean isCastle;
    private final boolean isEnPassant;
    private final String promotionPiece;

    public Move(String from, String to, IPiece movingPiece, IPiece capturedPiece, 
                String algebraicNotation, int moveNumber, String playerColor, 
                boolean isCheck, boolean isCheckmate, boolean isCastle, 
                boolean isEnPassant, String promotionPiece) {
        this.from = from;
        this.to = to;
        this.movingPiece = movingPiece;
        this.capturedPiece = capturedPiece;
        this.algebraicNotation = algebraicNotation;
        this.moveNumber = moveNumber;
        this.playerColor = playerColor;
        this.isCheck = isCheck;
        this.isCheckmate = isCheckmate;
        this.isCastle = isCastle;
        this.isEnPassant = isEnPassant;
        this.promotionPiece = promotionPiece;
        this.score = 0;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getScore() {
        return score;
    }

    // Getters
    public String getFrom() { return from; }
    public String getTo() { return to; }
    public IPiece getMovingPiece() { return movingPiece; }
    public IPiece getCapturedPiece() { return capturedPiece; }
    public String getAlgebraicNotation() { return algebraicNotation; }
    public int getMoveNumber() { return moveNumber; }
    public String getPlayerColor() { return playerColor; }
    public boolean isCheck() { return isCheck; }
    public boolean isCheckmate() { return isCheckmate; }
    public boolean isCastle() { return isCastle; }
    public boolean isEnPassant() { return isEnPassant; }
    public String getPromotionPiece() { return promotionPiece; }

    public boolean isCapture() {
        return capturedPiece != null || isEnPassant;
    }

    @Override
    public String toString() {
        return algebraicNotation;
    }

    /**
     * Creates a simple move without special conditions
     */
    public static Move createSimpleMove(String from, String to, IPiece movingPiece, 
                                       IPiece capturedPiece, int moveNumber, String playerColor) {
        return new Move(from, to, movingPiece, capturedPiece, "", moveNumber, playerColor, 
                       false, false, false, false, null);
    }
}
