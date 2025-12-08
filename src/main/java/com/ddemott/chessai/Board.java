package com.ddemott.chessai;

import java.util.ArrayList;
import java.util.List;

import com.ddemott.chessai.pieces.Bishop;
import com.ddemott.chessai.pieces.IPiece;
import com.ddemott.chessai.pieces.King;
import com.ddemott.chessai.pieces.Knight;
import com.ddemott.chessai.pieces.Pawn;
import com.ddemott.chessai.pieces.Queen;
import com.ddemott.chessai.pieces.Rook;

public class Board {
	private IPiece[][] board; // 2D array to represent the board
	private String enPassantTarget; // Target square for en passant capture (e.g., "e3")
	private List<IPiece> capturedWhitePieces = new ArrayList<>();
	private List<IPiece> capturedBlackPieces = new ArrayList<>();

	public Board() {
		this(true); // Default constructor initializes the board
	}

	// Private constructor for cloning
	private Board(boolean initialize) {
		board = new IPiece[8][8]; // 8x8 chess board
		enPassantTarget = null; // No en passant target initially
		if (initialize) {
			initializeBoard();
		}
	}

	private void initializeBoard() {
		// Adding Pawns for both sides
		for (char col = 'a'; col <= 'h'; col++) {
			board[1][col - 'a'] = new Pawn(Side.WHITE, col + "2");
			board[6][col - 'a'] = new Pawn(Side.BLACK, col + "7");
		}

		// Adding Rooks for both sides
		board[0][0] = new Rook(Side.WHITE, "a1");
		board[0][7] = new Rook(Side.WHITE, "h1");
		board[7][0] = new Rook(Side.BLACK, "a8");
		board[7][7] = new Rook(Side.BLACK, "h8");

		// Adding Knights for both sides
		board[0][1] = new Knight(Side.WHITE, "b1");
		board[0][6] = new Knight(Side.WHITE, "g1");
		board[7][1] = new Knight(Side.BLACK, "b8");
		board[7][6] = new Knight(Side.BLACK, "g8");

		// Adding Bishops for both sides
		board[0][2] = new Bishop(Side.WHITE, "c1");
		board[0][5] = new Bishop(Side.WHITE, "f1");
		board[7][2] = new Bishop(Side.BLACK, "c8");
		board[7][5] = new Bishop(Side.BLACK, "f8");

		// Adding Queens for both sides
		board[0][3] = new Queen(Side.WHITE, "d1");
		board[7][3] = new Queen(Side.BLACK, "d8");

		// Adding Kings for both sides
		board[0][4] = new King(Side.WHITE, "e1");
		board[7][4] = new King(Side.BLACK, "e8");
	}

	public IPiece getPieceAt(String position) {
		int[] coords = convertPositionToCoordinates(position);
		if (coords == null) {
			return null; // Out of bounds or invalid
		}
		if (coords[0] < 0 || coords[0] >= 8 || coords[1] < 0 || coords[1] >= 8) {
			return null; // Out of bounds
		}
		return board[coords[0]][coords[1]];
	}

	public void setPieceAt(String position, IPiece piece) {
		int[] coords = convertPositionToCoordinates(position);
		if (coords == null) {
			return; // Out of bounds or invalid
		}
		if (coords[0] < 0 || coords[0] >= 8 || coords[1] < 0 || coords[1] >= 8) {
			return; // Out of bounds
		}
		board[coords[0]][coords[1]] = piece;
		if (piece != null) {
			// Ensure piece internal position always matches board coordinates
			piece.setPosition(position);
		}
	}

	public int[] convertPositionToCoordinates(String position) {
		if (position == null) {
			return null; // Return null for invalid positions
		}
		position = position.toLowerCase(); // Ensure position is in lowercase
		if (position.length() != 2) {
			return null; // Invalid position format
		}
		char column = position.charAt(0);
		int row = position.charAt(1) - '1';
		int col = column - 'a';
		// Check bounds
		if (row < 0 || row >= 8 || col < 0 || col >= 8) {
			return null;
		}
		return new int[]{row, col};
	}

	public String convertCoordinatesToPosition(int row, int col) {
		if (row < 0 || row >= 8 || col < 0 || col >= 8) {
			return null; // Out of bounds
		}
		char column = (char) ('a' + col);
		char rowChar = (char) ('1' + row);
		return String.valueOf(column) + rowChar;
	}

	/**
	 * Helper method to check if a value is between two others (inclusive)
	 */
	private boolean isBetweenInclusive(int value, int bound1, int bound2) {
		return value >= Math.min(bound1, bound2) && value <= Math.max(bound1, bound2);
	}

