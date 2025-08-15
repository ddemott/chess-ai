package com.ddemott.chessai;

import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.ddemott.chessai.pieces.IPiece;
import com.ddemott.chessai.pieces.Pawn;
import com.ddemott.chessai.pieces.Rook;
import com.ddemott.chessai.pieces.Knight;
import com.ddemott.chessai.pieces.Bishop;
import com.ddemott.chessai.pieces.Queen;
import com.ddemott.chessai.pieces.King;

/**
 * Manages the history of moves in a chess game, including algebraic notation generation
 * and PGN export functionality.
 */
public class MoveHistory {

    /**
     * Returns a copy of the moves list (for compatibility with legacy code)
     */
    public List<Move> getMoves() {
        return new ArrayList<>(moves);
    }
    private final ArrayList<Move> moves;
    private int currentMoveIndex;

    public MoveHistory() {
        this.moves = new ArrayList<>();
        this.currentMoveIndex = -1;
    }

    /**
     * Adds a move to the history and generates its algebraic notation
     */
    public void addMove(String from, String to, IPiece movingPiece, IPiece capturedPiece, 
                       Board board, String playerColor) {
        addMove(from, to, movingPiece, capturedPiece, board, playerColor, null);
    }
    
    /**
     * Adds a move to the history with promotion support
     */
    public void addMove(String from, String to, IPiece movingPiece, IPiece capturedPiece, 
                       Board board, String playerColor, String promotionPiece) {
        // Remove any moves after current position (for undo/redo support)
        while (moves.size() > currentMoveIndex + 1) {
            moves.remove(moves.size() - 1);
        }

        int moveNumber = (moves.size() / 2) + 1;
        String algebraicNotation = generateAlgebraicNotation(from, to, movingPiece, capturedPiece, board, promotionPiece);
        
        Move move = new Move(from, to, movingPiece, capturedPiece, algebraicNotation, 
                           moveNumber, playerColor, false, false, false, false, promotionPiece);
        
        moves.add(move);
        currentMoveIndex++;
    }

    
    /**
     * Generates Standard Algebraic Notation (SAN) for a move with promotion support
     */
    private String generateAlgebraicNotation(String from, String to, IPiece movingPiece, 
                                           IPiece capturedPiece, Board board, String promotionPiece) {
        StringBuilder notation = new StringBuilder();

        // Handle castling
        if (movingPiece instanceof King && Math.abs(from.charAt(0) - to.charAt(0)) == 2) {
            if (to.charAt(0) > from.charAt(0)) {
                return "O-O"; // Kingside castling
            } else {
                return "O-O-O"; // Queenside castling
            }
        }

        // Add piece symbol (nothing for pawns)
        if (!(movingPiece instanceof Pawn)) {
            notation.append(getPieceSymbol(movingPiece));
        }

        // Add disambiguation if needed
        String disambiguation = getDisambiguation(from, to, movingPiece, board);
        notation.append(disambiguation);

        // Add capture symbol
        if (capturedPiece != null) {
            if (movingPiece instanceof Pawn) {
                notation.append(from.charAt(0)); // Add file for pawn captures
            }
            notation.append("x");
        }

        // Add destination square
        notation.append(to);

        // Add promotion
        if (promotionPiece != null) {
            notation.append("=").append(promotionPiece);
        }

        // Add check/checkmate indicators (to be properly implemented)
        // This is a placeholder - proper check detection would be needed
        // if (isCheck) { notation.append("+"); }
        // if (isCheckmate) { notation.append("#"); }

        return notation.toString();
    }

    /**
     * Gets the piece symbol for algebraic notation
     */
    private String getPieceSymbol(IPiece piece) {
        if (piece instanceof King) return "K";
        if (piece instanceof Queen) return "Q";
        if (piece instanceof Rook) return "R";
        if (piece instanceof Bishop) return "B";
        if (piece instanceof Knight) return "N";
        return ""; // Pawn has no symbol
    }

    /**
     * Determines if disambiguation is needed for algebraic notation
     */
    private String getDisambiguation(String from, String to, IPiece movingPiece, Board board) {
        // This is a simplified version - full disambiguation would require
        // checking all pieces of the same type that could move to the same square
        return "";
    }

    /**
     * Gets the current move list as a formatted string
     */
    public String getMoveListDisplay() {
        StringBuilder display = new StringBuilder();
        display.append("Move History:\n");
        display.append("=============\n");

        for (int i = 0; i < moves.size(); i += 2) {
            int moveNumber = (i / 2) + 1;
            display.append(String.format("%d. ", moveNumber));
            
            // White's move
            if (i < moves.size()) {
                display.append(String.format("%-8s", moves.get(i).getAlgebraicNotation()));
            }
            
            // Black's move
            if (i + 1 < moves.size()) {
                display.append(String.format("%-8s", moves.get(i + 1).getAlgebraicNotation()));
            }
            
            display.append("\n");
        }

        return display.toString();
    }

