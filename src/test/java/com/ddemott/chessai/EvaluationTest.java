package com.ddemott.chessai;

import com.ddemott.chessai.pieces.King;
import com.ddemott.chessai.pieces.IPiece;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

class EvaluationTest {
    @Test
    void testToggleColor() {
        Evaluation eval = new Evaluation();
        assertEquals("Black", eval.toggleColor("White"));
        assertEquals("White", eval.toggleColor("Black"));
    }

    @Test
    void testFindKing() {
        // Minimal mock Board implementation for findKing
        class MockBoard extends Board {
            private IPiece[][] boardArray = new IPiece[8][8];
            @Override
            public IPiece[][] getBoardArray() { return boardArray; }
            public void setPiece(IPiece piece, int row, int col) { boardArray[row][col] = piece; }
        }
        MockBoard board = new MockBoard();
        King whiteKing = new King("White", "e1");
        King blackKing = new King("Black", "e8");
        board.setPiece(whiteKing, 7, 4); // e1
        board.setPiece(blackKing, 0, 4); // e8
        Evaluation eval = new Evaluation();
        assertEquals(whiteKing, eval.findKing(board, "White"));
        assertEquals(blackKing, eval.findKing(board, "Black"));
        assertNull(eval.findKing(board, "Red")); // Nonexistent color
    }

    @Test
    void testIsCastled() {
        Evaluation eval = new Evaluation();
        // Castled positions: g1, c1, g8, c8
        assertTrue(eval.isCastled("g1", null));
        assertTrue(eval.isCastled("c1", null));
        assertTrue(eval.isCastled("g8", null));
        assertTrue(eval.isCastled("c8", null));
        // Not castled
        assertFalse(eval.isCastled("e1", null));
        assertFalse(eval.isCastled("e8", null));
    }

    @Test
    void testIsPieceGuarded() {
        Evaluation eval = new Evaluation();
        // Minimal mock Board and Piece
        class MockPiece implements IPiece {
            private String color, position;
            public MockPiece(String color, String position) { this.color = color; this.position = position; }
            public String getColor() { return color; }
            public String getPosition() { return position; }
            public int getValue() { return 1; }
            public boolean hasMoved() { return false; }
            public void setHasMoved(boolean b) {}
            public void setPosition(String p) { position = p; }
            public java.util.List<String> getAllPossibleMoves(Board board) { return java.util.Arrays.asList(position + " f2"); }
            public IPiece clonePiece() { return new MockPiece(color, position); }
            public boolean isValidMove(String newPosition, Board board) { return false; }
        }
        class MockBoard extends Board {
            public IPiece getPieceAt(String pos) { return "f2".equals(pos) ? new MockPiece("White", "f2") : null; }
        }
        MockPiece piece = new MockPiece("White", "e2");
        MockBoard board = new MockBoard();
        assertTrue(eval.isPieceGuarded(piece, board));
    }

    @Test
    void testEvaluateKingSafety() {
        Evaluation eval = new Evaluation();
        class MockKing extends King {
            public MockKing(String color, String position) { super(color, position); }
        }
        class MockBoard extends Board {
            public int[] convertPositionToCoordinates(String pos) { return "e1".equals(pos) ? new int[]{7,4} : new int[]{0,0}; }
            public IPiece getPieceAt(String pos) { return null; }
        }
        MockKing king = new MockKing("White", "e1");
        MockBoard board = new MockBoard();
        int score = eval.evaluateKingSafety(board, "White");
        assertTrue(score <= 0); // Exposed king penalty
    }

    @Test
    void testEvaluatePawnShield() {
        Evaluation eval = new Evaluation();
        class MockKing extends King {
            public MockKing(String color, String position) { super(color, position); }
        }
        class MockPawn extends com.ddemott.chessai.pieces.Pawn {
            public MockPawn(String color, String position) { super(color, position); }
        }
        class MockBoard extends Board {
            public int[] convertPositionToCoordinates(String pos) {
                switch(pos) {
                    case "g1": return new int[]{7,6};
                    case "f2": return new int[]{6,5};
                    case "g2": return new int[]{6,6};
                    case "h2": return new int[]{6,7};
                    default: return new int[]{0,0};
                }
            }
            public IPiece getPieceAt(String pos) {
                if ("f2".equals(pos) || "g2".equals(pos) || "h2".equals(pos)) return new MockPawn("White", pos);
                return null;
            }
        }
        MockKing king = new MockKing("White", "g1");
        MockBoard board = new MockBoard();
        int score = eval.evaluatePawnShield(king, board);
        assertTrue(score > 0);
    }

    @Test
    void testIsInCheckAndCheckmate() {
        Evaluation eval = new Evaluation();
        // Use a more realistic mock for Board and King
        class MockKing extends King {
            public MockKing(String color, String position) { super(color, position); }
        }
        class MockBoard extends Board {
            @Override
            public IPiece[][] getBoardArray() {
                IPiece[][] arr = new IPiece[8][8];
                arr[0][4] = new MockKing("Black", "e8"); // Black king at e8
                arr[1][3] = new com.ddemott.chessai.pieces.Queen("White", "d7"); // White queen at d7
                return arr;
            }
            @Override
            public java.util.List<String> getAllPossibleMoves(String color) {
                if ("Black".equals(color)) return java.util.Collections.emptyList();
                return java.util.Arrays.asList("d7 e8"); // Queen attacks e8
            }
            @Override
            public IPiece getPieceAt(String pos) {
                if ("e8".equals(pos)) return new MockKing("Black", "e8");
                if ("d7".equals(pos)) return new com.ddemott.chessai.pieces.Queen("White", "d7");
                return null;
            }
            @Override
            public Board clone() { return this; }
            @Override
            public int[] convertPositionToCoordinates(String pos) {
                if ("e8".equals(pos)) return new int[]{0,4};
                if ("d7".equals(pos)) return new int[]{1,3};
                return new int[]{0,0};
            }
        }
        MockBoard board = new MockBoard();
        assertTrue(eval.isInCheck(board, "Black"));
        assertTrue(eval.isCheckmate(board, "Black"));
    }

    @Test
    void testEvaluateBoard() {
        Evaluation eval = new Evaluation();
        class MockPawn extends com.ddemott.chessai.pieces.Pawn {
            public MockPawn(String color, String position) { super(color, position); }
        }
        class MockBoard extends Board {
            private IPiece[][] arr = new IPiece[8][8];
            public MockBoard() { arr[6][4] = new MockPawn("White", "e2"); }
            public IPiece[][] getBoardArray() { return arr; }
            public IPiece getPieceAt(String pos) {
                if ("e2".equals(pos)) return arr[6][4];
                return null;
            }
            public java.util.List<String> getAllPossibleMoves(String color) { return java.util.Collections.emptyList(); }
            public int[] convertPositionToCoordinates(String pos) { return new int[]{6,4}; }
        }
        MockBoard board = new MockBoard();
        int score = eval.evaluateBoard(board, "White");
        assertTrue(score >= 0);
    }
}