	public boolean movePiece(String from, String to) {
		IPiece piece = getPieceAt(from);
		if (piece == null) {
			return false;
		}

		// Temporarily sync piece position for validation
		String oldPosition = piece.getPosition();
		piece.setPosition(from);
		boolean isValid = piece.isValidMove(to, this);
		piece.setPosition(oldPosition); // Restore

		if (!isValid) {
			return false;
		}
		// Block illegal moves for pinned pieces or moves that expose king to check
		if (wouldExposeKingToCheck(from, to)) {
			return false;
		}
		// Handle special cases first
		// Castling
		if (piece instanceof King
				&& Math.abs(convertPositionToCoordinates(to)[1] - convertPositionToCoordinates(from)[1]) == 2) {
			boolean castlingSuccess = executeCastling(from, to);
			if (castlingSuccess) {
				enPassantTarget = null;
			}
			return castlingSuccess;
		}
		// En passant
		if (piece instanceof Pawn && isEnPassantCapture(from, to)) {
			return executeEnPassant(from, to);
		}
		// If this is a pawn moving to the last rank, it must specify a promotion piece
		if (piece instanceof Pawn) {
			int[] toCoords = convertPositionToCoordinates(to);
			boolean isPromotionRank = (piece.getSide() == Side.WHITE && toCoords[0] == GameConstants.RANK_8)
					|| (piece.getSide() == Side.BLACK && toCoords[0] == GameConstants.RANK_1);
			if (isPromotionRank) {
				return false; // Must use movePiece(from, to, promotionPiece) for promotions
			}
		}
		// Regular move
		IPiece captured = getPieceAt(to);
		if (captured != null) {
			if (captured.getSide() == Side.WHITE) {
				capturedWhitePieces.add(captured);
			} else {
				capturedBlackPieces.add(captured);
			}
		}
		setPieceAt(to, piece);
		setPieceAt(from, null);
		piece.setPosition(to);
		piece.setHasMoved(true);
		// Track pawn two-square moves for en passant
		if (piece instanceof Pawn && isPawnTwoSquareMove(from, to)) {
			int[] fromCoords = convertPositionToCoordinates(from);
			int[] toCoords = convertPositionToCoordinates(to);
			int targetRow = (fromCoords[0] + toCoords[0]) / 2;
			int targetCol = fromCoords[1];
			enPassantTarget = convertCoordinatesToPosition(targetRow, targetCol);
		} else {
			enPassantTarget = null;
		}
		return true;
	}

