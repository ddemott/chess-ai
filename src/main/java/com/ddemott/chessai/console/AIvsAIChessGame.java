package com.ddemott.chessai.console;

import com.ddemott.chessai.engine.GameEngine;
import com.ddemott.chessai.Move;
import com.ddemott.chessai.ai.AIDifficulty;
import java.util.Scanner;

/**
 * AI vs AI chess game mode where two AI players of different difficulties compete
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
        System.out.println("=== AI vs AI Chess Game ===\n");
        
        AIDifficulty white = selectDifficulty("White");
        AIDifficulty black = selectDifficulty("Black");
        
        AIvsAIChessGame game = new AIvsAIChessGame(white, black);
        game.playGame();
    }
    
    private static AIDifficulty selectDifficulty(String playerColor) {
        Scanner scanner = new Scanner(System.in);
        
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
    
    public void playGame() {
        Scanner scanner = new Scanner(System.in);
        
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
                case 1: moveDelay = 500; break;
                case 2: moveDelay = 1000; break;
                case 3: moveDelay = 2000; break;
                case 4: 
                    moveDelay = 0; 
                    pauseAfterEachMove = true;
                    System.out.println("Press Enter after each move to continue...");
                    break;
                default: 
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
            
            System.out.println("🤖 " + currentPlayer + " (" + gameEngine.getAIDifficulty().getDisplayName() + ") is thinking...");
            
            // Track move for display
            Move lastMoveBeforeAI = gameEngine.getLastMove();
            
            // Make AI move
            gameEngine.makeAIMove();
            Move aiMove = gameEngine.getLastMove();
            
            if (aiMove != null) {
                // Add captured piece to display
                if (aiMove.getCapturedPiece() != null) {
                    display.addCapturedPiece(aiMove.getCapturedPiece());
                }
                
                System.out.println("🤖 " + currentPlayer + " played: " + aiMove.getAlgebraicNotation() + 
                                 " (" + aiMove.getFrom() + " → " + aiMove.getTo() + ")");
                
                moveCount++;
                
                // Check for game end conditions
                String nextPlayer = gameEngine.getCurrentTurn();
                if (gameEngine.getGameState().getBoard().isKingInCheck(nextPlayer)) {
                    if (gameEngine.getGameState().getBoard().isCheckmate(nextPlayer)) {
                        display.displayBoard();
                        System.out.println("🏆 CHECKMATE! " + currentPlayer + " (" + 
                            (currentPlayer.equals("White") ? whiteDifficulty.getDisplayName() : blackDifficulty.getDisplayName()) + 
                            ") wins!");
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
                
                // Handle timing
                if (pauseAfterEachMove) {
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
                System.out.println("❌ " + currentPlayer + " could not make a move!");
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
        String saveChoice = scanner.nextLine().trim().toLowerCase();
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
        
        scanner.close();
    }
    
    private void displayGameSummary() {
        System.out.println("\\n=== Game Summary ===");
        System.out.println("White: " + whiteDifficulty);
        System.out.println("Black: " + blackDifficulty);
        System.out.println("Total moves: " + gameEngine.getMoveHistory().getMoves().size());
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
        demo.playGame();
    }
}
