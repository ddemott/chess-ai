package com.ddemott.chessai.console;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.ddemott.chessai.Board;
import com.ddemott.chessai.State;
import com.ddemott.chessai.Move;
import com.ddemott.chessai.pieces.IPiece;
import com.ddemott.chessai.pieces.King;

/**
 * Enhanced console display with better visualization and status information
 */
public class EnhancedConsoleDisplay {
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_BOLD = "\u001B[1m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_BLUE = "\u001B[34m";
    private static final String ANSI_CYAN = "\u001B[36m";
    private static final String ANSI_WHITE = "\u001B[37m";
    private static final String ANSI_BG_YELLOW = "\u001B[43m";
    private static final String ANSI_BG_RED = "\u001B[41m";
    
    private final State gameState;
    private final List<IPiece> capturedWhitePieces;
    private final List<IPiece> capturedBlackPieces;
    private boolean useColors;

    public EnhancedConsoleDisplay(State gameState) {
        this.gameState = gameState;
        this.capturedWhitePieces = new ArrayList<>();
        this.capturedBlackPieces = new ArrayList<>();
        this.useColors = true; // Can be disabled for systems that don't support ANSI colors
    }

    /**
     * Displays the board with enhanced features
     */
    public void displayBoard() {
        displayBoard(null, null, null);
    }

    /**
     * Displays the board with highlighted squares and status information
     */
    public void displayBoard(String highlightFrom, String highlightTo, Set<String> possibleMoves) {
        Board board = gameState.getBoard();
        
        // Display captured pieces
        displayCapturedPieces();
        
        System.out.println();
        
        // Column headers
        System.out.print("   ");
        for (char col = 'a'; col <= 'h'; col++) {
            System.out.print(" " + col + " ");
        }
        System.out.println();
        
        // Board rows
        for (int row = 7; row >= 0; row--) {
            // Row number
            System.out.print(" " + (row + 1) + " ");
            
            for (int col = 0; col < 8; col++) {
                String position = "" + (char) ('a' + col) + (row + 1);
                IPiece piece = board.getPieceAt(position);
                
                String pieceDisplay = getPieceDisplay(piece);
                
                // Apply highlighting
                if (position.equals(highlightFrom)) {
                    pieceDisplay = applyHighlight(pieceDisplay, ANSI_BG_YELLOW);
                } else if (position.equals(highlightTo)) {
                    pieceDisplay = applyHighlight(pieceDisplay, ANSI_BG_YELLOW);
                } else if (possibleMoves != null && possibleMoves.contains(position)) {
                    pieceDisplay = applyHighlight(pieceDisplay, ANSI_GREEN);
                }
                
                // Check if king is in check
                if (piece instanceof King && isKingInCheck(piece.getColor())) {
                    pieceDisplay = applyHighlight(pieceDisplay, ANSI_BG_RED);
                }
                
                System.out.print(pieceDisplay);
            }
            
            // Row number (right side)
            System.out.println(" " + (row + 1));
        }
        
        // Column headers (bottom)
        System.out.print("   ");
        for (char col = 'a'; col <= 'h'; col++) {
            System.out.print(" " + col + " ");
        }
        System.out.println();
        
        // Display game status
        displayGameStatus();
        System.out.println();
    }

    /**
     * Display captured pieces for both sides
     */
    private void displayCapturedPieces() {
        System.out.println(colorText("Captured Pieces:", ANSI_BOLD));
        
        // White captured pieces
        System.out.print(colorText("White lost: ", ANSI_WHITE + ANSI_BOLD));
        if (capturedWhitePieces.isEmpty()) {
            System.out.print(colorText("none", ANSI_WHITE));
        } else {
            for (IPiece piece : capturedWhitePieces) {
                System.out.print(getPieceSymbol(piece) + " ");
            }
        }
        System.out.println();
        
        // Black captured pieces
        System.out.print(colorText("Black lost: ", ANSI_WHITE + ANSI_BOLD));
        if (capturedBlackPieces.isEmpty()) {
            System.out.print(colorText("none", ANSI_WHITE));
        } else {
            for (IPiece piece : capturedBlackPieces) {
                System.out.print(getPieceSymbol(piece) + " ");
            }
        }
        System.out.println();
    }

    /**
     * Display current game status
     */
    private void displayGameStatus() {
        String currentPlayer = gameState.getCurrentTurn();
        
        // Current turn
        String turnText = "Current turn: " + currentPlayer;
        System.out.println(colorText(turnText, ANSI_BOLD));
        
        // Check status
        if (isKingInCheck(currentPlayer)) {
            if (isCheckmate(currentPlayer)) {
                System.out.println(colorText("CHECKMATE! " + getOpponentColor(currentPlayer) + " wins!", 
                                            ANSI_RED + ANSI_BOLD));
            } else {
                System.out.println(colorText("CHECK! " + currentPlayer + " king is under attack!", 
                                            ANSI_RED + ANSI_BOLD));
            }
        } else if (isStalemate(currentPlayer)) {
            System.out.println(colorText("STALEMATE! The game is a draw.", ANSI_YELLOW + ANSI_BOLD));
        }
        
        // Last move display
        Move lastMove = gameState.getMoveHistory().getLastMove();
        if (lastMove != null) {
            String lastMoveText = "Last move: " + lastMove.getAlgebraicNotation() + 
                                " (" + lastMove.getFrom() + " → " + lastMove.getTo() + ")";
            System.out.println(colorText(lastMoveText, ANSI_CYAN));
        }
    }

    /**
     * Add a captured piece to the display
     */
    public void addCapturedPiece(IPiece piece) {
        if (piece.getColor().equals("White")) {
            capturedWhitePieces.add(piece);
        } else {
            capturedBlackPieces.add(piece);
        }
    }

    /**
     * Get piece display with appropriate coloring
     */
    private String getPieceDisplay(IPiece piece) {
        if (piece == null) {
            return " . ";
        }
        
        String symbol = String.valueOf(getPieceSymbol(piece));
        
        if (piece.getColor().equals("White")) {
            return colorText(" " + symbol + " ", ANSI_WHITE + ANSI_BOLD);
        } else {
            return colorText(" " + symbol + " ", ANSI_BLUE + ANSI_BOLD);
        }
    }

    /**
     * Get piece symbol character
     */
    private char getPieceSymbol(IPiece piece) {
        char symbol;
        switch (piece.getClass().getSimpleName()) {
            case "King": symbol = 'K'; break;
            case "Queen": symbol = 'Q'; break;
            case "Rook": symbol = 'R'; break;
            case "Bishop": symbol = 'B'; break;
            case "Knight": symbol = 'N'; break;
            case "Pawn": symbol = 'P'; break;
            default: symbol = '?'; break;
        }
        
        return piece.getColor().equals("White") ? symbol : Character.toLowerCase(symbol);
    }

    /**
     * Apply color/highlight to text
     */
    private String colorText(String text, String colorCode) {
        if (!useColors) {
            return text;
        }
        return colorCode + text + ANSI_RESET;
    }

    /**
     * Apply highlighting to a piece display
     */
    private String applyHighlight(String text, String highlightCode) {
        if (!useColors) {
            return text;
        }
        return highlightCode + text + ANSI_RESET;
    }

    /**
     * Check if a king is in check
     */
    private boolean isKingInCheck(String playerColor) {
        return gameState.getBoard().isKingInCheck(playerColor);
    }

    /**
     * Check if the current player is in checkmate
     */
    private boolean isCheckmate(String playerColor) {
        return gameState.getBoard().isCheckmate(playerColor);
    }

    /**
     * Check if the current player is in stalemate
     */
    private boolean isStalemate(String playerColor) {
        return gameState.getBoard().isStalemate(playerColor);
    }

    /**
     * Get the opponent's color
     */
    private String getOpponentColor(String playerColor) {
        return playerColor.equals("White") ? "Black" : "White";
    }

    /**
     * Generate move suggestions for beginners
     */
    public List<String> generateMoveSuggestions(String playerColor) {
        List<String> suggestions = new ArrayList<>();
        List<String> possibleMoves = gameState.getAllPossibleMoves(playerColor);
        
        // Take up to 3 random suggestions
        int count = Math.min(3, possibleMoves.size());
        for (int i = 0; i < count; i++) {
            suggestions.add(possibleMoves.get(i));
        }
        
        return suggestions;
    }

    /**
     * Display move suggestions
     */
    public void displayMoveSuggestions(String playerColor) {
        List<String> suggestions = generateMoveSuggestions(playerColor);
        
        if (!suggestions.isEmpty()) {
            System.out.println(colorText("💡 Move suggestions:", ANSI_YELLOW + ANSI_BOLD));
            for (String move : suggestions) {
                System.out.println("  • " + move);
            }
            System.out.println();
        }
    }

    /**
     * Display enhanced error message for invalid moves
     */
    public void displayInvalidMoveError(String from, String to, String reason) {
        System.out.println(colorText("❌ Invalid move: " + from + " to " + to, ANSI_RED + ANSI_BOLD));
        System.out.println(colorText("Reason: " + reason, ANSI_RED));
        System.out.println();
    }

    /**
     * Display move validation with detailed feedback
     */
    public String validateMoveWithFeedback(String from, String to) {
        Board board = gameState.getBoard();
        IPiece piece = board.getPieceAt(from);
        
        if (piece == null) {
            return "No piece found at " + from;
        }
        
        if (!piece.getColor().equals(gameState.getCurrentTurn())) {
            return "Cannot move opponent's piece";
        }
        
        IPiece targetPiece = board.getPieceAt(to);
        if (targetPiece != null && targetPiece.getColor().equals(piece.getColor())) {
            return "Cannot capture your own piece";
        }
        
        if (!piece.isValidMove(to, board)) {
            return "Invalid move for " + piece.getClass().getSimpleName().toLowerCase();
        }
        
        return null; // No error
    }

    /**
     * Disable colors for systems that don't support ANSI codes
     */
    public void disableColors() {
        this.useColors = false;
    }

    /**
     * Enable colors (default)
     */
    public void enableColors() {
        this.useColors = true;
    }
}
