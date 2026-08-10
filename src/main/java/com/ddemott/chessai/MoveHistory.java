package com.ddemott.chessai;

import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import com.ddemott.chessai.util.Log;

import com.ddemott.chessai.pieces.IPiece;
import com.ddemott.chessai.pieces.Pawn;
import com.ddemott.chessai.pieces.King;

/**
 * Manages the history of moves in a chess game, including algebraic notation
 * generation and PGN export functionality.
 */
public class MoveHistory {

	// use Log wrapper for consistent logging

	/**
	 * Returns a copy of the moves list (for compatibility with legacy code)
	 */
	public List<Move> getMoves() {
		return new ArrayList<>(moves);
	}
	private final ArrayList<Move> moves;
	private int currentMoveIndex;

	public MoveHistory() {
		this.moves = new ArrayList<>();
		this.currentMoveIndex = -1;
	}

	/**
	 * Adds a move to the history and generates its algebraic notation
	 */
	public void addMove(String from, String to, IPiece movingPiece, IPiece capturedPiece,
	        Board board, String playerColor) {
		addMove(from, to, movingPiece, capturedPiece, board, playerColor, null, false, false, false,
		        false, null, false);
	}

	/**
	 * Adds a move to the history with promotion support
	 */
	public void addMove(String from, String to, IPiece movingPiece, IPiece capturedPiece,
	        Board board, String playerColor, String promotionPiece) {
		addMove(from, to, movingPiece, capturedPiece, board, playerColor, promotionPiece, false,
		        false, false, false, null, false);
	}

	/**
	 * Adds a move to the history with full state for check/checkmate, special move
	 * flags, and undo support
	 */
	public void addMove(String from, String to, IPiece movingPiece, IPiece capturedPiece,
	        Board board, String playerColor, String promotionPiece, boolean isCheck,
	        boolean isCheckmate, boolean isCastle, boolean isEnPassant,
	        String enPassantTargetBefore, boolean wasFirstMove) {
		// Remove any moves after current position (for undo/redo support)
		while (moves.size() > currentMoveIndex + 1) {
			moves.remove(moves.size() - 1);
		}

		int moveNumber = (moves.size() / 2) + 1;
		String algebraicNotation = generateAlgebraicNotation(from, to, movingPiece, capturedPiece,
		        board, promotionPiece, isCheck, isCheckmate, isCastle);

		Side side = playerColor.equalsIgnoreCase("White") ? Side.WHITE : Side.BLACK;
		Move move = new Move(from, to, movingPiece, capturedPiece, algebraicNotation, moveNumber,
		        side, isCheck, isCheckmate, isCastle, isEnPassant, promotionPiece,
		        enPassantTargetBefore, wasFirstMove);

		moves.add(move);
		currentMoveIndex++;
	}

	/**
	 * Generates Standard Algebraic Notation (SAN) for a move with promotion support
	 */
	private String generateAlgebraicNotation(String from, String to, IPiece movingPiece,
	        IPiece capturedPiece, Board board, String promotionPiece, boolean isCheck,
	        boolean isCheckmate, boolean isCastle) {
		StringBuilder notation = new StringBuilder();

		// Handle castling
		if (isCastle
		        || (movingPiece instanceof King && Math.abs(from.charAt(0) - to.charAt(0)) == 2)) {
			if (to.charAt(0) > from.charAt(0)) {
				notation.append("O-O");
			} else {
				notation.append("O-O-O");
			}
			if (isCheckmate) {
				notation.append("#");
			} else if (isCheck) {
				notation.append("+");
			}
			return notation.toString();
		}

		// Add piece symbol (nothing for pawns)
		if (!(movingPiece instanceof Pawn)) {
			notation.append(movingPiece.getSymbol());
		}

		// Add disambiguation if needed
		String disambiguation = getDisambiguation(from, to, movingPiece, board);
		notation.append(disambiguation);

		// Add capture symbol (including en passant captures where capturedPiece may be
		// null in the 'to' square)
		boolean isCapture = capturedPiece != null;
		// For en passant, the captured piece position differs from 'to', so
		// capturedPiece
		// from the caller may be null. We detect en passant pawn captures by checking
		// if
		// a pawn moved diagonally to an empty square.
		if (!isCapture && movingPiece instanceof Pawn && from.charAt(0) != to.charAt(0)) {
			isCapture = true; // en passant capture
		}
		if (isCapture) {
			if (movingPiece instanceof Pawn) {
				notation.append(from.charAt(0)); // Add file for pawn captures
			}
			notation.append("x");
		}

		// Add destination square
		notation.append(to);

		// Add promotion
		if (promotionPiece != null) {
			notation.append("=").append(promotionPiece);
		}

		// Add check/checkmate indicators
		if (isCheckmate) {
			notation.append("#");
		} else if (isCheck) {
			notation.append("+");
		}

		return notation.toString();
	}