	/**
	 * Checks if moving a piece would expose the king to check
	 * 
	 * @param from
	 *            Starting position
	 * @param to
	 *            Ending position
	 * @return true if the move would expose the king to check
	 */
	public boolean wouldExposeKingToCheck(String from, String to) {
		IPiece piece = getPieceAt(from);
		if (piece == null || piece instanceof King) {
			return false; // Kings can't be pinned
		}

		Side opponentSide = piece.getSide().flip();
		String kingPosition = findKingPosition(piece.getSide());
		if (kingPosition == null) {
			return false;
		}
		int[] kingCoords = convertPositionToCoordinates(kingPosition);
		int[] pieceCoords = convertPositionToCoordinates(from);
		int[] targetCoords = convertPositionToCoordinates(to);

		// Check if piece is aligned with king (orthogonal or diagonal)
		int dRow = Integer.signum(pieceCoords[0] - kingCoords[0]);
		int dCol = Integer.signum(pieceCoords[1] - kingCoords[1]);
		boolean isOrthogonal = (dRow == 0 || dCol == 0);
		boolean isDiagonal = (Math.abs(pieceCoords[0] - kingCoords[0]) == Math.abs(pieceCoords[1] - kingCoords[1]));
		if (!isOrthogonal && !isDiagonal) {
			// Not on a pin line, just check normal check exposure
			Board clonedBoard = this.clone();
			clonedBoard.setPieceAt(to, piece.clonePiece());
			clonedBoard.setPieceAt(from, null);
			return clonedBoard.isKingInCheck(piece.getSide());
		}

		// Search for a pinning piece in the direction from piece to king (opposite
		// direction)
		int pinDirRow = -dRow;
		int pinDirCol = -dCol;
		int curRow = pieceCoords[0] + pinDirRow;
		int curCol = pieceCoords[1] + pinDirCol;
		boolean foundKing = false;
		while (curRow >= 0 && curRow < 8 && curCol >= 0 && curCol < 8) {
			if (curRow == kingCoords[0] && curCol == kingCoords[1]) {
				foundKing = true;
				break;
			}
			if (board[curRow][curCol] != null) {
				break;
			}
			curRow += pinDirRow;
			curCol += pinDirCol;
		}
		if (!foundKing) {
			// Not on a pin line, just check normal check exposure
			Board clonedBoard = this.clone();
			clonedBoard.setPieceAt(to, piece.clonePiece());
			clonedBoard.setPieceAt(from, null);
			return clonedBoard.isKingInCheck(piece.getSide());
		}

		// Now search for a pinning attacker in the direction from piece away from king
		int attRow = pieceCoords[0] + dRow;
		int attCol = pieceCoords[1] + dCol;
		IPiece pinningAttacker = null;
		while (attRow >= 0 && attRow < 8 && attCol >= 0 && attCol < 8) {
			IPiece att = board[attRow][attCol];
			if (att != null) {
				if (att.getSide() == opponentSide) {
					if ((isOrthogonal && (att instanceof Rook || att instanceof Queen))
							|| (isDiagonal && (att instanceof Bishop || att instanceof Queen))) {
						pinningAttacker = att;
					}
				}
				break;
			}
			attRow += dRow;
			attCol += dCol;
		}
		if (pinningAttacker == null) {
			// Not pinned, just check normal check exposure
			Board clonedBoard = this.clone();
			clonedBoard.setPieceAt(to, piece.clonePiece());
			clonedBoard.setPieceAt(from, null);
			return clonedBoard.isKingInCheck(piece.getSide());
		}

		// If pinned, only allow moves strictly along the pin line between king and
		// attacker (not past attacker)
		// Knights cannot move if pinned
		if (piece instanceof Knight) {
			return true;
		}
		// Check if move is on the pin line and between king and attacker
		int moveVecRow = targetCoords[0] - kingCoords[0];
		int moveVecCol = targetCoords[1] - kingCoords[1];
		// Must be collinear with pin direction
		boolean collinear = (dRow == 0 ? moveVecRow == 0 : moveVecRow % dRow == 0)
				&& (dCol == 0 ? moveVecCol == 0 : moveVecCol % dCol == 0);
		// Must be in the same direction
		boolean sameDirection = (dRow == 0 || Integer.signum(moveVecRow) == dRow)
				&& (dCol == 0 || Integer.signum(moveVecCol) == dCol);
		// Must not go past the attacker
		boolean notPastAttacker = isBetweenInclusive(targetCoords[0], kingCoords[0], attRow - dRow)
				&& isBetweenInclusive(targetCoords[1], kingCoords[1], attCol - dCol);
		if (!(collinear && sameDirection && notPastAttacker)) {
			return true; // Illegal move for pinned piece
		}
		// Otherwise, simulate the move and check for check
		Board clonedBoard = this.clone();
		clonedBoard.setPieceAt(to, piece.clonePiece());
		clonedBoard.setPieceAt(from, null);
		return clonedBoard.isKingInCheck(piece.getSide());
	}

	/**
	 * Execute castling move - moves both king and rook
	 */
	private boolean executeCastling(String kingFrom, String kingTo) {
		IPiece king = getPieceAt(kingFrom);
		int[] kingFromCoords = convertPositionToCoordinates(kingFrom);
		int[] kingToCoords = convertPositionToCoordinates(kingTo);

		// Determine if kingside or queenside castling
		boolean isKingside = kingToCoords[1] > kingFromCoords[1];

		// Get rook positions
		String rookFrom, rookTo;
		if (isKingside) {
			rookFrom = convertCoordinatesToPosition(kingFromCoords[0], 7); // h-file
			rookTo = convertCoordinatesToPosition(kingFromCoords[0], 5); // f-file
		} else {
			rookFrom = convertCoordinatesToPosition(kingFromCoords[0], 0); // a-file
			rookTo = convertCoordinatesToPosition(kingFromCoords[0], 3); // d-file
		}

		IPiece rook = getPieceAt(rookFrom);

		// Move king
		setPieceAt(kingTo, king);
		setPieceAt(kingFrom, null);
		king.setPosition(kingTo);
		king.setHasMoved(true);

		// Move rook
		setPieceAt(rookTo, rook);
		setPieceAt(rookFrom, null);
		rook.setPosition(rookTo);
		if (rook != null) {
			rook.setHasMoved(true);
		}

		return true;
	}

	/**
	 * Check if a move is a pawn two-square move that enables en passant
	 */
	private boolean isPawnTwoSquareMove(String from, String to) {
		int[] fromCoords = convertPositionToCoordinates(from);
		int[] toCoords = convertPositionToCoordinates(to);

		// Check if it's a pawn moving exactly 2 squares vertically
		return Math.abs(toCoords[0] - fromCoords[0]) == 2 && fromCoords[1] == toCoords[1];
	}

