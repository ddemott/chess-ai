package com.ddemott.chessai;

import com.ddemott.chessai.pieces.IPiece;
import com.ddemott.chessai.pieces.King;
import com.ddemott.chessai.pieces.Pawn;

import java.util.List;

/**
 * This class is responsible for evaluating the board state in a chess game. It
 * includes methods to evaluate the overall position, check and checkmate
 * status, and King safety.
 */
public class Evaluation {

	/**
	 * Evaluates the board state and returns a score based on the given color's
	 * perspective.
	 * 
	 * @param board
	 *            The board to evaluate.
	 * @param color
	 *            The color to evaluate from (e.g., "White" or "Black").
	 * @return The evaluation score.
	 */
	public int evaluateBoard(Board board, String color) {
		return evaluateBoard(board, color.equalsIgnoreCase("White") ? Side.WHITE : Side.BLACK);
	}

	public int evaluateBoard(Board board, Side side) {
		int totalValue = 0;
		IPiece[][] pieces = board.getBoardArray();

		for (int row = 0; row < GameConstants.BOARD_SIZE; row++) {
			for (int col = 0; col < GameConstants.BOARD_SIZE; col++) {
				IPiece piece = pieces[row][col];
				if (piece != null) {
					int value = piece.getValue();
					if (piece.getSide() != side) {
						value = -value;
					}
					totalValue += value;

					// Add bonus for piece safety (This is expensive to check perfectly, maybe
					// simplify?)
					// isPieceGuarded generates moves, which is slow. For now, we'll leave it
					// but we should optimize it later.
					if (isPieceGuarded(piece, board)) {
						totalValue += piece.getSide() == side
								? GameConstants.PIECE_SAFETY_MULTIPLIER * value
								: -GameConstants.PIECE_SAFETY_MULTIPLIER * value;
					} else {
						totalValue -= piece.getSide() == side
								? GameConstants.PIECE_SAFETY_MULTIPLIER * value
								: -GameConstants.PIECE_SAFETY_MULTIPLIER * value;
					}
				}
			}
		}

		// Additional evaluation for King safety
		totalValue += evaluateKingSafety(board, side);

		// Strategic Evaluation (Structure & Activity)
		totalValue += evaluatePawnStructure(board, side);
		totalValue += evaluateMobility(board, side);
		totalValue += evaluateCenterControl(board, side);

		Side opponentSide = side.flip();

		// Check and checkmate evaluation
		// Use optimized Board methods instead of local slow implementations
		if (board.isKingInCheck(opponentSide)) {
			totalValue += GameConstants.CHECK_BONUS; // Opponent's King is in check, add a moderate positive score
		}
		if (board.isCheckmate(opponentSide)) {
			totalValue += GameConstants.CHECKMATE_SCORE; // Opponent's King is in checkmate
		}
		if (board.isKingInCheck(side)) {
			totalValue -= GameConstants.CHECK_BONUS; // Own King is in check, add a moderate negative score
		}
		if (board.isCheckmate(side)) {
			totalValue -= GameConstants.CHECKMATE_SCORE; // Own King is in checkmate
		}

		return totalValue;
	}

	/**
	 * Checks if the King of the given color is in check. Deprecated: Use
	 * Board.isKingInCheck instead
	 */
	public boolean isInCheck(Board board, String color) {
		return board.isKingInCheck(color);
	}

	/**
	 * Checks if the King of the given color is in checkmate. Deprecated: Use
	 * Board.isCheckmate instead
	 */
	public boolean isCheckmate(Board board, String color) {
		return board.isCheckmate(color);
	}

	/**
	 * Finds the King piece of the given color on the board.
	 * 
	 * @param board
	 *            The board to search.
	 * @param color
	 *            The color of the King to find.
	 * @return The King piece if found, null otherwise.
	 */
	public IPiece findKing(Board board, String color) {
		return findKing(board, color.equalsIgnoreCase("White") ? Side.WHITE : Side.BLACK);
	}

	public IPiece findKing(Board board, Side side) {
		IPiece[][] pieces = board.getBoardArray();
		for (int row = 0; row < GameConstants.BOARD_SIZE; row++) {
			for (int col = 0; col < GameConstants.BOARD_SIZE; col++) {
				IPiece piece = pieces[row][col];
				if (piece instanceof King && piece.getSide() == side) {
					return piece;
				}
			}
		}
		return null;
	}

	/**
	 * Checks if a piece is guarded by any friendly pieces.
	 * 
	 * @param piece
	 *            The piece to check.
	 * @param board
	 *            The board to evaluate.
	 * @return True if the piece is guarded, false otherwise.
	 */
	public boolean isPieceGuarded(IPiece piece, Board board) {
		String pos = piece.getPosition();
		// Remove piece temporarily so friendly pieces can "move" there (support it)
		IPiece temp = board.getPieceAt(pos);
		board.setPieceAt(pos, null);

		// Check if ANY piece of the SAME side attacks this empty square
		// isSquareUnderAttack checks if 'defendingSide' is under attack by opponent.
		// So we pass opponent as 'defendingSide' to check if WE (the attacking side
		// relative to that check) attack it.
		boolean isGuarded = board.isSquareUnderAttack(pos, piece.getSide().flip());

		board.setPieceAt(pos, temp);
		return isGuarded;
	}

