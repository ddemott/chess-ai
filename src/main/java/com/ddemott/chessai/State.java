package com.ddemott.chessai;

import java.util.List;
import com.ddemott.chessai.pieces.*;

import com.ddemott.chessai.ai.AIStrategy;
import com.ddemott.chessai.pieces.IPiece;

public class State {
	public AIStrategy getAIStrategy() {
		return aiStrategy;
	}
	private Board board;
	private Side currentTurn;
	private AIStrategy aiStrategy;
	private MoveHistory moveHistory;

	public State() {
		this.board = new Board();
		this.currentTurn = Side.WHITE; // Starting with white's turn
		this.moveHistory = new MoveHistory();
	}

	public Board getBoard() {
		return board;
	}

	public String getCurrentTurn() {
		return currentTurn.toString();
	}

	public Side getCurrentTurnSide() {
		return currentTurn;
	}

	public void setCurrentTurn(String currentTurn) {
		this.currentTurn = currentTurn.equalsIgnoreCase("White") ? Side.WHITE : Side.BLACK;
	}

	public void setCurrentTurn(Side side) {
		this.currentTurn = side;
	}

	public void setAIStrategy(AIStrategy aiStrategy) {
		this.aiStrategy = aiStrategy;
	}

	public boolean movePiece(String from, String to) {
		return movePiece(from, to, null);
	}

	public boolean movePiece(String from, String to, String promotionPiece) {
		// Basic validation
		IPiece piece = board.getPieceAt(from);
		if (piece == null || piece.getSide() != currentTurn) {
			return false;
		}

		// Capture state before move for undo support
		String enPassantTargetBefore = board.getEnPassantTarget();
		boolean wasFirstMove = !piece.hasMoved();

		// Detect special move types before executing
		boolean isCastle = piece instanceof King && Math.abs(from.charAt(0) - to.charAt(0)) == 2;
		boolean isEnPassant = piece instanceof Pawn && to.equals(board.getEnPassantTarget());

		// Check for captured piece before move
		IPiece capturedPiece = board.getPieceAt(to);
		// For en passant, the captured pawn is on a different square
		if (isEnPassant) {
			int[] toCoords = board.convertPositionToCoordinates(to);
			int capturedRow = piece.getSide() == Side.WHITE ? toCoords[0] - 1 : toCoords[0] + 1;
			String capturedPos = board.convertCoordinatesToPosition(capturedRow, toCoords[1]);
			capturedPiece = board.getPieceAt(capturedPos);
		}

		// Execute move using Board class (which handles validation and pin checking)
		boolean moveSuccessful;
		if (promotionPiece != null) {
			moveSuccessful = board.movePiece(from, to, promotionPiece);
		} else {
			moveSuccessful = board.movePiece(from, to);
		}

		// Update game state if move was successful
		if (moveSuccessful) {
			// Record position
			String positionFEN = board.toFEN().split(" ")[0];
			moveHistory.addPosition(positionFEN);

			// Update half-move clock
			boolean isPawnMove = piece instanceof Pawn;
			moveHistory.updateHalfmoveClock(isPawnMove, capturedPiece != null);

			// Detect check/checkmate after the move
			Side opponentSide = currentTurn.flip();
			boolean isCheck = board.isKingInCheck(opponentSide);
			boolean isCheckmate = isCheck && board.isCheckmate(opponentSide);

			// Record move in history with full state
			moveHistory.addMove(from, to, piece, capturedPiece, board, currentTurn.toString(),
			        promotionPiece, isCheck, isCheckmate, isCastle, isEnPassant,
			        enPassantTargetBefore, wasFirstMove);

			toggleTurn();
		}

		return moveSuccessful;
	}

	public String getBestMove() {
		return aiStrategy.calculateBestMove(this, currentTurn.toString());
	}

	private void toggleTurn() {
		currentTurn = currentTurn.flip();
	}

	public List<String> getAllPossibleMoves(String color) {
		return board.getAllPossibleMoves(color);
	}

	public List<String> getAllPossibleMoves(Side side) {
		return board.getAllPossibleMoves(side);
	}

	@Override
	public State clone() {
		State newState = new State();
		newState.board = this.board.clone();
		newState.currentTurn = this.currentTurn;
		newState.setAIStrategy(this.aiStrategy);
		newState.moveHistory = this.moveHistory.copy();
		return newState;
	}

	// Getter for move history
	public MoveHistory getMoveHistory() {
		return moveHistory;
	}

