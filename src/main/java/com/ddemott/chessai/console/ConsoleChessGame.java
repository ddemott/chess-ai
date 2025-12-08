package com.ddemott.chessai.console;

import com.ddemott.chessai.engine.GameEngine;
import com.ddemott.chessai.Move;
import com.ddemott.chessai.ai.AIDifficulty;

import java.util.Scanner;

public class ConsoleChessGame {

	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		printGameInstructions();
		System.out.println("Select game mode:");
		System.out.println("1. Human vs Human");
		System.out.println("2. Human vs AI");
		System.out.println("3. AI vs AI");
		System.out.print("Enter choice (1-3): ");
		int mode = 2;
		try {
			mode = Integer.parseInt(scanner.nextLine().trim());
		} catch (Exception e) {
			System.out.println("Invalid input, defaulting to Human vs AI.");
		}

		GameEngine gameEngine = new GameEngine(4);
		EnhancedConsoleDisplay display = new EnhancedConsoleDisplay(gameEngine.getGameState());
		PlayerController whitePlayer;
		PlayerController blackPlayer;
		String whiteLabel;
		String blackLabel;
		switch (mode) {
			case 1 :
				whitePlayer = new HumanPlayerController(scanner, display);
				blackPlayer = new HumanPlayerController(scanner, display);
				whiteLabel = "Human (White)";
				blackLabel = "Human (Black)";
				break;
			case 3 :
				whitePlayer = new AIPlayerController(AIDifficulty.INTERMEDIATE);
				blackPlayer = new AIPlayerController(AIDifficulty.ADVANCED);
				whiteLabel = "AI (White, Intermediate)";
				blackLabel = "AI (Black, Advanced)";
				break;
			case 2 :
			default :
				whitePlayer = new HumanPlayerController(scanner, display);
				blackPlayer = new AIPlayerController(AIDifficulty.ADVANCED);
				whiteLabel = "Human (White)";
				blackLabel = "AI (Black, Advanced)";
				break;
		}

		while (true) {
			String currentTurn = gameEngine.getCurrentTurn();
			PlayerController currentPlayer = currentTurn.equals("White") ? whitePlayer : blackPlayer;
			String playerLabel = currentTurn.equals("White") ? whiteLabel : blackLabel;
			Move move = currentPlayer.selectMove(gameEngine);
			if (move == null) {
				System.out.println("Game ended by player.");
				break;
			}
			boolean moveSuccessful;
			if (move.getPromotionPiece() != null) {
				moveSuccessful = gameEngine.movePiece(move.getFrom(), move.getTo(), move.getPromotionPiece());
			} else {
				moveSuccessful = gameEngine.movePiece(move.getFrom(), move.getTo());
			}
			if (!moveSuccessful) {
				System.out.println("❌ Move execution failed. Try again.");
				continue;
			}
			Move lastMove = gameEngine.getLastMove();
			StringBuilder moveMsg = new StringBuilder();
			moveMsg.append("[" + playerLabel + "] ");
			moveMsg.append(
					lastMove.getAlgebraicNotation() + " (" + lastMove.getFrom() + " → " + lastMove.getTo() + ")");
			moveMsg.append(" [Score: " + move.getScore() + "]");
			if (lastMove.getCapturedPiece() != null) {
				display.addCapturedPiece(lastMove.getCapturedPiece());
				moveMsg.append(" *ATTACK* [Captured: " + lastMove.getCapturedPiece().getClass().getSimpleName() + " - "
						+ lastMove.getCapturedPiece().getColor() + "]");
			}
			System.out.println(moveMsg.toString());
			// Check for game end conditions
			String nextPlayer = gameEngine.getCurrentTurn();
			if (gameEngine.getGameState().getBoard().isKingInCheck(nextPlayer)) {
				if (gameEngine.getGameState().getBoard().isCheckmate(nextPlayer)) {
					display.displayBoard();
					System.out.println("🏆 CHECKMATE! " + lastMove.getPlayerColor() + " wins!");
					break;
				} else {
					System.out.println("⚠️  CHECK! " + nextPlayer + " king is under attack!");
				}
			} else if (gameEngine.getGameState().getBoard().isStalemate(nextPlayer)) {
				display.displayBoard();
				System.out.println("🤝 STALEMATE! The game is a draw.");
				break;
			}
		}
		scanner.close();
	}

	private static void printGameInstructions() {
		System.out.println("\n=== ChessAI Game Instructions ===");
		System.out.println("• Enter moves: 'e2 e4' (from square to square)");
		System.out.println("• Commands:");
		System.out.println("  - 'help': Show this help");
		System.out.println("  - 'history' or 'moves': Show move history");
		System.out.println("  - 'suggest' or 'hint': Get move suggestions");
		System.out.println("  - 'undo': Undo last move (yours and AI's)");
		System.out.println("  - 'redo': Redo undone move");
		System.out.println("  - 'save <filename>': Save game to PGN file");
		System.out.println("  - 'load <filename>': Load game from PGN file");
		System.out.println("  - 'export': Export game in PGN format");
		System.out.println("  - 'difficulty': Change AI difficulty level");
		System.out.println("  - 'aivsai': Start AI vs AI game mode");
		System.out.println("  - 'exit' or 'quit': End the game");
		System.out.println("• Features:");
		System.out.println("  - Kings in check are highlighted in red");
		System.out.println("  - Captured pieces are shown above the board");
		System.out.println("  - Detailed error messages help you learn");
		System.out.println("==================================\n");
	}

	/**
	 * Check if a move is a pawn promotion
	 */
	public static boolean isPawnPromotionMove(GameEngine gameEngine, String from, String to) {
		var piece = gameEngine.getGameState().getBoard().getPieceAt(from);
		if (piece == null || !piece.getClass().getSimpleName().equals("Pawn")) {
			return false;
		}

		int[] toCoords = gameEngine.getGameState().getBoard().convertPositionToCoordinates(to);
		int destinationRank = toCoords[0];

		// White pawns promote on rank 8 (row 7), Black pawns promote on rank 1 (row 0)
		if (piece.getColor().equals("White") && destinationRank == 7) {
			return true;
		}
		if (piece.getColor().equals("Black") && destinationRank == 0) {
			return true;
		}

		return false;
	}

	/**
	 * Prompt user to select promotion piece
	 */
	public static String promptForPromotionPiece(Scanner scanner) {
		System.out.println("🎉 Pawn Promotion! Choose your piece:");
		System.out.println("  Q - Queen (most powerful)");
		System.out.println("  R - Rook");
		System.out.println("  B - Bishop");
		System.out.println("  N - Knight");
		System.out.print("Enter your choice (Q/R/B/N): ");

		if (!scanner.hasNextLine()) {
			return null;
		}

		String choice = scanner.nextLine().trim().toUpperCase();

		if (choice.equals("Q") || choice.equals("R") || choice.equals("B") || choice.equals("N")) {
			String pieceName = getPieceFullName(choice);
			System.out.println("✅ Promoting to " + pieceName + "!");
			return choice;
		} else {
			System.out.println("❌ Invalid choice. Please enter Q, R, B, or N.");
			return promptForPromotionPiece(scanner); // Recursive retry
		}
	}

	/**
	 * Get full piece name for display
	 */
	private static String getPieceFullName(String pieceCode) {
		switch (pieceCode) {
			case "Q" :
				return "Queen";
			case "R" :
				return "Rook";
			case "B" :
				return "Bishop";
			case "N" :
				return "Knight";
			default :
				return "Unknown";
		}
	}
}