	/**
	 * Determines if disambiguation is needed for algebraic notation. Checks all
	 * pieces of the same type and side that can also move to the same square.
	 */
	private String getDisambiguation(String from, String to, IPiece movingPiece, Board board) {
		if (movingPiece instanceof Pawn || movingPiece instanceof King) {
			return ""; // Pawns use file on captures (handled elsewhere), kings are unique
		}

		char pieceSymbol = movingPiece.getSymbol();
		Side side = movingPiece.getSide();
		java.util.List<String> ambiguousFromSquares = new java.util.ArrayList<>();

		for (int row = 0; row < GameConstants.BOARD_SIZE; row++) {
			for (int col = 0; col < GameConstants.BOARD_SIZE; col++) {
				IPiece other = board.getBoardArray()[row][col];
				if (other == null || other.getSide() != side || other.getSymbol() != pieceSymbol) {
					continue;
				}
				String otherPos = board.convertCoordinatesToPosition(row, col);
				if (otherPos.equals(from)) {
					continue; // Skip self
				}
				// Check if this other piece can also move to 'to'
				String oldPos = other.getPosition();
				other.setPosition(otherPos);
				boolean canMove = other.isValidMove(to, board);
				other.setPosition(oldPos);
				if (canMove && !board.wouldExposeKingToCheck(otherPos, to)) {
					ambiguousFromSquares.add(otherPos);
				}
			}
		}

		if (ambiguousFromSquares.isEmpty()) {
			return "";
		}

		boolean sameFile = false;
		boolean sameRank = false;
		for (String otherFrom : ambiguousFromSquares) {
			if (otherFrom.charAt(0) == from.charAt(0)) {
				sameFile = true;
			}
			if (otherFrom.charAt(1) == from.charAt(1)) {
				sameRank = true;
			}
		}

		if (!sameFile) {
			return String.valueOf(from.charAt(0)); // file disambiguates
		} else if (!sameRank) {
			return String.valueOf(from.charAt(1)); // rank disambiguates
		} else {
			return from; // both file and rank needed
		}
	}

	/**
	 * Gets the current move list as a formatted string
	 */
	public String getMoveListDisplay() {
		StringBuilder display = new StringBuilder();
		display.append("Move History:\n");
		display.append("=============\n");

		for (int i = 0; i < moves.size(); i += 2) {
			int moveNumber = (i / 2) + 1;
			display.append(String.format("%d. ", moveNumber));

			// White's move
			if (i < moves.size()) {
				display.append(String.format("%-8s", moves.get(i).getAlgebraicNotation()));
			}

			// Black's move
			if (i + 1 < moves.size()) {
				display.append(String.format("%-8s", moves.get(i + 1).getAlgebraicNotation()));
			}

			display.append("\n");
		}

		return display.toString();
	}

	/**
	 * Gets the last move played
	 */
	public Move getLastMove() {
		if (moves.isEmpty()) {
			return null;
		}
		return moves.get(currentMoveIndex);
	}

	/**
	 * Gets all moves in the history
	 */
	public List<Move> getAllMoves() {
		return new ArrayList<>(moves);
	}

	/**
	 * Gets the number of moves played
	 */
	public int getMoveCount() {
		return moves.size();
	}

	/**
	 * Checks if undo is possible
	 */
	public boolean canUndo() {
		return currentMoveIndex >= 0;
	}

	/**
	 * Checks if redo is possible
	 */
	public boolean canRedo() {
		return currentMoveIndex < moves.size() - 1;
	}

	/**
	 * Moves back one position in history (for undo functionality)
	 */
	public Move undoMove() {
		if (!canUndo()) {
			return null;
		}

		Move move = moves.get(currentMoveIndex);
		currentMoveIndex--;
		return move;
	}

	/**
	 * Moves forward one position in history (for redo functionality)
	 */
	public Move redoMove() {
		if (!canRedo()) {
			return null;
		}

		currentMoveIndex++;
		return moves.get(currentMoveIndex);
	}

