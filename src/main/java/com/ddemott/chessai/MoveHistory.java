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
    private final List<Move> moves;
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
     * Generates Standard Algebraic Notation (SAN) for a move
     */
    private String generateAlgebraicNotation(String from, String to, IPiece movingPiece, 
                                           IPiece capturedPiece, Board board) {
        return generateAlgebraicNotation(from, to, movingPiece, capturedPiece, board, null);
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
     * Clears the move history
     */
    public void clear() {
        moves.clear();
        currentMoveIndex = -1;
    }
}
