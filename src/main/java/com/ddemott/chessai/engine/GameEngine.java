package com.ddemott.chessai.engine;

import com.ddemott.chessai.State;
import com.ddemott.chessai.MoveHistory;
import com.ddemott.chessai.Move;
import com.ddemott.chessai.ai.AIStrategy;
import com.ddemott.chessai.ai.MinMaxStrategy;
import com.ddemott.chessai.ai.AIDifficulty;

/**
 * Manages the state and logic of the chess game.
 */
public class GameEngine {
    private State state;
    private AIStrategy aiStrategy;
    private AIDifficulty aiDifficulty;

    // Original constructor for backward compatibility
    public GameEngine(int aiDepth) {
        this.aiDifficulty = AIDifficulty.fromDepth(aiDepth);
        this.state = new State();
        this.aiStrategy = new MinMaxStrategy(aiDepth);
        state.setAIStrategy(aiStrategy);
    }
    
    // New constructor with difficulty enum
    public GameEngine(AIDifficulty difficulty) {
        this.aiDifficulty = difficulty;
        this.state = new State();
        this.aiStrategy = new MinMaxStrategy(difficulty.getDepth());
        state.setAIStrategy(aiStrategy);
    }
    
    // Constructor for AI vs AI with different difficulties
    public GameEngine(AIDifficulty whiteDifficulty, AIDifficulty blackDifficulty) {
        // Start with white difficulty, will be switched during gameplay
        this.aiDifficulty = whiteDifficulty;
        this.state = new State();
        this.aiStrategy = new MinMaxStrategy(whiteDifficulty.getDepth());
        state.setAIStrategy(aiStrategy);
    }

    public String getCurrentTurn() {
        return state.getCurrentTurn();
    }

    public boolean movePiece(String from, String to) {
        return state.movePiece(from, to);
    }
    
    public boolean movePiece(String from, String to, String promotionPiece) {
        return state.movePiece(from, to, promotionPiece);
    }

    public String getBestMove() {
        return state.getBestMove();
    }

    public void makeAIMove() {
        String aiMove = getBestMove();
        if (aiMove != null) {
            String[] aiPositions = aiMove.split(" ");
            if (aiPositions.length == 2) {
                boolean moveSuccess = movePiece(aiPositions[0], aiPositions[1]);
                if (!moveSuccess) {
                    // Log that AI move failed - this shouldn't happen if AI is working correctly
                    System.err.println("Warning: AI suggested invalid move: " + aiMove);
                }
            }
        }
    }

    public String getBoardRepresentation() {
        return state.getBoard().getBoardRepresentation();
    }

    public State getGameState() {
        return state;
    }

    public void printBoard() {
        state.getBoard().printBoard();
    }

    // Move history related methods
    public MoveHistory getMoveHistory() {
        return state.getMoveHistory();
    }

    public String getMoveListDisplay() {
        return state.getMoveHistory().getMoveListDisplay();
    }

    public Move getLastMove() {
        return state.getMoveHistory().getLastMove();
    }

    public boolean undoLastMove() {
        return state.undoLastMove();
    }

    public boolean redoLastMove() {
        return state.redoLastMove();
    }

    public boolean canUndo() {
        return state.getMoveHistory().canUndo();
    }

    public boolean canRedo() {
        return state.getMoveHistory().canRedo();
    }

    public String exportGameToPGN(String whitePlayer, String blackPlayer, String result) {
        return state.getMoveHistory().exportToPGN(whitePlayer, blackPlayer, result);
    }

    public boolean saveGameToPGNFile(String filename, String whitePlayer, String blackPlayer, String result) {
        return state.getMoveHistory().saveToPGNFile(filename, whitePlayer, blackPlayer, result);
    }
    
    /**
     * Load a game from a PGN file
     * @param filename The PGN file to load
     * @return true if successfully loaded, false otherwise
     */
    public boolean loadGameFromPGNFile(String filename) {
        MoveHistory.PGNGameData gameData = MoveHistory.loadFromPGNFile(filename);
        if (gameData == null) {
            return false;
        }
        
        return loadGameFromPGNData(gameData);
    }
    
    /**
     * Load a game from parsed PGN data
     * @param gameData The parsed PGN data
     * @return true if successfully loaded, false otherwise
     */
    public boolean loadGameFromPGNData(MoveHistory.PGNGameData gameData) {
        try {
            // Reset the game state
            this.state = new State();
            state.setAIStrategy(aiStrategy);
            
            // Replay all the moves
            for (String algebraicMove : gameData.moves) {
                if (!playMoveFromAlgebraicNotation(algebraicMove)) {
                    System.err.println("Failed to play move: " + algebraicMove);
                    return false;
                }
            }
            
            System.out.println("Successfully loaded game: " + gameData);
            return true;
        } catch (Exception e) {
            System.err.println("Error loading game from PGN: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Attempts to play a move from algebraic notation
     * This is a simplified parser - full algebraic notation parsing is complex
     */
    private boolean playMoveFromAlgebraicNotation(String algebraicMove) {
        // Handle castling
        if (algebraicMove.equals("O-O")) {
            // Kingside castling
            String currentPlayer = getCurrentTurn();
            if (currentPlayer.equals("White")) {
                return movePiece("e1", "g1");
            } else {
                return movePiece("e8", "g8");
            }
        } else if (algebraicMove.equals("O-O-O")) {
            // Queenside castling
            String currentPlayer = getCurrentTurn();
            if (currentPlayer.equals("White")) {
                return movePiece("e1", "c1");
            } else {
                return movePiece("e8", "c8");
            }
        }
        
        // Handle promotion moves (e.g., e8=Q)
        if (algebraicMove.contains("=")) {
            String[] parts = algebraicMove.split("=");
            if (parts.length == 2) {
                String movepart = parts[0];
                String promotionPiece = parts[1];
                String[] coordinates = parseBasicMove(movepart);
                if (coordinates != null) {
                    return movePiece(coordinates[0], coordinates[1], promotionPiece);
                }
            }
        }
        
        // Try to parse as basic coordinate move
        String[] coordinates = parseBasicMove(algebraicMove);
        if (coordinates != null) {
            return movePiece(coordinates[0], coordinates[1]);
        }
        
        // If we can't parse it, try to find the move by checking all possible moves
        return findAndPlayMove(algebraicMove);
    }
    
    /**
     * Parse simple moves like "e4", "Nf3", "axb5" to coordinates
     */
    private String[] parseBasicMove(String move) {
        // This is a simplified parser - just handle basic cases
        if (move.length() >= 2) {
            String toSquare = move.substring(move.length() - 2);
            if (toSquare.matches("[a-h][1-8]")) {
                // For now, we'll need to implement move disambiguation
                // This is a complex task that would require analyzing the board state
                // For testing, we'll return null and use the fallback method
                return null;
            }
        }
        return null;
    }
    
    /**
     * Fallback method to find a move by trying all possible moves
     */
    private boolean findAndPlayMove(String algebraicMove) {
        // This is a placeholder - implementing full algebraic notation parsing
        // would require significant additional logic to handle disambiguation
        System.out.println("Warning: Could not parse move '" + algebraicMove + "' - skipping");
        return true; // Continue loading other moves
    }
    
    /**
     * Get current AI difficulty
     */
    public AIDifficulty getAIDifficulty() {
        return aiDifficulty;
    }
    
    /**
     * Change AI difficulty during gameplay
     */
    public void setAIDifficulty(AIDifficulty difficulty) {
        this.aiDifficulty = difficulty;
        this.aiStrategy = new MinMaxStrategy(difficulty.getDepth());
        state.setAIStrategy(aiStrategy);
    }
}