	// Undo functionality
	public boolean undoLastMove() {
		if (!moveHistory.canUndo()) {
			return false;
		}

		Move lastMove = moveHistory.undoMove();
		if (lastMove == null) {
			return false;
		}

		// Handle promotion: replace the promoted piece with the original pawn
		if (lastMove.getPromotionPiece() != null) {
			IPiece pawn = lastMove.getMovingPiece().clonePiece();
			pawn.setPosition(lastMove.getFrom());
			if (lastMove.wasFirstMove()) {
				pawn.setHasMoved(false);
			}
			board.setPieceAt(lastMove.getFrom(), pawn);
			board.setPieceAt(lastMove.getTo(), null);
		} else {
			// Move piece back to original position
			IPiece piece = board.getPieceAt(lastMove.getTo());
			if (piece != null) {
				board.setPieceAt(lastMove.getFrom(), piece);
				piece.setPosition(lastMove.getFrom());
				board.setPieceAt(lastMove.getTo(), null);
				// Restore hasMoved flag
				if (lastMove.wasFirstMove()) {
					piece.setHasMoved(false);
				}
			}
		}

		// Handle castling: also move rook back
		if (lastMove.isCastle()) {
			int[] kingToCoords = board.convertPositionToCoordinates(lastMove.getTo());
			int[] kingFromCoords = board.convertPositionToCoordinates(lastMove.getFrom());
			boolean isKingside = kingToCoords[1] > kingFromCoords[1];
			String rookFrom, rookTo;
			if (isKingside) {
				rookTo = board.convertCoordinatesToPosition(kingFromCoords[0], 7); // h-file
				                                                                   // (original)
				rookFrom = board.convertCoordinatesToPosition(kingFromCoords[0], 5); // f-file
				                                                                     // (castled)
			} else {
				rookTo = board.convertCoordinatesToPosition(kingFromCoords[0], 0); // a-file
				                                                                   // (original)
				rookFrom = board.convertCoordinatesToPosition(kingFromCoords[0], 3); // d-file
				                                                                     // (castled)
			}
			IPiece rook = board.getPieceAt(rookFrom);
			if (rook != null) {
				board.setPieceAt(rookTo, rook);
				rook.setPosition(rookTo);
				board.setPieceAt(rookFrom, null);
				rook.setHasMoved(false);
			}
		}

		// Handle en passant: restore the captured pawn at its original position
		if (lastMove.isEnPassant()) {
			if (lastMove.getCapturedPiece() != null) {
				int[] toCoords = board.convertPositionToCoordinates(lastMove.getTo());
				Side movingSide = lastMove.getSide();
				int capturedRow = movingSide == Side.WHITE ? toCoords[0] - 1 : toCoords[0] + 1;
				String capturedPos = board.convertCoordinatesToPosition(capturedRow, toCoords[1]);
				board.setPieceAt(capturedPos, lastMove.getCapturedPiece());
				lastMove.getCapturedPiece().setPosition(capturedPos);
			}
		} else {
			// Restore captured piece for non-en-passant moves
			if (lastMove.getCapturedPiece() != null) {
				board.setPieceAt(lastMove.getTo(), lastMove.getCapturedPiece());
				lastMove.getCapturedPiece().setPosition(lastMove.getTo());
			}
		}

		// Restore en passant target
		board.setEnPassantTarget(lastMove.getEnPassantTargetBefore());

		// Switch back to the previous player
		toggleTurn();
		return true;
	}

	// Redo functionality
	public boolean redoLastMove() {
		if (!moveHistory.canRedo()) {
			return false;
		}

		Move moveToRedo = moveHistory.redoMove();
		if (moveToRedo == null) {
			return false;
		}

		// Re-execute the move using Board (handles castling, en passant, etc.)
		if (moveToRedo.getPromotionPiece() != null) {
			board.movePiece(moveToRedo.getFrom(), moveToRedo.getTo(),
			        moveToRedo.getPromotionPiece());
		} else {
			board.movePiece(moveToRedo.getFrom(), moveToRedo.getTo());
		}
		toggleTurn();
		return true;
	}

	/**
	 * Check if the specified color is in stalemate A stalemate occurs when the
	 * player to move is not in check but has no legal moves
	 */
	public boolean isStalemate(String color) {
		return isStalemate(color.equalsIgnoreCase("White") ? Side.WHITE : Side.BLACK);
	}

	public boolean isStalemate(Side side) {
		// Delegate to Board's isStalemate method which has the corrected logic
		return board.isStalemate(side);
	}

	/**
	 * Check if the current position has occurred three times This uses the move
	 * history to check for repetitions
	 */
	public boolean isThreefoldRepetition() {
		// Get all positions from move history
		List<String> positions = moveHistory.getPositionHistory();
		if (positions.size() < 3) { // Need at least 3 occurrences for threefold repetition
			return false;
		}

		// Get current position FEN (without move numbers and counters)
		String currentPosition = board.toFEN().split(" ")[0];

		// Count occurrences of current position
		int repetitions = 0;
		for (String position : positions) {
			if (position.split(" ")[0].equals(currentPosition)) {
				repetitions++;
			}
		}
		return repetitions >= 3;
	}

	/**
	 * Check if fifty moves have been made without a pawn move or capture
	 */
	public boolean isFiftyMoveRule() {
		return moveHistory.getHalfmoveClock() >= 100; // 50 moves = 100 half-moves
	}

	/**
	 * Checks if the game is over due to checkmate, stalemate, draws by repetition
	 * or fifty-move rule
	 *
	 * @return true if the game is over, false otherwise
	 */
	public boolean isGameOver() {
		// Check for checkmate - if the current player's king is in check and they have
		// no legal moves
		if (board.isKingInCheck(currentTurn) && isStalemate(currentTurn)) {
			return true;
		}

		// Check for stalemate - if the current player has no legal moves but is not in
		// check
		if (isStalemate(currentTurn) && !board.isKingInCheck(currentTurn)) {
			return true;
		}

		// Check for threefold repetition
		if (isThreefoldRepetition()) {
			return true;
		}

		// Check for fifty-move rule
		if (isFiftyMoveRule()) {
			return true;
		}

		return false;
	}
}