	/**
	 * Check if a move is an en passant capture
	 */
	private boolean isEnPassantCapture(String from, String to) {
		if (enPassantTarget == null) {
			return false;
		}

		// Check if the destination matches the en passant target
		return to.equals(enPassantTarget);
	}

	/**
	 * Execute an en passant capture
	 */
	private boolean executeEnPassant(String from, String to) {
		IPiece pawn = getPieceAt(from);
		int[] toCoords = convertPositionToCoordinates(to);

		// Determine the captured pawn's position
		int capturedPawnRow = pawn.getSide() == Side.WHITE ? toCoords[0] - 1 : toCoords[0] + 1;
		String capturedPawnPosition = convertCoordinatesToPosition(capturedPawnRow, toCoords[1]);

		// Move the capturing pawn
		setPieceAt(to, pawn);
		setPieceAt(from, null);
		pawn.setPosition(to);
		pawn.setHasMoved(true);

		// Remove the captured pawn
		setPieceAt(capturedPawnPosition, null);

		// Clear en passant target
		enPassantTarget = null;

		return true;
	}

	public List<String> getAllPossibleMoves(String color) {
		// Wrapper for legacy String support
		return getAllPossibleMoves(color.equalsIgnoreCase("White") ? Side.WHITE : Side.BLACK);
	}

	public List<String> getAllPossibleMoves(Side side) {
		List<String> possibleMoves = new ArrayList<>();
		for (int row = 0; row < 8; row++) {
			for (int col = 0; col < 8; col++) {
				IPiece piece = board[row][col];
				if (piece != null && piece.getSide() == side) {
					List<String> moves = piece.getAllPossibleMoves(this);
					if (moves != null && !moves.isEmpty()) {
						possibleMoves.addAll(moves);
					}
				}
			}
		}
		return possibleMoves;
	}

	public IPiece[][] getBoardArray() {
		return board;
	}

	@Override
	public Board clone() {
		Board newBoard = new Board(false); // Don't initialize - we'll copy pieces manually
		for (int row = 0; row < 8; row++) {
			for (int col = 0; col < 8; col++) {
				if (this.board[row][col] != null) {
					newBoard.board[row][col] = this.board[row][col].clonePiece();
				}
			}
		}
		// Copy en passant target state
		newBoard.enPassantTarget = this.enPassantTarget;
		return newBoard;
	}

	/**
	 * Get the current en passant target square
	 * 
	 * @return the target square (e.g., "e3") or null if no en passant is possible
	 */
	public String getEnPassantTarget() {
		return enPassantTarget;
	}

	/**
	 * Set the en passant target square
	 * 
	 * @param target
	 *            the target square (e.g., "e3") or null to clear
	 */
	public void setEnPassantTarget(String target) {
		this.enPassantTarget = target;
	}

	// Method to get board representation as string for display purposes
	public String getBoardRepresentation() {
		StringBuilder sb = new StringBuilder();
		sb.append("  a b c d e f g h\n");
		for (int row = 7; row >= 0; row--) {
			sb.append((row + 1)).append(" ");
			for (int col = 0; col < 8; col++) {
				IPiece piece = board[row][col];
				if (piece == null) {
					sb.append(". ");
				} else {
					char symbol = piece.getSymbol();
					symbol = piece.getSide() == Side.WHITE ? symbol : Character.toLowerCase(symbol);
					sb.append(symbol).append(" ");
				}
			}
			sb.append((row + 1)).append("\n");
		}
		sb.append("  a b c d e f g h\n");
		return sb.toString();
	}

	/**
	 * Check if the king of the specified color is in check
	 */
	public boolean isKingInCheck(String kingColor) {
		// Wrapper for legacy String support
		return isKingInCheck(kingColor.equalsIgnoreCase("White") ? Side.WHITE : Side.BLACK);
	}

