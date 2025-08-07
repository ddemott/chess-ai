package com.ddemott.chessai;

import java.util.ArrayList;
import java.util.List;

import com.ddemott.chessai.pieces.Bishop;
import com.ddemott.chessai.pieces.IPiece;
import com.ddemott.chessai.pieces.King;
import com.ddemott.chessai.pieces.Knight;
import com.ddemott.chessai.pieces.Pawn;
import com.ddemott.chessai.pieces.Queen;
import com.ddemott.chessai.pieces.Rook;

public class Board {
    private IPiece[][] board; // 2D array to represent the board
    private String enPassantTarget; // Target square for en passant capture (e.g., "e3")

    public Board() {
        board = new IPiece[8][8]; // 8x8 chess board
        enPassantTarget = null; // No en passant target initially
        initializeBoard();
    }

    private void initializeBoard() {
        // Adding Pawns for both sides
        for (char col = 'a'; col <= 'h'; col++) {
            board[1][col - 'a'] = new Pawn("White", col + "2");
            board[6][col - 'a'] = new Pawn("Black", col + "7");
        }

        // Adding Rooks for both sides
        board[0][0] = new Rook("White", "a1");
        board[0][7] = new Rook("White", "h1");
        board[7][0] = new Rook("Black", "a8");
        board[7][7] = new Rook("Black", "h8");

        // Adding Knights for both sides
        board[0][1] = new Knight("White", "b1");
        board[0][6] = new Knight("White", "g1");
        board[7][1] = new Knight("Black", "b8");
        board[7][6] = new Knight("Black", "g8");

        // Adding Bishops for both sides
        board[0][2] = new Bishop("White", "c1");
        board[0][5] = new Bishop("White", "f1");
        board[7][2] = new Bishop("Black", "c8");
        board[7][5] = new Bishop("Black", "f8");

        // Adding Queens for both sides
        board[0][3] = new Queen("White", "d1");
        board[7][3] = new Queen("Black", "d8");

        // Adding Kings for both sides
        board[0][4] = new King("White", "e1");
        board[7][4] = new King("Black", "e8");
    }

    public IPiece getPieceAt(String position) {
        int[] coords = convertPositionToCoordinates(position);
        if (coords[0] < 0 || coords[0] >= 8 || coords[1] < 0 || coords[1] >= 8) {
            return null; // Out of bounds
        }
        return board[coords[0]][coords[1]];
    }

    public void setPieceAt(String position, IPiece piece) {
        int[] coords = convertPositionToCoordinates(position);
        if (coords[0] < 0 || coords[0] >= 8 || coords[1] < 0 || coords[1] >= 8) {
            return; // Out of bounds
        }
        board[coords[0]][coords[1]] = piece;
    }

    public int[] convertPositionToCoordinates(String position) {
        position = position.toLowerCase(); // Ensure position is in lowercase
        char column = position.charAt(0);
        int row = position.charAt(1) - '1';
        return new int[]{row, column - 'a'};
    }

    public String convertCoordinatesToPosition(int row, int col) {
        if (row < 0 || row >= 8 || col < 0 || col >= 8) {
            return null; // Out of bounds
        }
        char column = (char) ('a' + col);
        char rowChar = (char) ('1' + row);
        return String.valueOf(column) + rowChar;
    }

