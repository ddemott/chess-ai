package com.ddemott.chessai;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import com.ddemott.chessai.pieces.Bishop;
import com.ddemott.chessai.pieces.Pawn;
import com.ddemott.chessai.Board;

public class BishopMovementTest {

    @Test
    public void testValidDiagonalMove() {
        // Initialize the board
        Board board = new Board();
        board.clearBoard();
        
        // Create a white bishop and place it at c1
        Bishop bishop = new Bishop("White", "c1");
        board.setPieceAt("c1", bishop);
        bishop.setPosition("c1"); // Ensure bishop's position matches board
        
        // Attempt to move the bishop diagonally to h6
        boolean validMove = bishop.isValidMove("h6", board);
        
        // The move should be valid
        assertTrue(validMove, "Diagonal move from c1 to h6 should be valid for bishop");
    }
    
    @Test
    public void testInvalidStraightMove() {
        Board board = new Board();
        board.clearBoard();
        Bishop bishop = new Bishop("White", "c1");
        board.setPieceAt("c1", bishop);
        bishop.setPosition("c1");
        // Bishops cannot move straight vertically or horizontally.
        assertFalse(bishop.isValidMove("c4", board));
    }

    @Test
    public void testBlockedByOwnPiece() {
        Board board = new Board();
        board.clearBoard();
        Bishop bishop = new Bishop("White", "c1");
        Pawn pawn = new Pawn("White", "e3");
        board.setPieceAt("c1", bishop);
        bishop.setPosition("c1");
        board.setPieceAt("e3", pawn);
        pawn.setPosition("e3");
        assertFalse(bishop.isValidMove("h6", board), "Bishop should NOT be able to move through own piece");
    }

    @Test
    public void testBlockedByOpponentPiece() {
        Board board = new Board();
        board.clearBoard();
        Bishop bishop = new Bishop("White", "c1");
        Pawn pawn = new Pawn("Black", "e3");
        board.setPieceAt("c1", bishop);
        bishop.setPosition("c1");
        board.setPieceAt("e3", pawn);
        pawn.setPosition("e3");
        assertFalse(bishop.isValidMove("h6", board), "Bishop should NOT be able to move through opponent's piece");
        assertTrue(bishop.isValidMove("e3", board), "Bishop should be able to capture opponent's piece at e3");
    }
}