	/**
	 * Exports the game in PGN (Portable Game Notation) format
	 */
	public String exportToPGN(String whitePlayer, String blackPlayer, String result) {
		StringBuilder pgn = new StringBuilder();

		// PGN Headers
		LocalDateTime now = LocalDateTime.now();
		pgn.append("[Event \"ChessAI Game\"]\n");
		pgn.append("[Site \"Local\"]\n");
		pgn.append("[Date \"").append(now.format(DateTimeFormatter.ofPattern("yyyy.MM.dd")))
		        .append("\"]\n");
		pgn.append("[Round \"1\"]\n");
		pgn.append("[White \"").append(whitePlayer).append("\"]\n");
		pgn.append("[Black \"").append(blackPlayer).append("\"]\n");
		pgn.append("[Result \"").append(result).append("\"]\n");
		pgn.append("\n");

		// Move list
		for (int i = 0; i < moves.size(); i += 2) {
			int moveNumber = (i / 2) + 1;
			pgn.append(moveNumber).append(". ");

			// White's move
			if (i < moves.size()) {
				pgn.append(moves.get(i).getAlgebraicNotation()).append(" ");
			}

			// Black's move
			if (i + 1 < moves.size()) {
				pgn.append(moves.get(i + 1).getAlgebraicNotation()).append(" ");
			}

			// Add line break every 8 moves for readability
			if (moveNumber % 8 == 0) {
				pgn.append("\n");
			}
		}

		pgn.append(result);
		return pgn.toString();
	}

	/**
	 * Saves the game to a PGN file
	 */
	public boolean saveToPGNFile(String filename, String whitePlayer, String blackPlayer,
	        String result) {
		try {
			String pgnContent = exportToPGN(whitePlayer, blackPlayer, result);
			Path path = Paths.get(filename);
			Files.write(path, pgnContent.getBytes());
			return true;
		} catch (IOException e) {
			Log.error("Error saving PGN file: " + e.getMessage(), e);
			return false;
		}
	}

	/**
	 * Loads a game from a PGN file
	 *
	 * @param filename
	 *            The PGN file to load
	 * @return PGNGameData containing headers and moves, or null if failed
	 */
	public static PGNGameData loadFromPGNFile(String filename) {
		try {
			Path path = Paths.get(filename);
			if (!Files.exists(path)) {
				Log.warn("PGN file not found: " + filename);
				return null;
			}

			String content = Files.readString(path);
			return parsePGN(content);
		} catch (IOException e) {
			Log.error("Error reading PGN file: " + e.getMessage(), e);
			return null;
		}
	}

	/**
	 * Parses PGN content and extracts headers and moves
	 *
	 * @param pgnContent
	 *            The PGN content as a string
	 * @return PGNGameData containing parsed information
	 */
	public static PGNGameData parsePGN(String pgnContent) {
		PGNGameData gameData = new PGNGameData();
		String[] lines = pgnContent.split("\n");

		boolean inHeaders = true;
		StringBuilder moveText = new StringBuilder();

		for (String line : lines) {
			line = line.trim();

			if (line.isEmpty()) {
				if (inHeaders) {
					inHeaders = false; // Empty line separates headers from moves
				}
				continue;
			}

			if (inHeaders && line.startsWith("[") && line.endsWith("]")) {
				// Parse header
				parseHeader(line, gameData);
			} else if (!inHeaders) {
				// Accumulate move text
				moveText.append(line).append(" ");
			}
		}

		// Parse moves
		if (moveText.length() > 0) {
			gameData.moves = parseMoves(moveText.toString().trim());
		}

		return gameData;
	}

	/**
	 * Parses a PGN header line
	 */
	private static void parseHeader(String headerLine, PGNGameData gameData) {
		// Remove brackets and split on first quote
		String content = headerLine.substring(1, headerLine.length() - 1);
		int firstQuote = content.indexOf('"');
		if (firstQuote == -1)
			return;

		String key = content.substring(0, firstQuote).trim();
		String value = content.substring(firstQuote + 1);
		if (value.endsWith("\"")) {
			value = value.substring(0, value.length() - 1);
		}

		switch (key) {
			case "Event":
				gameData.event = value;
				break;
			case "Site":
				gameData.site = value;
				break;
			case "Date":
				gameData.date = value;
				break;
			case "Round":
				gameData.round = value;
				break;
			case "White":
				gameData.whitePlayer = value;
				break;
			case "Black":
				gameData.blackPlayer = value;
				break;
			case "Result":
				gameData.result = value;
				break;
		}
	}