    /**
     * Gets the last move played
     */
    public Move getLastMove() {
        if (moves.isEmpty()) {
            return null;
        }
        return moves.get(currentMoveIndex);
    }

    /**
     * Gets all moves in the history
     */
    public List<Move> getAllMoves() {
        return new ArrayList<>(moves);
    }

    /**
     * Gets the number of moves played
     */
    public int getMoveCount() {
        return moves.size();
    }

    /**
     * Checks if undo is possible
     */
    public boolean canUndo() {
        return currentMoveIndex >= 0;
    }

    /**
     * Checks if redo is possible
     */
    public boolean canRedo() {
        return currentMoveIndex < moves.size() - 1;
    }

    /**
     * Moves back one position in history (for undo functionality)
     */
    public Move undoMove() {
        if (!canUndo()) {
            return null;
        }
        
        Move move = moves.get(currentMoveIndex);
        currentMoveIndex--;
        return move;
    }

    /**
     * Moves forward one position in history (for redo functionality)
     */
    public Move redoMove() {
        if (!canRedo()) {
            return null;
        }
        
        currentMoveIndex++;
        return moves.get(currentMoveIndex);
    }

    /**
     * Exports the game in PGN (Portable Game Notation) format
     */
    public String exportToPGN(String whitePlayer, String blackPlayer, String result) {
        StringBuilder pgn = new StringBuilder();
        
        // PGN Headers
        LocalDateTime now = LocalDateTime.now();
        pgn.append("[Event \"ChessAI Game\"]\n");
        pgn.append("[Site \"Local\"]\n");
        pgn.append("[Date \"").append(now.format(DateTimeFormatter.ofPattern("yyyy.MM.dd"))).append("\"]\n");
        pgn.append("[Round \"1\"]\n");
        pgn.append("[White \"").append(whitePlayer).append("\"]\n");
        pgn.append("[Black \"").append(blackPlayer).append("\"]\n");
        pgn.append("[Result \"").append(result).append("\"]\n");
        pgn.append("\n");

        // Move list
        for (int i = 0; i < moves.size(); i += 2) {
            int moveNumber = (i / 2) + 1;
            pgn.append(moveNumber).append(". ");
            
            // White's move
            if (i < moves.size()) {
                pgn.append(moves.get(i).getAlgebraicNotation()).append(" ");
            }
            
            // Black's move
            if (i + 1 < moves.size()) {
                pgn.append(moves.get(i + 1).getAlgebraicNotation()).append(" ");
            }
            
            // Add line break every 8 moves for readability
            if (moveNumber % 8 == 0) {
                pgn.append("\n");
            }
        }
        
        pgn.append(result);
        return pgn.toString();
    }

    /**
     * Saves the game to a PGN file
     */
    public boolean saveToPGNFile(String filename, String whitePlayer, String blackPlayer, String result) {
        try {
            String pgnContent = exportToPGN(whitePlayer, blackPlayer, result);
            Path path = Paths.get(filename);
            Files.write(path, pgnContent.getBytes());
            return true;
        } catch (IOException e) {
            System.err.println("Error saving PGN file: " + e.getMessage());
            return false;
        }
    }

    /**
     * Loads a game from a PGN file
     * @param filename The PGN file to load
     * @return PGNGameData containing headers and moves, or null if failed
     */
    public static PGNGameData loadFromPGNFile(String filename) {
        try {
            Path path = Paths.get(filename);
            if (!Files.exists(path)) {
                System.err.println("PGN file not found: " + filename);
                return null;
            }
            
            String content = Files.readString(path);
            return parsePGN(content);
        } catch (IOException e) {
            System.err.println("Error reading PGN file: " + e.getMessage());
            return null;
        }
    }

    /**
     * Parses PGN content and extracts headers and moves
     * @param pgnContent The PGN content as a string
     * @return PGNGameData containing parsed information
     */
    public static PGNGameData parsePGN(String pgnContent) {
        PGNGameData gameData = new PGNGameData();
        String[] lines = pgnContent.split("\n");
        
        boolean inHeaders = true;
        StringBuilder moveText = new StringBuilder();
        
        for (String line : lines) {
            line = line.trim();
            
            if (line.isEmpty()) {
                if (inHeaders) {
                    inHeaders = false; // Empty line separates headers from moves
                }
                continue;
            }
            
            if (inHeaders && line.startsWith("[") && line.endsWith("]")) {
                // Parse header
                parseHeader(line, gameData);
            } else if (!inHeaders) {
                // Accumulate move text
                moveText.append(line).append(" ");
            }
        }
        
        // Parse moves
        if (moveText.length() > 0) {
            gameData.moves = parseMoves(moveText.toString().trim());
        }
        
        return gameData;
    }