	public boolean isKingInCheck(Side kingSide) {
		// Find the king position
		String kingPosition = findKingPosition(kingSide);
		if (kingPosition == null) {
			return false; // No king found (shouldn't happen in normal game)
		}

		Side opponentSide = kingSide.flip();

		// Directly check each opponent piece to see if it can attack the king
		for (int row = 0; row < 8; row++) {
			for (int col = 0; col < 8; col++) {
				IPiece piece = board[row][col];
				if (piece != null && piece.getSide() == opponentSide) {
					String piecePosition = convertCoordinatesToPosition(row, col);
					// Temporarily update piece position for validation
					String oldPosition = piece.getPosition();
					piece.setPosition(piecePosition);

					// For non-king pieces, use normal validation
					if (!(piece instanceof King)) {
						if (piece.isValidMove(kingPosition, this)) {
							piece.setPosition(oldPosition); // Restore
							return true;
						}
					} else {
						// For king pieces, do NOT count adjacent kings as attacking each other
						// Kings cannot legally move next to each other
						int[] targetCoords = convertPositionToCoordinates(kingPosition);
						int dx = Math.abs(targetCoords[0] - row);
						int dy = Math.abs(targetCoords[1] - col);
						// If kings are adjacent, do NOT count as check
						if (dx <= 1 && dy <= 1 && (dx != 0 || dy != 0)) {
							piece.setPosition(oldPosition); // Restore
							continue;
						}
					}
					piece.setPosition(oldPosition); // Restore
				}
			}
		}
		return false;
	}

	/**
	 * Helper method to check if the path between two positions is clear
	 */
	public boolean isPathClear(String from, String to) {
		int[] fromCoords = convertPositionToCoordinates(from);
		int[] toCoords = convertPositionToCoordinates(to);

		int rowDir = Integer.compare(toCoords[0], fromCoords[0]); // -1, 0, or 1
		int colDir = Integer.compare(toCoords[1], fromCoords[1]); // -1, 0, or 1

		int row = fromCoords[0] + rowDir;
		int col = fromCoords[1] + colDir;

		while (row != toCoords[0] || col != toCoords[1]) {
			if (board[row][col] != null) {
				return false; // Path is blocked
			}

			row += rowDir;
			col += colDir;
		}

		return true; // Path is clear
	}

	/**
	 * Find the position of the king for the specified color
	 */
	public String findKingPosition(String kingColor) {
		if (kingColor == null) {
			return null;
		}
		if (kingColor.equalsIgnoreCase("White")) {
			return findKingPosition(Side.WHITE);
		} else if (kingColor.equalsIgnoreCase("Black")) {
			return findKingPosition(Side.BLACK);
		}
		return null; // Invalid color
	}

	public String findKingPosition(Side kingSide) {
		for (int row = 0; row < 8; row++) {
			for (int col = 0; col < 8; col++) {
				IPiece piece = board[row][col];
				if (piece instanceof King && piece.getSide() == kingSide) {
					return convertCoordinatesToPosition(row, col);
				}
			}
		}
		return null; // King not found
	}

	/**
	 * Check if a square is under attack by the opponent Uses a non-recursive
	 * approach to avoid infinite loops during castling validation
	 */
	public boolean isSquareUnderAttack(String position, String defendingColor) {
		return isSquareUnderAttack(position, defendingColor.equalsIgnoreCase("White") ? Side.WHITE : Side.BLACK);
	}

	public boolean isSquareUnderAttack(String position, Side defendingSide) {
		Side attackingSide = defendingSide.flip();
		// Check each opposing piece to see if it can attack the position
		for (int row = 0; row < 8; row++) {
			for (int col = 0; col < 8; col++) {
				IPiece piece = board[row][col];
				if (piece != null && piece.getSide() == attackingSide) {
					String piecePosition = convertCoordinatesToPosition(row, col);
					// Temporarily update piece position for validation
					String oldPosition = piece.getPosition();
					piece.setPosition(piecePosition);

					// For non-king pieces, use normal validation
					if (!(piece instanceof King)) {
						if (piece.isValidMove(position, this)) {
							piece.setPosition(oldPosition); // Restore
							return true;
						}
					} else {
						// For king pieces, only check normal moves (not castling) to avoid recursion
						int[] targetCoords = convertPositionToCoordinates(position);
						int dx = Math.abs(targetCoords[0] - row);
						int dy = Math.abs(targetCoords[1] - col);
						// King can attack one square in any direction
						if (dx <= 1 && dy <= 1 && (dx != 0 || dy != 0)) {
							piece.setPosition(oldPosition); // Restore
							return true;
						}
					}
					piece.setPosition(oldPosition); // Restore
				}
			}
		}

		return false;
	}

	/**
	 * Check if the specified player is in checkmate
	 */
	public boolean isCheckmate(String playerColor) {
		return isCheckmate(playerColor.equalsIgnoreCase("White") ? Side.WHITE : Side.BLACK);
	}

