package com.ddemott.chessai.engine;

import com.ddemott.chessai.util.Log;

import com.ddemott.chessai.State;
import com.ddemott.chessai.MoveHistory;
import com.ddemott.chessai.Move;
import com.ddemott.chessai.Side;
import com.ddemott.chessai.ai.AIStrategy;
import com.ddemott.chessai.ai.MinMaxStrategy;
import com.ddemott.chessai.ai.AIDifficulty;
import com.ddemott.chessai.pieces.IPiece;
import com.ddemott.chessai.pieces.Pawn;
import com.ddemott.chessai.pieces.Knight;
import com.ddemott.chessai.pieces.Bishop;
import com.ddemott.chessai.pieces.Rook;
import com.ddemott.chessai.pieces.Queen;
import com.ddemott.chessai.pieces.King;

import com.ddemott.chessai.ai.OpeningBook;
import java.util.List;
import java.util.ArrayList;

/**
 * Manages the state and logic of the chess game.
 */
public class GameEngine {
	private State state;
	private AIStrategy aiStrategy;
	private AIDifficulty aiDifficulty;
	private OpeningBook openingBook = new OpeningBook();

	// Original constructor for backward compatibility
	public GameEngine(int aiDepth) {
		this.aiDifficulty = AIDifficulty.fromDepth(aiDepth);
		this.state = new State();
		this.aiStrategy = new MinMaxStrategy(aiDepth);
		state.setAIStrategy(aiStrategy);
	}

	// New constructor with difficulty enum
	public GameEngine(AIDifficulty difficulty) {
		this.aiDifficulty = difficulty;
		this.state = new State();
		this.aiStrategy = new MinMaxStrategy(difficulty.getDepth());
		state.setAIStrategy(aiStrategy);
	}

	// Constructor for AI vs AI with different difficulties
	public GameEngine(AIDifficulty whiteDifficulty, AIDifficulty blackDifficulty) {
		// Start with white difficulty, will be switched during gameplay
		this.aiDifficulty = whiteDifficulty;
		this.state = new State();
		this.aiStrategy = new MinMaxStrategy(whiteDifficulty.getDepth());
		state.setAIStrategy(aiStrategy);
	}

	public String getCurrentTurn() {
		return state.getCurrentTurn();
	}

	public boolean movePiece(String from, String to) {
		return state.movePiece(from, to);
	}

	public boolean movePiece(String from, String to, String promotionPiece) {
		return state.movePiece(from, to, promotionPiece);
	}

	// Returns both the best move and its score for display
	public com.ddemott.chessai.ai.MoveResult getBestMoveWithScore() {
		// 1. Check Opening Book
		String bookMove = getBookMove();
		if (bookMove != null) {
			return new com.ddemott.chessai.ai.MoveResult(0, bookMove);
		}

		// 2. AI Strategy
		AIStrategy strategy = state.getAIStrategy();
		if (strategy instanceof com.ddemott.chessai.ai.MinMaxStrategy) {
			com.ddemott.chessai.ai.MinMaxStrategy minmax = (com.ddemott.chessai.ai.MinMaxStrategy) strategy;
			return minmax.calculateBestMoveWithScore(state, state.getCurrentTurn());
		}
		// Fallback: just return move with dummy score
		String move = getBestMove();
		return new com.ddemott.chessai.ai.MoveResult(0, move);
	}

	private String getBookMove() {
		try {
			List<Move> historyMoves = state.getMoveHistory().getAllMoves();
			StringBuilder sb = new StringBuilder();
			for (Move m : historyMoves) {
				if (sb.length() > 0)
					sb.append(" ");
				sb.append(m.getAlgebraicNotation());
			}
			String historyStr = sb.toString();

			String bookMoveSAN = openingBook.getMove(historyStr);
			if (bookMoveSAN != null) {
				// Find the coordinate move that matches this SAN
				List<String> legalMoves = state.getAllPossibleMoves(getCurrentTurn());
				for (String coordMove : legalMoves) {
					// Simulate to get SAN
					State simState = state.clone();
					String[] parts = coordMove.trim().split("\\s+");
					if (parts.length < 2)
						continue;
					String promotion = parts.length > 2 ? parts[2] : null;

					try {
						if (simState.movePiece(parts[0], parts[1], promotion)) {
							String san = simState.getMoveHistory().getLastMove()
							        .getAlgebraicNotation();
							// Compare cleaned SAN strings
							if (cleanSAN(san).equals(cleanSAN(bookMoveSAN))) {
								Log.info("Playing book move: " + bookMoveSAN + " (" + coordMove
								        + ")");
								return coordMove;
							}
						}
					} catch (Exception e) {
						Log.warn("Error checking book move '" + bookMoveSAN
						        + "' against candidate '" + coordMove + "': " + e.getMessage());
					}
				}
			}
		} catch (Exception e) {
			Log.warn("Error in opening book lookup: " + e.getMessage());
		}
		return null;
	}