    /**
     * Parses a PGN header line
     */
    private static void parseHeader(String headerLine, PGNGameData gameData) {
        // Remove brackets and split on first quote
        String content = headerLine.substring(1, headerLine.length() - 1);
        int firstQuote = content.indexOf('"');
        if (firstQuote == -1) return;
        
        String key = content.substring(0, firstQuote).trim();
        String value = content.substring(firstQuote + 1);
        if (value.endsWith("\"")) {
            value = value.substring(0, value.length() - 1);
        }
        
        switch (key) {
            case "Event": gameData.event = value; break;
            case "Site": gameData.site = value; break;
            case "Date": gameData.date = value; break;
            case "Round": gameData.round = value; break;
            case "White": gameData.whitePlayer = value; break;
            case "Black": gameData.blackPlayer = value; break;
            case "Result": gameData.result = value; break;
        }
    }

    /**
     * Parses move text and extracts algebraic notation moves
     */
    private static List<String> parseMoves(String moveText) {
        List<String> moves = new ArrayList<>();
        
        // Remove result notation at the end
        moveText = moveText.replaceAll("\\s+(1-0|0-1|1/2-1/2|\\*)\\s*$", "");
        
        // Split by move numbers and process
        String[] tokens = moveText.split("\\s+");
        
        for (String token : tokens) {
            token = token.trim();
            if (token.isEmpty()) continue;
            
            // Skip move numbers (e.g., "1.", "2.", etc.)
            if (token.matches("\\d+\\.")) continue;
            
            // Skip comments in braces or parentheses
            if (token.startsWith("{") || token.startsWith("(")) continue;
            
            // Clean up the move notation
            token = cleanMoveNotation(token);
            
            if (!token.isEmpty() && isValidMoveNotation(token)) {
                moves.add(token);
            }
        }
        
        return moves;
    }

    /**
     * Cleans move notation by removing annotations
     */
    private static String cleanMoveNotation(String move) {
        // Remove check (+), checkmate (#), annotation (!,?, etc.)
        return move.replaceAll("[+#!?]", "");
    }

    /**
     * Basic validation of move notation
     */
    private static boolean isValidMoveNotation(String move) {
        if (move.length() < 2) return false;
        
        // Castling
        if (move.equals("O-O") || move.equals("O-O-O")) return true;
        
        // Standard notation should end with a square (e.g., e4, Nf3, axb5)
        return move.matches(".*[a-h][1-8].*");
    }

    /**
     * Data class to hold parsed PGN information
     */
    public static class PGNGameData {
        public String event = "";
        public String site = "";
        public String date = "";
        public String round = "";
        public String whitePlayer = "";
        public String blackPlayer = "";
        public String result = "";
        public List<String> moves = new ArrayList<>();
        
        @Override
        public String toString() {
            return String.format("PGN Game: %s vs %s (%s) - %d moves", 
                whitePlayer, blackPlayer, result, moves.size());
        }
    }

    /**
     * Clears the move history
     */
    public void clear() {
        moves.clear();
        currentMoveIndex = -1;
        positionHistory.clear();
        halfmoveClock = 0;
    }

    // Position repetition tracking
    private final ArrayList<String> positionHistory = new ArrayList<>();
    private int halfmoveClock = 0;

    /**
     * Gets the history of positions for threefold repetition checking
     */
    public List<String> getPositionHistory() {
        return new ArrayList<>(positionHistory);
    }

    /**
     * Adds a position to the history and returns true if it's a threefold repetition
     */
    public void addPosition(String position) {
        positionHistory.add(position);
        
        // Remove any positions after the current index (for undo/redo support)
        while (positionHistory.size() > currentMoveIndex + 2) { // +2 because we add position before the move is recorded
            positionHistory.remove(positionHistory.size() - 1);
        }
    }

    /**
     * Checks if the current position has appeared three times
     */
    public boolean isThreefoldRepetition() {
        if (positionHistory.isEmpty()) return false;
        String currentPosition = positionHistory.get(positionHistory.size() - 1);
        int repetitions = 0;
        
        for (String position : positionHistory) {
            if (position.equals(currentPosition)) {
                repetitions++;
                if (repetitions >= 3) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Gets the half-move clock for fifty-move rule checking
     * Increments on each move, resets on pawn moves and captures
     */
    public int getHalfmoveClock() {
        return halfmoveClock;
    }

    /**
     * Updates the half-move clock
     * @param isPawnMove true if a pawn was moved
     * @param isCapture true if a piece was captured
     */
    public void updateHalfmoveClock(boolean isPawnMove, boolean isCapture) {
        if (isPawnMove || isCapture) {
            halfmoveClock = 0;
        } else {
            halfmoveClock++;
        }
    }
}
