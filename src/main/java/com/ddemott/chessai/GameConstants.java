package com.ddemott.chessai;

public final class GameConstants {
	private GameConstants() {
	} // Prevent instantiation

	// Board Dimensions
	public static final int BOARD_SIZE = 8;

	// Piece Values (Centipawns)
	public static final int PAWN_VALUE = 100;
	public static final int KNIGHT_VALUE = 320;
	public static final int BISHOP_VALUE = 330;
	public static final int ROOK_VALUE = 500;
	public static final int QUEEN_VALUE = 900;
	public static final int KING_VALUE = 20000;

	// Evaluation Scores
	public static final int CHECKMATE_SCORE = 100000;
	public static final int DRAW_SCORE = 0;
	public static final int CHECK_PENALTY = 50;

	// Files/Ranks
	public static final int RANK_1 = 0;
	public static final int RANK_2 = 1;
	public static final int RANK_7 = 6;
	public static final int RANK_8 = 7;

	// Evaluation Weights
	public static final double PIECE_SAFETY_MULTIPLIER = 0.2;
	public static final int CHECK_BONUS = 200; // Bonus/Penalty for check
	public static final int KING_EXPOSED_PENALTY = 50;
	public static final int KING_CASTLED_BONUS = 30;
	public static final int PAWN_SHIELD_BONUS = 10;

	// Positional Weights
	public static final int ISOLATED_PAWN_PENALTY = 20;
	public static final int DOUBLED_PAWN_PENALTY = 20;
	public static final int PASSED_PAWN_BONUS = 30;
	public static final int MOBILITY_BONUS = 5; // Per valid move
	public static final int CENTER_CONTROL_BONUS = 15; // Controlling e4, d4, e5, d5
}