	private String cleanSAN(String san) {
		return san.replaceAll("[+#]", "");
	}

	public String getBestMove() {
		String bookMove = getBookMove();
		if (bookMove != null) {
			return bookMove;
		}

		// 2. Fallback to AI
		return state.getBestMove();
	}

	public void makeAIMove() {
		String aiMove = getBestMove();
		if (aiMove != null) {
			String[] aiPositions = aiMove.split(" ");
			if (aiPositions.length == 2 || aiPositions.length == 3) {
				String promotion = aiPositions.length == 3 ? aiPositions[2] : null;
				boolean moveSuccess = movePiece(aiPositions[0], aiPositions[1], promotion);
				if (!moveSuccess) {
					// Log error internally or throw exception. For now, silence.
				}
			}
		}
	}

	public String getBoardRepresentation() {
		return state.getBoard().getBoardRepresentation();
	}

	public State getGameState() {
		return state;
	}

	// Move history related methods
	public MoveHistory getMoveHistory() {
		return state.getMoveHistory();
	}

	public String getMoveListDisplay() {
		return state.getMoveHistory().getMoveListDisplay();
	}

	public Move getLastMove() {
		return state.getMoveHistory().getLastMove();
	}

	public boolean undoLastMove() {
		return state.undoLastMove();
	}

	public boolean redoLastMove() {
		return state.redoLastMove();
	}

	public boolean canUndo() {
		return state.getMoveHistory().canUndo();
	}

	public boolean canRedo() {
		return state.getMoveHistory().canRedo();
	}

	public String exportGameToPGN(String whitePlayer, String blackPlayer, String result) {
		return state.getMoveHistory().exportToPGN(whitePlayer, blackPlayer, result);
	}

	public boolean saveGameToPGNFile(String filename, String whitePlayer, String blackPlayer,
	        String result) {
		return state.getMoveHistory().saveToPGNFile(filename, whitePlayer, blackPlayer, result);
	}

	/**
	 * Load a game from a PGN file
	 *
	 * @param filename
	 *            The PGN file to load
	 * @return true if successfully loaded, false otherwise
	 */
	public boolean loadGameFromPGNFile(String filename) {
		MoveHistory.PGNGameData gameData = MoveHistory.loadFromPGNFile(filename);
		if (gameData == null) {
			return false;
		}

		return loadGameFromPGNData(gameData);
	}

	/**
	 * Load a game from parsed PGN data
	 *
	 * @param gameData
	 *            The parsed PGN data
	 * @return true if successfully loaded, false otherwise
	 */
	public boolean loadGameFromPGNData(MoveHistory.PGNGameData gameData) {
		try {
			// Reset the game state
			this.state = new State();
			state.setAIStrategy(aiStrategy);

			// Replay all the moves
			for (String algebraicMove : gameData.moves) {
				if (!playMoveFromAlgebraicNotation(algebraicMove)) {
					Log.error("Failed to play move: " + algebraicMove);
					return false;
				}
			}

			return true;
		} catch (Exception e) {
			Log.error("Error loading game from PGN: " + e.getMessage(), e);
			return false;
		}
	}

