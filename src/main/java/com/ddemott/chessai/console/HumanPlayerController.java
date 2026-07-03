package com.ddemott.chessai.console;

import com.ddemott.chessai.pieces.IPiece;

import com.ddemott.chessai.engine.GameEngine;
import com.ddemott.chessai.Move;
import java.util.Scanner;
import java.util.List;

/**
 * Human player controller for console input
 */
public class HumanPlayerController implements PlayerController {
	private final Scanner scanner;
	private final EnhancedConsoleDisplay display;

	public HumanPlayerController(Scanner scanner, EnhancedConsoleDisplay display) {
		this.scanner = scanner;
		this.display = display;
	}

	@Override
	public Move selectMove(GameEngine gameEngine) {
		while (true) {
			display.displayBoard();
			System.out.print("Enter your move or command: ");
			if (!scanner.hasNextLine()) {
				System.out.println("\nInput stream ended. Exiting game.");
				return null;
			}
			String input = scanner.nextLine().trim();
			if (input.equalsIgnoreCase("exit") || input.equalsIgnoreCase("quit")) {
				return null;
			}

			// Handle commands
			String lowerInput = input.toLowerCase();
			String[] inputParts = input.split("\\s+", 2);
			String command = inputParts[0].toLowerCase();

			if (command.equals("help")) {
				ConsoleChessGame.printGameInstructions();
				continue;
			}
			if (command.equals("history") || command.equals("moves")) {
				System.out.println(gameEngine.getMoveListDisplay());
				continue;
			}
			if (command.equals("undo")) {
				if (!gameEngine.canUndo()) {
					System.out.println("Nothing to undo.");
					continue;
				}
				// Undo AI's move, then player's move
				gameEngine.undoLastMove();
				if (gameEngine.canUndo()) {
					gameEngine.undoLastMove();
				}
				System.out.println("Undo successful.");
				continue;
			}
			if (command.equals("redo")) {
				if (!gameEngine.canRedo()) {
					System.out.println("Nothing to redo.");
					continue;
				}
				gameEngine.redoLastMove();
				if (gameEngine.canRedo()) {
					gameEngine.redoLastMove();
				}
				System.out.println("Redo successful.");
				continue;
			}
			if (command.equals("save")) {
				String filename = inputParts.length > 1 ? inputParts[1] : "game.pgn";
				boolean saved = gameEngine.saveGameToPGNFile(filename, "Player", "ChessAI", "*");
				System.out.println(saved ? "Game saved to " + filename : "Failed to save game.");
				continue;
			}
			if (command.equals("load")) {
				String filename = inputParts.length > 1 ? inputParts[1] : "game.pgn";
				boolean loaded = gameEngine.loadGameFromPGNFile(filename);
				System.out.println(loaded ? "Game loaded from " + filename : "Failed to load game.");
				continue;
			}
			if (command.equals("export")) {
				System.out.println(gameEngine.exportGameToPGN("Player", "ChessAI", "*"));
				continue;
			}
			if (command.equals("difficulty")) {
				System.out.println("Current difficulty: " + gameEngine.getAIDifficulty());
				System.out.println("Available: BEGINNER, EASY, INTERMEDIATE, ADVANCED, EXPERT");
				System.out.print("Enter new difficulty: ");
				if (scanner.hasNextLine()) {
					String diffInput = scanner.nextLine().trim().toUpperCase();
					try {
						com.ddemott.chessai.ai.AIDifficulty diff = com.ddemott.chessai.ai.AIDifficulty
								.valueOf(diffInput);
						gameEngine.setAIDifficulty(diff);
						System.out.println("Difficulty set to " + diff);
					} catch (IllegalArgumentException e) {
						System.out.println("Invalid difficulty level.");
					}
				}
				continue;
			}
			if (command.equals("suggest") || command.equals("hint")) {
				com.ddemott.chessai.ai.MoveResult suggestion = gameEngine.getBestMoveWithScore();
				if (suggestion != null && suggestion.move() != null) {
					System.out.println("Suggested move: " + suggestion.move() + " (score: " + suggestion.value() + ")");
				} else {
					System.out.println("No suggestion available.");
				}
				continue;
			}
			if (command.equals("captured")) {
				List<IPiece> capturedWhite = gameEngine.getGameState().getBoard().getCapturedPieces("White");
				List<IPiece> capturedBlack = gameEngine.getGameState().getBoard().getCapturedPieces("Black");
				System.out.println("Captured white pieces: " + formatCapturedPieces(capturedWhite));
				System.out.println("Captured black pieces: " + formatCapturedPieces(capturedBlack));
				continue;
			}

			// Parse as move input
			String[] positions = input.split(" ");
			if (positions.length != 2) {
				System.out.println(
						"Invalid input format. Please enter your move as 'e2 e4' or type 'help' for commands.");
				continue;
			}
			String from = positions[0];
			String to = positions[1];
			String promotionPiece = null;
			if (ConsoleChessGame.isPawnPromotionMove(gameEngine, from, to)) {
				promotionPiece = ConsoleChessGame.promptForPromotionPiece(scanner);
				if (promotionPiece == null) {
					continue;
				}
			}
			var validation = MoveValidator.validateMove(from, to, gameEngine.getCurrentTurn(),
					gameEngine.getGameState().getBoard());
			if (!validation.isValid()) {
				display.displayInvalidMoveError(from, to, validation.getError().getMessage());
				List<String> suggestions = MoveValidator.generateMoveSuggestions(from,
						gameEngine.getGameState().getBoard(), gameEngine.getCurrentTurn());
				if (!suggestions.isEmpty()) {
					System.out.println("Valid moves for this piece:");
					for (String suggestion : suggestions) {
						System.out.println("  - " + suggestion);
					}
					System.out.println();
				}
				continue;
			}
			IPiece movingPiece = gameEngine.getGameState().getBoard().getPieceAt(from);
			IPiece capturedPiece = gameEngine.getGameState().getBoard().getPieceAt(to);
			int moveNumber = gameEngine.getGameState().getMoveHistory().getMoves().size() / 2 + 1;
			String playerColor = gameEngine.getCurrentTurn();
			Move move = new Move(from, to, movingPiece, capturedPiece, "", moveNumber, playerColor, false, false, false,
					false, promotionPiece);
			return move;
		}
	}

	private String formatCapturedPieces(List<IPiece> pieces) {
		if (pieces.isEmpty()) {
			return "none";
		}
		StringBuilder sb = new StringBuilder();
		for (IPiece piece : pieces) {
			if (sb.length() > 0) {
				sb.append(", ");
			}
			sb.append(piece.getClass().getSimpleName());
		}
		return sb.toString();
	}
}