	public boolean isCheckmate(Side playerSide) {
		// Regular checkmate logic
		if (!isKingInCheck(playerSide)) {
			return false; // Not in check, so can't be checkmate
		}

		// Try all possible moves to see if any gets out of check
		for (int fromRow = 0; fromRow < 8; fromRow++) {
			for (int fromCol = 0; fromCol < 8; fromCol++) {
				IPiece piece = board[fromRow][fromCol];
				if (piece != null && piece.getSide() == playerSide) {
					String from = convertCoordinatesToPosition(fromRow, fromCol);

					// Temporarily sync piece position for validation
					String oldPosition = piece.getPosition();
					piece.setPosition(from);

					// Get all possible moves for this piece
					for (int toRow = 0; toRow < 8; toRow++) {
						for (int toCol = 0; toCol < 8; toCol++) {
							String to = convertCoordinatesToPosition(toRow, toCol);

							// Skip if it's not a valid move
							if (!piece.isValidMove(to, this)) {
								continue;
							}

							// Simulate the move
							Board clonedBoard = this.clone();

							// Execute the move on the cloned board
							IPiece clonedPiece = clonedBoard.getPieceAt(from);
							if (clonedPiece != null) {
								clonedPiece.setPosition(to);
								clonedBoard.setPieceAt(to, clonedPiece);
								clonedBoard.setPieceAt(from, null);

								// Check if this move gets out of check
								if (!clonedBoard.isKingInCheck(playerSide)) {
									piece.setPosition(oldPosition); // Restore
									return false; // Found a move that gets out of check
								}
							}
						}
					}

					piece.setPosition(oldPosition); // Restore
				}
			}
		}

		return true; // No moves get out of check - checkmate
	}

	/**
	 * Check if the specified player is in stalemate
	 */
	public boolean isStalemate(String playerColor) {
		return isStalemate(playerColor.equalsIgnoreCase("White") ? Side.WHITE : Side.BLACK);
	}

	public boolean isStalemate(Side playerSide) {
		if (isKingInCheck(playerSide)) {
			return false; // In check, so can't be stalemate
		}

		// Check for any legal moves
		for (int fromRow = 0; fromRow < 8; fromRow++) {
			for (int fromCol = 0; fromCol < 8; fromCol++) {
				IPiece piece = board[fromRow][fromCol];
				if (piece != null && piece.getSide() == playerSide) {
					String from = convertCoordinatesToPosition(fromRow, fromCol);

					// Temporarily sync piece position for validation
					String oldPosition = piece.getPosition();
					piece.setPosition(from);

					// For each destination square
					for (int toRow = 0; toRow < 8; toRow++) {
						for (int toCol = 0; toCol < 8; toCol++) {
							String to = convertCoordinatesToPosition(toRow, toCol);

							// Skip if the move isn't valid according to piece rules
							if (!piece.isValidMove(to, this)) {
								continue;
							}

							// For pawn promotion, check if any promotion is legal
							if (piece instanceof Pawn) {
								boolean isPromotionRank = (piece.getSide() == Side.WHITE && toRow == 7)
										|| (piece.getSide() == Side.BLACK && toRow == 0);
								if (isPromotionRank) {
									// Try all promotion pieces
									String[] promotions = {"Q", "R", "B", "N"};
									for (String promotionPiece : promotions) {
										Board clonedBoard = this.clone();
										IPiece promotedPiece = clonedBoard.createPromotionPiece(promotionPiece,
												piece.getColor(), to);
										if (promotedPiece != null) {
											clonedBoard.setPieceAt(from, null);
											clonedBoard.setPieceAt(to, promotedPiece);

											// If this promotion doesn't leave king in check, it's legal
											if (!clonedBoard.isKingInCheck(piece.getSide())) {
												piece.setPosition(oldPosition); // Restore
												return false; // Found a legal promotion move
											}
										}
									}
									continue; // Skip regular move check for promotion moves
								}
							}

							// Skip if this move would expose the king to check
							if (wouldExposeKingToCheck(from, to)) {
								continue;
							}

							// If we reach here, this is a legal move
							piece.setPosition(oldPosition); // Restore
							return false;
						}
					}

					piece.setPosition(oldPosition); // Restore
				}
			}
		}

		return true; // No legal moves found - stalemate
	}