	/**
	 * Parses move text and extracts algebraic notation moves
	 */
	private static List<String> parseMoves(String moveText) {
		List<String> moves = new ArrayList<>();

		// Remove result notation at the end
		moveText = moveText.replaceAll("\\s+(1-0|0-1|1/2-1/2|\\*)\\s*$", "");

		// Split by move numbers and process
		String[] tokens = moveText.split("\\s+");

		for (String token : tokens) {
			token = token.trim();
			if (token.isEmpty())
				continue;

			// Skip move numbers (e.g., "1.", "2.", etc.)
			if (token.matches("\\d+\\."))
				continue;

			// Skip comments in braces or parentheses
			if (token.startsWith("{") || token.startsWith("("))
				continue;

			// Clean up the move notation
			token = cleanMoveNotation(token);

			if (!token.isEmpty() && isValidMoveNotation(token)) {
				moves.add(token);
			}
		}

		return moves;
	}

	/**
	 * Cleans move notation by removing annotations
	 */
	private static String cleanMoveNotation(String move) {
		// Remove check (+), checkmate (#), annotation (!,?, etc.)
		return move.replaceAll("[+#!?]", "");
	}

	/**
	 * Basic validation of move notation
	 */
	private static boolean isValidMoveNotation(String move) {
		if (move.length() < 2)
			return false;

		// Castling
		if (move.equals("O-O") || move.equals("O-O-O"))
			return true;

		// Standard notation should end with a square (e.g., e4, Nf3, axb5)
		return move.matches(".*[a-h][1-8].*");
	}

	/**
	 * Data class to hold parsed PGN information
	 */
	public static class PGNGameData {
		public String event = "";
		public String site = "";
		public String date = "";
		public String round = "";
		public String whitePlayer = "";
		public String blackPlayer = "";
		public String result = "";
		public List<String> moves = new ArrayList<>();

		@Override
		public String toString() {
			return String.format("PGN Game: %s vs %s (%s) - %d moves", whitePlayer, blackPlayer,
			        result, moves.size());
		}
	}

	/**
	 * Creates a deep copy of this MoveHistory
	 */
	public MoveHistory copy() {
		MoveHistory copy = new MoveHistory();
		copy.moves.addAll(this.moves);
		copy.currentMoveIndex = this.currentMoveIndex;
		copy.positionHistory.addAll(this.positionHistory);
		copy.halfmoveClock = this.halfmoveClock;
		return copy;
	}

	/**
	 * Clears the move history
	 */
	public void clear() {
		moves.clear();
		currentMoveIndex = -1;
		positionHistory.clear();
		halfmoveClock = 0;
	}

	// Position repetition tracking
	private final ArrayList<String> positionHistory = new ArrayList<>();
	private int halfmoveClock = 0;

	/**
	 * Gets the history of positions for threefold repetition checking
	 */
	public List<String> getPositionHistory() {
		return new ArrayList<>(positionHistory);
	}

	/**
	 * Adds a position to the history. Truncates any positions after the current
	 * move index first (for undo/redo support).
	 */
	public void addPosition(String position) {
		// Remove any positions after current move index before adding the new one.
		// After undo, currentMoveIndex points to the last active move, so we want
		// exactly (currentMoveIndex + 1) positions before adding the new one.
		// At the start (currentMoveIndex == -1), we want 0 positions before adding.
		while (positionHistory.size() > currentMoveIndex + 1) {
			positionHistory.remove(positionHistory.size() - 1);
		}
		positionHistory.add(position);
	}

	/**
	 * Checks if the current position has appeared three times
	 */
	public boolean isThreefoldRepetition() {
		if (positionHistory.isEmpty())
			return false;
		String currentPosition = positionHistory.get(positionHistory.size() - 1);
		int repetitions = 0;

		for (String position : positionHistory) {
			if (position.equals(currentPosition)) {
				repetitions++;
				if (repetitions >= 3) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Gets the half-move clock for fifty-move rule checking Increments on each
	 * move, resets on pawn moves and captures
	 */
	public int getHalfmoveClock() {
		return halfmoveClock;
	}

	/**
	 * Updates the half-move clock
	 *
	 * @param isPawnMove
	 *            true if a pawn was moved
	 * @param isCapture
	 *            true if a piece was captured
	 */
	public void updateHalfmoveClock(boolean isPawnMove, boolean isCapture) {
		if (isPawnMove || isCapture) {
			halfmoveClock = 0;
			// Debug output removed
		} else {
			halfmoveClock++;
			// Debug output removed
		}
	}
}