	/**
	 * Plays a move from Standard Algebraic Notation (SAN). Parses the SAN string,
	 * finds the matching legal move, and executes it.
	 */
	private boolean playMoveFromAlgebraicNotation(String algebraicMove) {
		// Strip check/checkmate symbols
		String san = algebraicMove.replaceAll("[+#!?]", "").trim();

		// Handle castling
		if (san.equals("O-O")) {
			Side currentSide = state.getCurrentTurnSide();
			return currentSide == Side.WHITE ? movePiece("e1", "g1") : movePiece("e8", "g8");
		} else if (san.equals("O-O-O")) {
			Side currentSide = state.getCurrentTurnSide();
			return currentSide == Side.WHITE ? movePiece("e1", "c1") : movePiece("e8", "c8");
		}

		// Parse promotion piece
		String promotionPiece = null;
		if (san.contains("=")) {
			int eqIdx = san.indexOf('=');
			promotionPiece = san.substring(eqIdx + 1);
			san = san.substring(0, eqIdx);
		}

		// Determine piece type
		Class<?> pieceType;
		int startIdx = 0;
		char firstChar = san.charAt(0);
		if (firstChar >= 'A' && firstChar <= 'Z') {
			switch (firstChar) {
				case 'N':
					pieceType = Knight.class;
					break;
				case 'B':
					pieceType = Bishop.class;
					break;
				case 'R':
					pieceType = Rook.class;
					break;
				case 'Q':
					pieceType = Queen.class;
					break;
				case 'K':
					pieceType = King.class;
					break;
				default:
					Log.error("Unknown piece type in SAN: " + algebraicMove);
					return false;
			}
			startIdx = 1;
		} else {
			pieceType = Pawn.class;
		}

		// Extract destination square (always the last two characters)
		if (san.length() < 2) {
			Log.error("SAN too short: " + algebraicMove);
			return false;
		}
		String toSquare = san.substring(san.length() - 2);

		// Extract disambiguation hints (between piece letter and destination, excluding
		// 'x')
		String middle = san.substring(startIdx, san.length() - 2).replace("x", "");
		Character disambigFile = null;
		Character disambigRank = null;
		for (char c : middle.toCharArray()) {
			if (c >= 'a' && c <= 'h') {
				disambigFile = c;
			} else if (c >= '1' && c <= '8') {
				disambigRank = c;
			}
		}

		// Find matching legal move
		Side currentSide = state.getCurrentTurnSide();
		List<String> legalMoves = state.getAllPossibleMoves(currentSide);
		List<String> candidates = new ArrayList<>();

		for (String coordMove : legalMoves) {
			String[] parts = coordMove.trim().split("\\s+");
			if (parts.length < 2) {
				continue;
			}
			String from = parts[0];
			String to = parts[1];

			if (!to.equals(toSquare)) {
				continue;
			}

			IPiece piece = state.getBoard().getPieceAt(from);
			if (piece == null || !pieceType.isInstance(piece)) {
				continue;
			}

			// Apply disambiguation filters
			if (disambigFile != null && from.charAt(0) != disambigFile) {
				continue;
			}
			if (disambigRank != null && from.charAt(1) != disambigRank) {
				continue;
			}

			candidates.add(coordMove);
		}

		if (candidates.size() == 1) {
			String[] parts = candidates.get(0).trim().split("\\s+");
			return movePiece(parts[0], parts[1], promotionPiece);
		} else if (candidates.isEmpty()) {
			Log.error("No legal move found for SAN: " + algebraicMove);
			return false;
		} else {
			Log.error(
			        "Ambiguous SAN (found " + candidates.size() + " candidates): " + algebraicMove);
			return false;
		}
	}

	/**
	 * Verifies the integrity of the game state by replaying the move history on a
	 * fresh board and comparing the result with the current board state. Throws
	 * RuntimeException if a discrepancy is found.
	 */
	public void verifyGameStateIntegrity() {
		try {
			// 1. Create a fresh state
			State replayState = new State();

			// 2. Replay all moves from history
			List<Move> allMoves = state.getMoveHistory().getAllMoves();
			for (Move move : allMoves) {
				String from = move.getFrom();
				String to = move.getTo();
				String promotion = move.getPromotionPiece();

				// Replay the move using the same logic as the game loop
				// Note: playMoveFromAlgebraicNotation handles castling notation specifically,
				// but here we have direct coordinates.
				// We need to handle castling logic if 'from' and 'to' are King's moves.
				// Since 'movePiece' handles coordinate-based castling detection, this should
				// work.

				if (!replayState.movePiece(from, to, promotion)) {
					String error = "Integrity Check Failed: Could not replay move "
					        + move.getAlgebraicNotation() + " (" + from + "->" + to + ")";
					Log.error(error);
					throw new RuntimeException(error);
				}
			}

			// 3. Compare the boards
			String comparison = state.getBoard().compareTo(replayState.getBoard());
			if (!"Boards are identical".equals(comparison)) {
				String error = "Integrity Check Failed: Board state mismatch!\n" + comparison;
				Log.error(error);
				throw new RuntimeException(error);
			}

			// 4. Verify turn
			if (!state.getCurrentTurn().equals(replayState.getCurrentTurn())) {
				String error = "Integrity Check Failed: Turn mismatch! Current="
				        + state.getCurrentTurn() + ", Replay=" + replayState.getCurrentTurn();
				Log.error(error);
				throw new RuntimeException(error);
			}

		} catch (Exception e) {
			if (e instanceof RuntimeException) {
				throw (RuntimeException) e;
			}
			Log.error("Error during integrity check: " + e.getMessage(), e);
			throw new RuntimeException("Integrity check failed with exception", e);
		}
	}

	/**
	 * Get current AI difficulty
	 */
	public AIDifficulty getAIDifficulty() {
		return aiDifficulty;
	}

	/**
	 * Change AI difficulty during gameplay
	 */
	public void setAIDifficulty(AIDifficulty difficulty) {
		this.aiDifficulty = difficulty;
		this.aiStrategy = new MinMaxStrategy(difficulty.getDepth());
		state.setAIStrategy(aiStrategy);
	}
}