    public boolean movePiece(String from, String to) {
        IPiece piece = getPieceAt(from);

        if (piece == null) {
            //System.out.println("No piece found at " + from);
            return false;
        }

        //System.out.println("Attempting to move from " + from + " to " + to);
        //System.out.println("Piece found at " + from + ": " + piece.getClass().getSimpleName() + " (" + piece.getColor() + ")");

        if (piece.isValidMove(to, this)) {
            // Check if this is a castling move
            if (piece instanceof King && Math.abs(convertPositionToCoordinates(to)[1] - convertPositionToCoordinates(from)[1]) == 2) {
                boolean castlingSuccess = executeCastling(from, to);
                if (castlingSuccess) {
                    // Clear en passant target after any move
                    enPassantTarget = null;
                }
                return castlingSuccess;
            }
            
            // Check if this is an en passant capture
            if (piece instanceof Pawn && isEnPassantCapture(from, to)) {
                return executeEnPassant(from, to);
            }
            
            // Track pawn two-square moves for en passant
            if (piece instanceof Pawn && isPawnTwoSquareMove(from, to)) {
                // Calculate en passant target square (square behind the pawn)
                int[] fromCoords = convertPositionToCoordinates(from);
                int[] toCoords = convertPositionToCoordinates(to);
                int targetRow = (fromCoords[0] + toCoords[0]) / 2; // Middle square
                int targetCol = fromCoords[1];
                enPassantTarget = convertCoordinatesToPosition(targetRow, targetCol);
            } else {
                // Clear en passant target if not a pawn two-square move
                enPassantTarget = null;
            }
            
            // Regular move
            setPieceAt(to, piece);
            setPieceAt(from, null);
            piece.setPosition(to);
            piece.setHasMoved(true);
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * Execute castling move - moves both king and rook
     */
    private boolean executeCastling(String kingFrom, String kingTo) {
        IPiece king = getPieceAt(kingFrom);
        int[] kingFromCoords = convertPositionToCoordinates(kingFrom);
        int[] kingToCoords = convertPositionToCoordinates(kingTo);
        
        // Determine if kingside or queenside castling
        boolean isKingside = kingToCoords[1] > kingFromCoords[1];
        
        // Get rook positions
        String rookFrom, rookTo;
        if (isKingside) {
            rookFrom = convertCoordinatesToPosition(kingFromCoords[0], 7); // h-file
            rookTo = convertCoordinatesToPosition(kingFromCoords[0], 5);   // f-file
        } else {
            rookFrom = convertCoordinatesToPosition(kingFromCoords[0], 0); // a-file
            rookTo = convertCoordinatesToPosition(kingFromCoords[0], 3);   // d-file
        }
        
        IPiece rook = getPieceAt(rookFrom);
        
        // Move king
        setPieceAt(kingTo, king);
        setPieceAt(kingFrom, null);
        king.setPosition(kingTo);
        
        // Move rook
        setPieceAt(rookTo, rook);
        setPieceAt(rookFrom, null);
        rook.setPosition(rookTo);
        
        return true;
    }

    /**
     * Check if a move is a pawn two-square move that enables en passant
     */
    private boolean isPawnTwoSquareMove(String from, String to) {
        int[] fromCoords = convertPositionToCoordinates(from);
        int[] toCoords = convertPositionToCoordinates(to);
        
        // Check if it's a pawn moving exactly 2 squares vertically
        return Math.abs(toCoords[0] - fromCoords[0]) == 2 && fromCoords[1] == toCoords[1];
    }

    /**
     * Check if a move is an en passant capture
     */
    private boolean isEnPassantCapture(String from, String to) {
        if (enPassantTarget == null) {
            return false;
        }
        
        // Check if the destination matches the en passant target
        return to.equals(enPassantTarget);
    }

    /**
     * Execute an en passant capture
     */
    private boolean executeEnPassant(String from, String to) {
        IPiece pawn = getPieceAt(from);
        int[] toCoords = convertPositionToCoordinates(to);
        
        // Determine the captured pawn's position
        int capturedPawnRow = pawn.getColor().equals("White") ? toCoords[0] - 1 : toCoords[0] + 1;
        String capturedPawnPosition = convertCoordinatesToPosition(capturedPawnRow, toCoords[1]);
        
        // Move the capturing pawn
        setPieceAt(to, pawn);
        setPieceAt(from, null);
        pawn.setPosition(to);
        pawn.setHasMoved(true);
        
        // Remove the captured pawn
        setPieceAt(capturedPawnPosition, null);
        
        // Clear en passant target
        enPassantTarget = null;
        
        return true;
    }

    public List<String> getAllPossibleMoves(String color) {
        List<String> possibleMoves = new ArrayList<>();
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                IPiece piece = board[row][col];
                if (piece != null && piece.getColor().equals(color)) {
                    List<String> moves = piece.getAllPossibleMoves(this);
                    if (moves != null && !moves.isEmpty()) {
                        possibleMoves.addAll(moves);
                    }
                }
            }
        }
        return possibleMoves;
    }

    public IPiece[][] getBoardArray() {
        return board;
    }