	/**
	 * Checks if a piece is pinned (can't move because it would expose the king to
	 * check)
	 * 
	 * @param position
	 *            Position of the piece to check
	 * @return true if the piece is pinned, false otherwise
	 */
	public boolean isPiecePinned(String position) {
		IPiece piece = getPieceAt(position);
		if (piece == null) {
			return false;
		}

		String kingPosition = findKingPosition(piece.getSide());
		if (kingPosition == null) {
			return false;
		}

		int[] kingCoords = convertPositionToCoordinates(kingPosition);
		int[] pieceCoords = convertPositionToCoordinates(position);
		if (kingCoords == null || pieceCoords == null) {
			return false;
		}

		int dRow = Integer.signum(pieceCoords[0] - kingCoords[0]);
		int dCol = Integer.signum(pieceCoords[1] - kingCoords[1]);
		boolean isOrthogonal = (dRow == 0 || dCol == 0);
		boolean isDiagonal = (Math.abs(pieceCoords[0] - kingCoords[0]) == Math.abs(pieceCoords[1] - kingCoords[1]));
		if (isOrthogonal || isDiagonal) {
			// Check for pinning attacker in the direction from piece away from king
			int attRow = pieceCoords[0] + dRow;
			int attCol = pieceCoords[1] + dCol;
			while (attRow >= 0 && attRow < 8 && attCol >= 0 && attCol < 8) {
				IPiece att = board[attRow][attCol];
				if (att != null) {
					if (att.getSide() != piece.getSide()) {
						if ((isOrthogonal && (att instanceof Rook || att instanceof Queen))
								|| (isDiagonal && (att instanceof Bishop || att instanceof Queen))) {
							// Check that there are no other pieces between piece and attacker
							int checkRow = kingCoords[0] + dRow;
							int checkCol = kingCoords[1] + dCol;
							boolean clear = true;
							while ((checkRow != pieceCoords[0] || checkCol != pieceCoords[1]) && clear) {
								if (board[checkRow][checkCol] != null) {
									clear = false;
								}
								checkRow += dRow;
								checkCol += dCol;
							}
							if (clear) {
								return true;
							}
						}
					}
					break;
				}
				attRow += dRow;
				attCol += dCol;
			}
		}

		// For knights, if aligned and attacker present, already handled above
		if (piece instanceof Knight) {
			return false;
		}

		// Fallback: check if every move exposes king to check
		List<String> possibleMoves = piece.getAllPossibleMoves(this);
		if (possibleMoves.isEmpty()) {
			return false; // No moves to check
		}
		boolean foundPin = false;
		for (String move : possibleMoves) {
			String[] parts = move.split(" ");
			if (parts.length == 2) {
				String to = parts[1];
				if (!piece.isValidMove(to, this)) {
					continue;
				}
				Board clonedBoard = this.clone();
				IPiece capturedPiece = clonedBoard.getPieceAt(to);
				clonedBoard.setPieceAt(to, piece.clonePiece());
				clonedBoard.setPieceAt(position, null);
				if (!clonedBoard.isKingInCheck(piece.getSide())) {
					return false; // Found a legal move, not pinned
				} else {
					foundPin = true;
				}
				clonedBoard.setPieceAt(position, piece.clonePiece());
				clonedBoard.setPieceAt(to, capturedPiece);
			}
		}
		return foundPin;
	}

	/**
	 * Move piece with optional pawn promotion
	 * 
	 * @param from
	 *            Starting position
	 * @param to
	 *            Ending position
	 * @param promotionPiece
	 *            Piece to promote to (Q, R, B, N) or null for no promotion
	 * @return true if move was successful
	 */
	public boolean movePiece(String from, String to, String promotionPiece) {
		IPiece piece = getPieceAt(from);
		if (piece == null) {
			return false;
		}

		// Temporarily sync piece position for validation
		String oldPosition = piece.getPosition();
		piece.setPosition(from);
		boolean isValid = piece.isValidMove(to, this);
		piece.setPosition(oldPosition); // Restore

		// Basic move validation
		if (!isValid) {
			return false;
		}

		// Check for pawn promotion
		if (piece instanceof Pawn) {
			int[] toCoords = convertPositionToCoordinates(to);
			boolean isPromotionRank = (piece.getSide() == Side.WHITE && toCoords[0] == 7)
					|| (piece.getSide() == Side.BLACK && toCoords[0] == 0);

			if (isPromotionRank) {
				// Must specify promotion piece when moving to promotion rank
				if (promotionPiece == null || !isValidPromotionPiece(promotionPiece)) {
					return false;
				}

				// For promotion moves, simulate the promotion and check if it exposes king to
				// check
				Board clonedBoard = this.clone();
				IPiece promotedPiece = createPromotionPiece(promotionPiece, piece.getColor(), to);
				if (promotedPiece == null) {
					return false;
				}

				// Execute the promotion on the cloned board
				clonedBoard.setPieceAt(from, null);
				clonedBoard.setPieceAt(to, promotedPiece);

				// Check if this would expose the king to check
				if (clonedBoard.isKingInCheck(piece.getSide())) {
					return false;
				}

				// Execute promotion move on actual board
				setPieceAt(from, null);
				setPieceAt(to, promotedPiece);
				return true;
			} else if (promotionPiece != null) {
				// Can't promote when not on promotion rank
				return false;
			}
		} else if (promotionPiece != null) {
			// Only pawns can promote
			return false;
		}

		// For non-promotion moves, check if it would expose king to check
		if (wouldExposeKingToCheck(from, to)) {
			return false;
		}

		// Regular move
		IPiece captured = getPieceAt(to);
		if (captured != null) {
			if (captured.getSide() == Side.WHITE) {
				capturedWhitePieces.add(captured);
			} else {
				capturedBlackPieces.add(captured);
			}
		}
		setPieceAt(to, piece);
		setPieceAt(from, null);
		piece.setPosition(to);
		piece.setHasMoved(true);
		// Track pawn two-square moves for en passant
		if (piece instanceof Pawn && isPawnTwoSquareMove(from, to)) {
			int[] fromCoords = convertPositionToCoordinates(from);
			int[] toCoords = convertPositionToCoordinates(to);
			int targetRow = (fromCoords[0] + toCoords[0]) / 2;
			int targetCol = fromCoords[1];
			enPassantTarget = convertCoordinatesToPosition(targetRow, targetCol);
		} else {
			enPassantTarget = null;
		}
		return true;
	}

