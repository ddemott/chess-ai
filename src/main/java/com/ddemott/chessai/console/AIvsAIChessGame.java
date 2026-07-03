package com.ddemott.chessai.console;

import com.ddemott.chessai.engine.GameEngine;
import com.ddemott.chessai.Move;
import com.ddemott.chessai.ai.AIDifficulty;
import java.util.Scanner;

/**
 * AI vs AI chess game mode where two AI players of different difficulties
 * compete
 */
public class AIvsAIChessGame {

	private GameEngine gameEngine;
	private AIDifficulty whiteDifficulty;
	private AIDifficulty blackDifficulty;
	private EnhancedConsoleDisplay display;
	private int moveDelay; // Delay between moves in milliseconds
	private boolean pauseAfterEachMove;

	public AIvsAIChessGame(AIDifficulty whiteDifficulty, AIDifficulty blackDifficulty) {
		this.whiteDifficulty = whiteDifficulty;
		this.blackDifficulty = blackDifficulty;
		this.gameEngine = new GameEngine(whiteDifficulty);
		this.display = new EnhancedConsoleDisplay(gameEngine.getGameState());
		this.moveDelay = 1000; // 1 second default
		this.pauseAfterEachMove = false;
	}

	public static void main(String[] args) {
		System.out.println("=== AI vs AI Chess Game (Automated Depth 4) ===\n");

		try (Scanner scanner = new Scanner(System.in)) {
			// Automate selection: Both ADVANCED (Depth 4)
			AIDifficulty white = AIDifficulty.ADVANCED;
			AIDifficulty black = AIDifficulty.ADVANCED;

			AIvsAIChessGame game = new AIvsAIChessGame(white, black);

			// Set speed manually to avoid prompt
			game.moveDelay = 1000; // 1 second

			// We need to override playGame to not ask for speed, or just subclass/modify
			// it.
			// Since we can't easily override the private method call inside playGame
			// without changing playGame,
			// let's create a specific method or just modify playGame to check if speed is
			// already set?
			// Actually, let's just modify the playGame method to take an optional
			// 'automated' flag or similar.
			// Or better, just copy the logic here since we are changing the file.

			game.playAutomatedGame(scanner);
		}
	}

	public void playAutomatedGame(Scanner scanner) {
		System.out.println("=== Game Setup ===");
		System.out.println("White: " + whiteDifficulty);
		System.out.println("Black: " + blackDifficulty);
		System.out.println("Speed: Normal (1000ms)");
		System.out.println("\n=== Game Starting ===\n");

		// Initial board display
		display.displayBoard();

		int moveCount = 0;
		final int MAX_MOVES = 200; // Prevent infinite games

		while (moveCount < MAX_MOVES) {
			String currentPlayer = gameEngine.getCurrentTurn();

			// Set appropriate AI difficulty for current player
			if (currentPlayer.equals("White")) {
				gameEngine.setAIDifficulty(whiteDifficulty);
			} else {
				gameEngine.setAIDifficulty(blackDifficulty);
			}

			System.out.println(
					"🤖 " + currentPlayer + " (" + gameEngine.getAIDifficulty().getDisplayName() + ") is thinking...");

			// Make AI move
			int initialHistorySize = gameEngine.getMoveHistory().getAllMoves().size();
			gameEngine.makeAIMove();
			int newHistorySize = gameEngine.getMoveHistory().getAllMoves().size();

			if (newHistorySize > initialHistorySize) {
				Move aiMove = gameEngine.getLastMove();
				// Add captured piece to display
				if (aiMove.getCapturedPiece() != null) {
					display.addCapturedPiece(aiMove.getCapturedPiece());
				}

				System.out.println("🤖 " + currentPlayer + " played: " + aiMove.getAlgebraicNotation() + " ("
						+ aiMove.getFrom() + " → " + aiMove.getTo() + ")");

				moveCount++;

				// Check for game end conditions
				String nextPlayer = gameEngine.getCurrentTurn();
				if (gameEngine.getGameState().getBoard().isKingInCheck(nextPlayer)) {
					if (gameEngine.getGameState().getBoard().isCheckmate(nextPlayer)) {
						display.displayBoard();
						System.out.println("🏆 CHECKMATE! " + currentPlayer + " ("
								+ (currentPlayer.equals("White")
										? whiteDifficulty.getDisplayName()
										: blackDifficulty.getDisplayName())
								+ ") wins!");
						break;
					} else {
						System.out.println("⚠️  CHECK! " + nextPlayer + " king is under attack!");
					}
				} else if (gameEngine.getGameState().getBoard().isStalemate(nextPlayer)) {
					display.displayBoard();
					System.out.println("🤝 STALEMATE! The game is a draw.");
					break;
				}

				// Display updated board
				display.displayBoard();

			} else {
				// AI could not make a move - check why
				if (gameEngine.getGameState().getBoard().isCheckmate(currentPlayer)) {
					display.displayBoard();
					System.out
							.println("🏆 CHECKMATE! " + (currentPlayer.equals("White") ? "Black" : "White") + " wins!");
				} else if (gameEngine.getGameState().getBoard().isStalemate(currentPlayer)) {
					display.displayBoard();
					System.out.println("🤝 STALEMATE! The game is a draw.");
				} else if (gameEngine.getGameState().isThreefoldRepetition()) {
					display.displayBoard();
					System.out.println("🤝 DRAW by Threefold Repetition!");
				} else if (gameEngine.getGameState().isFiftyMoveRule()) {
					display.displayBoard();
					System.out.println("🤝 DRAW by Fifty-Move Rule!");
				} else {
					System.out.println("❌ " + currentPlayer + " could not make a move (Resignation or Error)!");
				}
				break;
			}
		}
		if (moveCount >= MAX_MOVES) {
			System.out.println("🕐 Game ended due to move limit (" + MAX_MOVES + " moves)");
		}

		// Display final game statistics
		displayGameSummary();
	}