    @Override
    public Board clone() {
        Board newBoard = new Board();
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (this.board[row][col] != null) {
                    newBoard.board[row][col] = this.board[row][col].clonePiece();
                }
            }
        }
        // Copy en passant target state
        newBoard.enPassantTarget = this.enPassantTarget;
        return newBoard;
    }

    /**
     * Get the current en passant target square
     * @return the target square (e.g., "e3") or null if no en passant is possible
     */
    public String getEnPassantTarget() {
        return enPassantTarget;
    }

    /**
     * Set the en passant target square
     * @param target the target square (e.g., "e3") or null to clear
     */
    public void setEnPassantTarget(String target) {
        this.enPassantTarget = target;
    }

    // Method to get board representation as string for display purposes
    public String getBoardRepresentation() {
        StringBuilder sb = new StringBuilder();
        sb.append("  a b c d e f g h\n");
        for (int row = 7; row >= 0; row--) {
            sb.append((row + 1)).append(" ");
            for (int col = 0; col < 8; col++) {
                IPiece piece = board[row][col];
                if (piece == null) {
                    sb.append(". ");
                } else {
                    String pieceSymbol = getPieceSymbol(piece);
                    sb.append(pieceSymbol).append(" ");
                }
            }
            sb.append((row + 1)).append("\n");
        }
        sb.append("  a b c d e f g h\n");
        return sb.toString();
    }

    // Updating the printBoard method to use standard chess notation
    public void printBoard() {
        System.out.println("  a b c d e f g h");
        for (int row = 7; row >= 0; row--) {
            System.out.print((row + 1) + " ");
            for (int col = 0; col < 8; col++) {
                IPiece piece = board[row][col];
                if (piece == null) {
                    System.out.print(". ");
                } else {
                    String pieceSymbol = getPieceSymbol(piece);
                    System.out.print(pieceSymbol + " ");
                }
            }
            System.out.println((row + 1));
        }
        System.out.println("  a b c d e f g h");
    }

    private String getPieceSymbol(IPiece piece) {
        char symbol;
        if (piece instanceof Pawn) {
            symbol = 'P';
        } else if (piece instanceof Knight) {
            symbol = 'N';
        } else if (piece instanceof Bishop) {
            symbol = 'B';
        } else if (piece instanceof Rook) {
            symbol = 'R';
        } else if (piece instanceof Queen) {
            symbol = 'Q';
        } else if (piece instanceof King) {
            symbol = 'K';
        } else {
            symbol = '?'; // Unknown piece type
        }
        return piece.getColor().equals("White") ? String.valueOf(symbol) : String.valueOf(Character.toLowerCase(symbol));
    }

    /**
     * Check if the king of the specified color is in check
     */
    public boolean isKingInCheck(String kingColor) {
        // Find the king position
        String kingPosition = findKingPosition(kingColor);
        if (kingPosition == null) {
            return false; // No king found (shouldn't happen in normal game)
        }
        
        // Check if any opponent piece can attack the king
        String opponentColor = kingColor.equals("White") ? "Black" : "White";
        List<String> opponentMoves = getAllPossibleMoves(opponentColor);
        
        for (String move : opponentMoves) {
            String[] parts = move.split(" ");
            if (parts.length == 2 && parts[1].equals(kingPosition)) {
                return true; // Opponent can attack the king
            }
        }
        
        return false;
    }

    /**
     * Find the position of the king for the specified color
     */
    public String findKingPosition(String kingColor) {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                IPiece piece = board[row][col];
                if (piece instanceof King && piece.getColor().equals(kingColor)) {
                    return convertCoordinatesToPosition(row, col);
                }
            }
        }
        return null; // King not found
    }

    /**
     * Check if a square is under attack by the opponent
     * Uses a non-recursive approach to avoid infinite loops during castling validation
     */
    public boolean isSquareUnderAttack(String position, String defendingColor) {
        String attackingColor = defendingColor.equals("White") ? "Black" : "White";
        
        // Check each opposing piece to see if it can attack the position
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                IPiece piece = board[row][col];
                if (piece != null && piece.getColor().equals(attackingColor)) {
                    // For non-king pieces, use normal validation
                    if (!(piece instanceof King)) {
                        if (piece.isValidMove(position, this)) {
                            return true;
                        }
                    } else {
                        // For king pieces, only check normal moves (not castling) to avoid recursion
                        String currentPos = piece.getPosition();
                        int[] currentCoords = convertPositionToCoordinates(currentPos);
                        int[] targetCoords = convertPositionToCoordinates(position);
                        
                        int dx = Math.abs(targetCoords[0] - currentCoords[0]);
                        int dy = Math.abs(targetCoords[1] - currentCoords[1]);
                        
                        // King can attack one square in any direction
                        if (dx <= 1 && dy <= 1 && (dx != 0 || dy != 0)) {
                            return true;
                        }
                    }
                }
            }
        }
        
        return false;
    }

    /**
     * Check if the specified player is in checkmate
     */
    public boolean isCheckmate(String playerColor) {
        if (!isKingInCheck(playerColor)) {
            return false; // Not in check, so can't be checkmate
        }
        
        // Try all possible moves to see if any gets out of check
        List<String> possibleMoves = getAllPossibleMoves(playerColor);
        
        for (String move : possibleMoves) {
            String[] parts = move.split(" ");
            if (parts.length == 2) {
                // Simulate the move
                Board clonedBoard = this.clone();
                if (clonedBoard.movePiece(parts[0], parts[1])) {
                    // Check if this move gets out of check
                    if (!clonedBoard.isKingInCheck(playerColor)) {
                        return false; // Found a move that gets out of check
                    }
                }
            }
        }
        
        return true; // No moves get out of check - checkmate
    }

    /**
     * Check if the specified player is in stalemate
     */
    public boolean isStalemate(String playerColor) {
        if (isKingInCheck(playerColor)) {
            return false; // In check, so can't be stalemate
        }
        
        // Check if player has any legal moves
        List<String> possibleMoves = getAllPossibleMoves(playerColor);
        
        for (String move : possibleMoves) {
            String[] parts = move.split(" ");
            if (parts.length == 2) {
                // Simulate the move
                Board clonedBoard = this.clone();
                if (clonedBoard.movePiece(parts[0], parts[1])) {
                    // Check if this move puts own king in check
                    if (!clonedBoard.isKingInCheck(playerColor)) {
                        return false; // Found a legal move
                    }
                }
            }
        }
        
        return true; // No legal moves available - stalemate
    }
    
    /**
     * Move piece with optional pawn promotion
     * @param from Starting position
     * @param to Ending position  
     * @param promotionPiece Piece to promote to (Q, R, B, N) or null for no promotion
     * @return true if move was successful
     */
    public boolean movePiece(String from, String to, String promotionPiece) {
        IPiece piece = getPieceAt(from);

        if (piece == null) {
            return false;
        }

        // Check if this is a pawn promotion move
        if (piece instanceof Pawn && isPawnPromotion(from, to)) {
            if (promotionPiece == null || !isValidPromotionPiece(promotionPiece)) {
                return false; // Must provide valid promotion piece
            }
            return executePromotion(from, to, promotionPiece);
        }
        
        // If promotion piece was specified but this isn't a promotion move, reject it
        if (promotionPiece != null) {
            return false;
        }
        
        // For non-promotion moves, use regular movePiece method
        return movePiece(from, to);
    }
    
    /**
     * Check if a pawn move is a promotion
     */
    private boolean isPawnPromotion(String from, String to) {
        IPiece piece = getPieceAt(from);
        if (!(piece instanceof Pawn)) {
            return false;
        }
        
        int[] toCoords = convertPositionToCoordinates(to);
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
     * Validate promotion piece type
     */
     private boolean isValidPromotionPiece(String promotionPiece) {
        // Only Queen, Rook, Bishop, and Knight are allowed for promotion
        return promotionPiece.equals("Q") || promotionPiece.equals("R") || 
               promotionPiece.equals("B") || promotionPiece.equals("N");
    }
    
    /**
     * Execute pawn promotion
     */
    private boolean executePromotion(String from, String to, String promotionPiece) {
        IPiece pawn = getPieceAt(from);
        
        // First validate the pawn move itself
        if (!pawn.isValidMove(to, this)) {
            return false;
        }
        
        // Create the promoted piece
        IPiece promotedPiece = createPromotionPiece(promotionPiece, pawn.getColor(), to);
        if (promotedPiece == null) {
            return false;
        }
        
        // Clear en passant target
        enPassantTarget = null;
        
        // Execute the move by placing promoted piece and removing pawn
        setPieceAt(to, promotedPiece);
        setPieceAt(from, null);
        
        return true;
    }
    
    /**
     * Create a piece for promotion
     */
    private IPiece createPromotionPiece(String pieceType, String color, String position) {
        switch (pieceType) {
            case "Q":
                return new Queen(color, position);
            case "R":
                return new Rook(color, position);
            case "B":
                return new Bishop(color, position);
            case "N":
                return new Knight(color, position);
            default:
                return null;
        }
    }
    
    /**
     * Generate algebraic notation for promotion moves
     */
    public String getPromotionNotation(String from, String to, String promotionPiece) {
        StringBuilder notation = new StringBuilder();
        
        int[] fromCoords = convertPositionToCoordinates(from);
        IPiece capturedPiece = getPieceAt(to);
        
        if (capturedPiece != null) {
            // Capture promotion: exf8=Q
            char fromFile = (char)('a' + fromCoords[1]);
            notation.append(fromFile).append("x").append(to);
        } else {
            // Non-capture promotion: e8=Q
            notation.append(to);
        }
        
        notation.append("=").append(promotionPiece);
        return notation.toString();
    }
    
    /**
     * Add clearBoard method for testing
     */
    public void clearBoard() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                board[row][col] = null;
            }
        }
    }
}