	/**
	 * Check if a pawn move is a promotion
	 */
	/**
	 * Validate promotion piece type
	 */
	private boolean isValidPromotionPiece(String promotionPiece) {
		// Only Queen, Rook, Bishop, and Knight are allowed for promotion
		return promotionPiece.equals("Q") || promotionPiece.equals("R") || promotionPiece.equals("B")
				|| promotionPiece.equals("N");
	}

	/**
	 * Create a piece for promotion
	 */
	public IPiece createPromotionPiece(String pieceType, String color, String position) {
		Side side = color.equalsIgnoreCase("White") ? Side.WHITE : Side.BLACK;
		if (!isValidPromotionPiece(pieceType)) {
			return null;
		}

		switch (pieceType) {
			case "Q" :
				return new Queen(side, position);
			case "R" :
				return new Rook(side, position);
			case "B" :
				return new Bishop(side, position);
			case "N" :
				return new Knight(side, position);
			default :
				return null;
		}
	}

	/**
	 * Generate algebraic notation for promotion moves
	 */
	public String getPromotionNotation(String from, String to, String promotionPiece) {
		StringBuilder notation = new StringBuilder();

		int[] fromCoords = convertPositionToCoordinates(from);
		IPiece capturedPiece = getPieceAt(to);

		if (capturedPiece != null) {
			// Capture promotion: exf8=Q
			char fromFile = (char) ('a' + fromCoords[1]);
			notation.append(fromFile).append("x").append(to);
		} else {
			// Non-capture promotion: e8=Q
			notation.append(to);
		}

		notation.append("=").append(promotionPiece);
		return notation.toString();
	}

	/**
	 * Add clearBoard method for testing
	 */
	public void clearBoard() {
		for (int row = 0; row < 8; row++) {
			for (int col = 0; col < 8; col++) {
				board[row][col] = null;
			}
		}
	}

	/**
	 * Convert the current board position to Forsyth–Edwards Notation (FEN). This
	 * method generates the piece placement portion of the FEN string. Full FEN
	 * requires additional game state information that this method doesn't provide.
	 * 
	 * @return The piece placement portion of FEN string representing the current
	 *         board position.
	 */
	public String toFEN() {
		StringBuilder fen = new StringBuilder();

		// Traverse board from 8th rank to 1st rank
		for (int row = 7; row >= 0; row--) {
			int emptySquares = 0;

			// Process each square in the rank from a-file to h-file
			for (int col = 0; col < 8; col++) {
				IPiece piece = board[row][col];

				if (piece == null) {
					emptySquares++;
				} else {
					// If there were empty squares before this piece, add the count
					if (emptySquares > 0) {
						fen.append(emptySquares);
						emptySquares = 0;
					}

					// Map piece to FEN symbol
					char symbol = piece.getSymbol();
					symbol = piece.getSide() == Side.WHITE ? symbol : Character.toLowerCase(symbol);
					fen.append(symbol);
				}
			}

			// Add any remaining empty squares at the end of the rank
			if (emptySquares > 0) {
				fen.append(emptySquares);
			}

			// Add rank separator (/) except for the last rank
			if (row > 0) {
				fen.append('/');
			}
		}

		return fen.toString();
	}

	/**
	 * Get captured pieces for each side
	 */
	public List<IPiece> getCapturedPieces(String color) {
		if (color.equalsIgnoreCase("White")) {
			return capturedWhitePieces;
		} else {
			return capturedBlackPieces;
		}
	}
}