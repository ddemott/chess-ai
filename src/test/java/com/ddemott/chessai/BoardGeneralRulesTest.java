package com.ddemott.chessai;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import com.ddemott.chessai.pieces.King;
import com.ddemott.chessai.pieces.Rook;
import com.ddemott.chessai.pieces.Bishop;
import com.ddemott.chessai.pieces.Queen;
import com.ddemott.chessai.pieces.Pawn;
import com.ddemott.chessai.pieces.Knight;
import com.ddemott.chessai.Board;

public class BoardGeneralRulesTest {

    @Test
    public void testStalemateDetection() {
        Board board = new Board();
        board.clearBoard();
        King blackKing = new King("Black", "h8");
        King whiteKing = new King("White", "f7");
        Queen whiteQueen = new Queen("White", "g6");
        board.setPieceAt("h8", blackKing);
        blackKing.setPosition("h8");
        board.setPieceAt("f7", whiteKing);
        whiteKing.setPosition("f7");
        board.setPieceAt("g6", whiteQueen);
        whiteQueen.setPosition("g6");
        assertTrue(board.isStalemate("Black"), "Black should be in stalemate");
    }

    @Test
    public void testCheckmateDetection() {
        Board board = new Board();
        board.clearBoard();
        King blackKing = new King("Black", "h8");
        Queen whiteQueen = new Queen("White", "g7");
        Rook whiteRook = new Rook("White", "h7");
        board.setPieceAt("h8", blackKing);
        blackKing.setPosition("h8");
        board.setPieceAt("g7", whiteQueen);
        whiteQueen.setPosition("g7");
        board.setPieceAt("h7", whiteRook);
        whiteRook.setPosition("h7");
        assertTrue(board.isCheckmate("Black"), "Black should be in checkmate");
    }

    @Test
    public void testPinScenario() {
        Board board = new Board();
        board.clearBoard();
        King whiteKing = new King("White", "e1");
        Rook blackRook = new Rook("Black", "e8");
        Bishop whiteBishop = new Bishop("White", "e4");
        board.setPieceAt("e1", whiteKing);
        whiteKing.setPosition("e1");
        board.setPieceAt("e8", blackRook);
        blackRook.setPosition("e8");
        board.setPieceAt("e4", whiteBishop);
        whiteBishop.setPosition("e4");
        // Bishop is pinned and cannot move off the e-file
        assertTrue(board.isPiecePinned("e4"), "Bishop should be pinned by rook");
    }

    @Test
    public void testDiscoveredCheckScenario() {
        Board board = new Board();
        board.clearBoard();
        King blackKing = new King("Black", "e8");
        Rook whiteRook = new Rook("White", "e1");
        Bishop whiteBishop = new Bishop("White", "e4");
        board.setPieceAt("e8", blackKing);
        blackKing.setPosition("e8");
        board.setPieceAt("e1", whiteRook);
        whiteRook.setPosition("e1");
        board.setPieceAt("e4", whiteBishop);
        whiteBishop.setPosition("e4");
        // Moving bishop off the e-file exposes king to check
        board.setPieceAt("e4", null);
        assertTrue(board.isKingInCheck("Black"), "Black king should be in check after discovered check");
    }

    @Test
    public void testIllegalMoveIntoCheckForNonKingPiece() {
        Board board = new Board();
        board.clearBoard();
        King whiteKing = new King("White", "e1");
        Rook blackRook = new Rook("Black", "e8");
        Bishop whiteBishop = new Bishop("White", "e4");
        board.setPieceAt("e1", whiteKing);
        whiteKing.setPosition("e1");
        board.setPieceAt("e8", blackRook);
        blackRook.setPosition("e8");
        board.setPieceAt("e4", whiteBishop);
        whiteBishop.setPosition("e4");
        // Bishop tries to move off the e-file, exposing king to check
        assertFalse(board.movePiece("e4", "d5"), "Bishop should NOT be able to move if it exposes king to check");
    }

    @Test
    public void testCannotCaptureOwnPiece() {
        Board board = new Board();
        board.clearBoard();
        Rook rook = new Rook("White", "a1");
        Pawn pawn = new Pawn("White", "a4");
        board.setPieceAt("a1", rook);
        rook.setPosition("a1");
        board.setPieceAt("a4", pawn);
        pawn.setPosition("a4");
        assertFalse(rook.isValidMove("a4", board), "Rook should NOT be able to capture own piece at a4");
    }
}