	/**
	 * Evaluates the safety of the King, including factors like castling status and
	 * pawn shield.
	 * 
	 * @param board
	 *            The board to evaluate.
	 * @param side
	 *            The color of the King to evaluate.
	 * @return The King safety score.
	 */
	public int evaluateKingSafety(Board board, Side side) {
		int kingSafetyScore = 0;
		IPiece king = findKing(board, side);
		if (king != null) {
			String kingPosition = king.getPosition();
			int[] coords = board.convertPositionToCoordinates(kingPosition);

			// Example: Add penalties for exposed King
			// Penalize if King is not castled and exposed in the center
			if (coords[1] == 4 || coords[1] == 3) {
				kingSafetyScore -= GameConstants.KING_EXPOSED_PENALTY;
			}

			// Add bonuses if King is castled and well-protected
			if (isCastled(kingPosition, board)) {
				kingSafetyScore += GameConstants.KING_CASTLED_BONUS;
			}

			// Additional penalties or bonuses for pawn shield, distance from edges, etc.
			kingSafetyScore += evaluatePawnShield(king, board);
		}
		return kingSafetyScore;
	}

	/**
	 * Checks if the King is castled based on its position.
	 * 
	 * @param kingPosition
	 *            The position of the King.
	 * @param board
	 *            The board to evaluate.
	 * @return True if the King is castled, false otherwise.
	 */
	public boolean isCastled(String kingPosition, Board board) {
		return kingPosition.equals("g1") || kingPosition.equals("c1") || kingPosition.equals("g8")
				|| kingPosition.equals("c8");
	}

	/**
	 * Evaluates the presence of a pawn shield in front of the King.
	 * 
	 * @param king
	 *            The King piece to evaluate.
	 * @param board
	 *            The board to evaluate.
	 * @return The pawn shield score.
	 */
	public int evaluatePawnShield(IPiece king, Board board) {
		int pawnShieldScore = 0;
		String kingPosition = king.getPosition();
		int[] coords = board.convertPositionToCoordinates(kingPosition);
		Side side = king.getSide();

		// Check the three squares in front of the castled King for pawns
		if (coords[1] == 6 || coords[1] == 2) {
			int row = side == Side.WHITE ? 1 : 6;
			int colStart = coords[1] - 1;
			int colEnd = coords[1] + 1;

			for (int col = colStart; col <= colEnd; col++) {
				IPiece piece = board.getPieceAt(board.convertCoordinatesToPosition(row, col));
				if (piece instanceof Pawn && piece.getSide() == side) {
					pawnShieldScore += GameConstants.PAWN_SHIELD_BONUS;
				}
			}
		}
		return pawnShieldScore;
	}

	/**
	 * Evaluates pawn structure (isolated, doubled pawns).
	 */
	public int evaluatePawnStructure(Board board, Side side) {
		int score = 0;
		IPiece[][] pieces = board.getBoardArray();
		int[] pawnsPerFile = new int[GameConstants.BOARD_SIZE];

		// Count pawns per file
		for (int col = 0; col < GameConstants.BOARD_SIZE; col++) {
			for (int row = 0; row < GameConstants.BOARD_SIZE; row++) {
				IPiece piece = pieces[row][col];
				if (piece instanceof Pawn && piece.getSide() == side) {
					pawnsPerFile[col]++;
				}
			}
		}

		for (int col = 0; col < GameConstants.BOARD_SIZE; col++) {
			int count = pawnsPerFile[col];
			if (count > 1) {
				score -= (count - 1) * GameConstants.DOUBLED_PAWN_PENALTY;
			}
			if (count > 0) {
				boolean isolated = true;
				if (col > 0 && pawnsPerFile[col - 1] > 0)
					isolated = false;
				if (col < GameConstants.BOARD_SIZE - 1 && pawnsPerFile[col + 1] > 0)
					isolated = false;
				if (isolated) {
					score -= GameConstants.ISOLATED_PAWN_PENALTY;
				}
			}
		}
		return score;
	}

	/**
	 * Evaluates piece mobility (number of valid moves). Note: This can be
	 * computationally expensive.
	 */
	public int evaluateMobility(Board board, Side side) {
		List<String> moves = board.getAllPossibleMoves(side);
		return moves.size() * GameConstants.MOBILITY_BONUS;
	}

	/**
	 * Evaluates control of the center squares (e4, d4, e5, d5).
	 */
	public int evaluateCenterControl(Board board, Side side) {
		int score = 0;
		String[] centerSquares = {"e4", "d4", "e5", "d5"};

		for (String pos : centerSquares) {
			// Check if we occupy it
			IPiece piece = board.getPieceAt(pos);
			if (piece != null && piece.getSide() == side) {
				score += GameConstants.CENTER_CONTROL_BONUS;
			}
			// Check if we guard it (attack it)
			if (board.isSquareUnderAttack(pos, side.flip())) {
				score += GameConstants.CENTER_CONTROL_BONUS / 2;
			}
		}
		return score;
	}
}