	private static AIDifficulty selectDifficulty(Scanner scanner, String playerColor) {
		System.out.println("Select difficulty for " + playerColor + " AI:");
		System.out.println(AIDifficulty.getAllDifficulties());
		while (true) {
			System.out.print("Enter choice (1-" + AIDifficulty.values().length + "): ");
			try {
				int choice = scanner.nextInt();
				if (choice >= 1 && choice <= AIDifficulty.values().length) {
					AIDifficulty selected = AIDifficulty.values()[choice - 1];
					System.out.println("Selected " + selected.getDisplayName() + " for " + playerColor + "\n");
					return selected;
				}
				System.out.println("Invalid choice. Please try again.");
			} catch (Exception e) {
				System.out.println("Invalid input. Please enter a number.");
				scanner.nextLine(); // Clear invalid input
			}
		}
	}

	public void playGame(Scanner scanner) {
		System.out.println("=== Game Setup ===");
		System.out.println("White: " + whiteDifficulty);
		System.out.println("Black: " + blackDifficulty);
		System.out.println();

		// Ask for game speed
		System.out.println("Game speed options:");
		System.out.println("1. Fast (0.5 seconds between moves)");
		System.out.println("2. Normal (1 second between moves)");
		System.out.println("3. Slow (2 seconds between moves)");
		System.out.println("4. Manual (pause after each move)");
		System.out.print("Choose speed (1-4): ");

		try {
			int speedChoice = scanner.nextInt();
			switch (speedChoice) {
				case 1 :
					moveDelay = 500;
					break;
				case 2 :
					moveDelay = 1000;
					break;
				case 3 :
					moveDelay = 2000;
					break;
				case 4 :
					moveDelay = 0;
					pauseAfterEachMove = true;
					System.out.println("Press Enter after each move to continue...");
					break;
				default :
					moveDelay = 1000;
					System.out.println("Invalid choice, using normal speed.");
			}
		} catch (Exception e) {
			moveDelay = 1000;
			System.out.println("Invalid input, using normal speed.");
		}

		scanner.nextLine(); // Clear buffer

		System.out.println("\\n=== Game Starting ===\\n");

		// Initial board display
		display.displayBoard();

		int moveCount = 0;
		final int MAX_MOVES = 200; // Prevent infinite games

		while (moveCount < MAX_MOVES) {
			String currentPlayer = gameEngine.getCurrentTurn();

			// Set appropriate AI difficulty for current player
			if (currentPlayer.equals("White")) {
				gameEngine.setAIDifficulty(whiteDifficulty);
			} else {
				gameEngine.setAIDifficulty(blackDifficulty);
			}

			System.out.println(
					"🤖 " + currentPlayer + " (" + gameEngine.getAIDifficulty().getDisplayName() + ") is thinking...");

			// ...existing code...

			// Make AI move
			int initialHistorySize = gameEngine.getMoveHistory().getAllMoves().size();
			gameEngine.makeAIMove();
			int newHistorySize = gameEngine.getMoveHistory().getAllMoves().size();

			if (newHistorySize > initialHistorySize) {
				Move aiMove = gameEngine.getLastMove();
				// Add captured piece to display
				if (aiMove.getCapturedPiece() != null) {
					display.addCapturedPiece(aiMove.getCapturedPiece());
				}

				System.out.println("🤖 " + currentPlayer + " played: " + aiMove.getAlgebraicNotation() + " ("
						+ aiMove.getFrom() + " → " + aiMove.getTo() + ")");

				moveCount++;

				// Check for game end conditions
				String nextPlayer = gameEngine.getCurrentTurn();
				if (gameEngine.getGameState().getBoard().isKingInCheck(nextPlayer)) {
					if (gameEngine.getGameState().getBoard().isCheckmate(nextPlayer)) {
						display.displayBoard();
						System.out.println("🏆 CHECKMATE! " + currentPlayer + " ("
								+ (currentPlayer.equals("White")
										? whiteDifficulty.getDisplayName()
										: blackDifficulty.getDisplayName())
								+ ") wins!");
						break;
					} else {
						System.out.println("⚠️  CHECK! " + nextPlayer + " king is under attack!");
					}
				} else if (gameEngine.getGameState().getBoard().isStalemate(nextPlayer)) {
					display.displayBoard();
					System.out.println("🤝 STALEMATE! The game is a draw.");
					break;
				}

				// Display updated board
				display.displayBoard();

				// Verify Game State Integrity
				gameEngine.verifyGameStateIntegrity();

				// Handle timing
				if (moveDelay > 0) {
					System.out.print("Press Enter to continue...");
					scanner.nextLine();
				} else if (moveDelay > 0) {
					try {
						Thread.sleep(moveDelay);
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
						break;
					}
				}

			} else {
				System.out.println("❌ " + currentPlayer + " could not make a move (Stalemate or Resignation)!");
				break;
			}
		}

		if (moveCount >= MAX_MOVES) {
			System.out.println("🕐 Game ended due to move limit (" + MAX_MOVES + " moves)");
		}

		// Display final game statistics
		displayGameSummary();

		// Offer to save the game
		System.out.print("\\nSave game to PGN file? (y/n): ");
		String saveChoice = "";
		if (scanner.hasNextLine()) {
			saveChoice = scanner.nextLine().trim().toLowerCase();
		}
		if (saveChoice.equals("y") || saveChoice.equals("yes")) {
			System.out.print("Enter filename: ");
			String filename = scanner.nextLine().trim();
			if (!filename.endsWith(".pgn")) {
				filename += ".pgn";
			}

			String whitePlayerName = "AI_" + whiteDifficulty.getDisplayName();
			String blackPlayerName = "AI_" + blackDifficulty.getDisplayName();
			String result = "*"; // Unknown result for now

			boolean saved = gameEngine.saveGameToPGNFile(filename, whitePlayerName, blackPlayerName, result);
			if (saved) {
				System.out.println("Game saved to " + filename);
			} else {
				System.out.println("Failed to save game.");
			}
		}
	}

	private void displayGameSummary() {
		System.out.println("\\n=== Game Summary ===");
		System.out.println("White: " + whiteDifficulty);
		System.out.println("Black: " + blackDifficulty);
		System.out.println("Total moves: " + gameEngine.getMoveHistory().getAllMoves().size());
		System.out.println("\\nMove history:");
		System.out.println(gameEngine.getMoveListDisplay());
	}

	/**
	 * Run a quick demo game
	 */
	public static void runDemo() {
		System.out.println("=== AI vs AI Demo Game ===");
		System.out.println("Running Beginner vs Advanced...");

		AIvsAIChessGame demo = new AIvsAIChessGame(AIDifficulty.BEGINNER, AIDifficulty.ADVANCED);
		demo.moveDelay = 800;
		try (Scanner scanner = new Scanner(System.in)) {
			demo.playGame(scanner);
		}
	}
}
