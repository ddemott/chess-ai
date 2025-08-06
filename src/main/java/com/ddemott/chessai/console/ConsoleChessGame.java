package com.ddemott.chessai.console;

import com.ddemott.chessai.engine.GameEngine;
import com.ddemott.chessai.Move;
import com.ddemott.chessai.console.EnhancedConsoleDisplay;
import com.ddemott.chessai.console.MoveValidator;
import com.ddemott.chessai.console.MoveValidator.MoveValidationResult;

import java.util.Scanner;
import java.util.List;

public class ConsoleChessGame {

    public static void main(String[] args) {
        GameEngine gameEngine = new GameEngine(4); // Set the depth as needed
        EnhancedConsoleDisplay display = new EnhancedConsoleDisplay(gameEngine.getGameState());

        Scanner scanner = new Scanner(System.in);
        
        // Display initial instructions
        printGameInstructions();

        while (true) {
            // Use enhanced display
            display.displayBoard();
            
            System.out.print("Enter your move or command: ");
            
            // Check if scanner has input available
            if (!scanner.hasNextLine()) {
                System.out.println("\nInput stream ended. Exiting game.");
                break;
            }
            
            String input = scanner.nextLine().trim();

            // Handle various commands
            if (input.equalsIgnoreCase("exit") || input.equalsIgnoreCase("quit")) {
                System.out.println("Thanks for playing!");
                break;
            } else if (input.equalsIgnoreCase("help")) {
                printGameInstructions();
                continue;
            } else if (input.equalsIgnoreCase("history") || input.equalsIgnoreCase("moves")) {
                System.out.println(gameEngine.getMoveListDisplay());
                continue;
            } else if (input.equalsIgnoreCase("undo")) {
                handleUndo(gameEngine);
                continue;
            } else if (input.equalsIgnoreCase("redo")) {
                handleRedo(gameEngine);
                continue;
            } else if (input.toLowerCase().startsWith("save ")) {
                handleSaveGame(gameEngine, input);
                continue;
            } else if (input.toLowerCase().startsWith("export")) {
                handleExportGame(gameEngine);
                continue;
            } else if (input.equalsIgnoreCase("suggest") || input.equalsIgnoreCase("hint")) {
                display.displayMoveSuggestions(gameEngine.getCurrentTurn());
                continue;
            }

            // Handle move input
            String[] positions = input.split(" ");
            if (positions.length != 2) {
                System.out.println("❌ Invalid input format. Please enter your move as 'e2 e4' or type 'help' for commands.");
                continue;
            }

            // Validate move with detailed feedback
            MoveValidationResult validation = MoveValidator.validateMove(
                positions[0], positions[1], gameEngine.getCurrentTurn(), gameEngine.getGameState().getBoard());
            
            if (!validation.isValid()) {
                display.displayInvalidMoveError(positions[0], positions[1], validation.getError().getMessage());
                
                // Show suggestions for the piece they tried to move
                List<String> suggestions = MoveValidator.generateMoveSuggestions(
                    positions[0], gameEngine.getGameState().getBoard(), gameEngine.getCurrentTurn());
                if (!suggestions.isEmpty()) {
                    System.out.println("💡 Valid moves for this piece:");
                    for (String suggestion : suggestions) {
                        System.out.println("  • " + suggestion);
                    }
                    System.out.println();
                }
                continue;
            }

            // Track captured piece for display
            Move lastMoveBeforeNew = gameEngine.getLastMove();
            
            boolean moveSuccessful = gameEngine.movePiece(positions[0], positions[1]);
            if (!moveSuccessful) {
                display.displayInvalidMoveError(positions[0], positions[1], "Move execution failed");
                continue;
            } else {
                Move move = gameEngine.getLastMove();
                
                // Add captured piece to display
                if (move.getCapturedPiece() != null) {
                    display.addCapturedPiece(move.getCapturedPiece());
                }
                
                System.out.println("✅ Move: " + move.getAlgebraicNotation() + 
                                 " (" + positions[0] + " → " + positions[1] + ")");
                
                // Check for special game states
                String currentPlayer = gameEngine.getCurrentTurn();
                if (gameEngine.getGameState().getBoard().isKingInCheck(currentPlayer)) {
                    if (gameEngine.getGameState().getBoard().isCheckmate(currentPlayer)) {
                        display.displayBoard();
                        System.out.println("🏆 CHECKMATE! " + move.getPlayerColor() + " wins!");
                        break;
                    } else {
                        System.out.println("⚠️  CHECK! " + currentPlayer + " king is under attack!");
                    }
                } else if (gameEngine.getGameState().getBoard().isStalemate(currentPlayer)) {
                    display.displayBoard();
                    System.out.println("🤝 STALEMATE! The game is a draw.");
                    break;
                }
            }

            // AI's turn
            if (gameEngine.getCurrentTurn().equals("Black")) {
                System.out.println("🤖 AI is thinking...");
                
                // Track AI's captured piece
                Move lastMoveBeforeAI = gameEngine.getLastMove();
                
                gameEngine.makeAIMove();
                Move aiMove = gameEngine.getLastMove();
                if (aiMove != null) {
                    // Add AI's captured piece to display
                    if (aiMove.getCapturedPiece() != null) {
                        display.addCapturedPiece(aiMove.getCapturedPiece());
                    }
                    
                    System.out.println("🤖 AI played: " + aiMove.getAlgebraicNotation() + 
                                     " (" + aiMove.getFrom() + " → " + aiMove.getTo() + ")");
                    
                    // Check for AI causing check/checkmate
                    String humanPlayer = "White";
                    if (gameEngine.getGameState().getBoard().isKingInCheck(humanPlayer)) {
                        if (gameEngine.getGameState().getBoard().isCheckmate(humanPlayer)) {
                            display.displayBoard();
                            System.out.println("💀 CHECKMATE! AI wins!");
                            break;
                        } else {
                            System.out.println("⚠️  CHECK! Your king is under attack!");
                        }
                    } else if (gameEngine.getGameState().getBoard().isStalemate(humanPlayer)) {
                        display.displayBoard();
                        System.out.println("🤝 STALEMATE! The game is a draw.");
                        break;
                    }
                }
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
        System.out.println("  - 'export': Export game in PGN format");
        System.out.println("  - 'exit' or 'quit': End the game");
        System.out.println("• Features:");
        System.out.println("  - Kings in check are highlighted in red");
        System.out.println("  - Captured pieces are shown above the board");
        System.out.println("  - Detailed error messages help you learn");
        System.out.println("==================================\n");
    }

    private static void handleUndo(GameEngine gameEngine) {
        if (!gameEngine.canUndo()) {
            System.out.println("No moves to undo.");
            return;
        }
        
        // Undo AI move (if it was AI's turn)
        if (gameEngine.getCurrentTurn().equals("White")) {
            if (gameEngine.undoLastMove()) {
                System.out.println("Undid AI move.");
            }
        }
        
        // Undo player move
        if (gameEngine.undoLastMove()) {
            System.out.println("Undid your move.");
        } else {
            System.out.println("Cannot undo move.");
        }
    }

    private static void handleRedo(GameEngine gameEngine) {
        if (!gameEngine.canRedo()) {
            System.out.println("No moves to redo.");
            return;
        }
        
        // Redo player move
        if (gameEngine.redoLastMove()) {
            System.out.println("Redid your move.");
        }
        
        // Redo AI move if available
        if (gameEngine.canRedo() && gameEngine.getCurrentTurn().equals("Black")) {
            if (gameEngine.redoLastMove()) {
                System.out.println("Redid AI move.");
            }
        }
    }

    private static void handleSaveGame(GameEngine gameEngine, String input) {
        String[] parts = input.split(" ", 2);
        if (parts.length < 2) {
            System.out.println("Please specify a filename: save <filename.pgn>");
            return;
        }
        
        String filename = parts[1];
        if (!filename.endsWith(".pgn")) {
            filename += ".pgn";
        }
        
        boolean saved = gameEngine.saveGameToPGNFile(filename, "Human", "ChessAI", "*");
        if (saved) {
            System.out.println("Game saved to " + filename);
        } else {
            System.out.println("Failed to save game.");
        }
    }

    private static void handleExportGame(GameEngine gameEngine) {
        String pgn = gameEngine.exportGameToPGN("Human", "ChessAI", "*");
        System.out.println("\n=== Game in PGN Format ===");
        System.out.println(pgn);
        System.out.println("==========================\n");
    }
}
