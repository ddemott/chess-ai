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
            // TODO: Add command handling for help, history, undo, redo, save, export, load, difficulty, captured, suggest, hint
            String[] positions = input.split(" ");
            if (positions.length != 2) {
                System.out.println("❌ Invalid input format. Please enter your move as 'e2 e4' or type 'help' for commands.");
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
            var validation = MoveValidator.validateMove(from, to, gameEngine.getCurrentTurn(), gameEngine.getGameState().getBoard());
            if (!validation.isValid()) {
                display.displayInvalidMoveError(from, to, validation.getError().getMessage());
                List<String> suggestions = MoveValidator.generateMoveSuggestions(
                    from, gameEngine.getGameState().getBoard(), gameEngine.getCurrentTurn());
                if (!suggestions.isEmpty()) {
                    System.out.println("💡 Valid moves for this piece:");
                    for (String suggestion : suggestions) {
                        System.out.println("  • " + suggestion);
                    }
                    System.out.println();
                }
                continue;
            }
            // Use MoveHistory.addMove to create a move object for preview (not actually add to history yet)
            IPiece movingPiece = gameEngine.getGameState().getBoard().getPieceAt(from);
            IPiece capturedPiece = gameEngine.getGameState().getBoard().getPieceAt(to);
            int moveNumber = gameEngine.getGameState().getMoveHistory().getMoves().size() / 2 + 1;
            String playerColor = gameEngine.getCurrentTurn();
            Move move = new Move(from, to, movingPiece, capturedPiece, "", moveNumber, playerColor, false, false, false, false, promotionPiece);
            return move;
        }
    }
}
