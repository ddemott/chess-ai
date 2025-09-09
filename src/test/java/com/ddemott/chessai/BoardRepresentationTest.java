package com.ddemott.chessai;

import com.ddemott.chessai.pieces.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BoardRepresentationTest {
    @Test
    void testInitialBoardRepresentation() {
        Board board = new Board();
        String rep = board.getBoardRepresentation();
        System.out.println("\n--- Board Representation ---\n" + rep + "\n---------------------------\n");
        // Check header and footer
        assertTrue(rep.startsWith("  a b c d e f g h\n"));
        assertTrue(rep.endsWith("  a b c d e f g h\n"));
        // Check top row (black pieces)
        String[] lines = rep.split("\n");
        assertTrue(lines[1].contains("r n b q k b n r")); // Black pieces
        // Check bottom row (white pieces)
        assertTrue(lines[8].contains("R N B Q K B N R")); // White pieces
        // Check pawns
        assertTrue(lines[2].contains("p p p p p p p p")); // Black pawns
        assertTrue(lines[7].contains("P P P P P P P P")); // White pawns
        // Check empty squares
        assertTrue(lines[3].contains(". . . . . . . ."));
    }

    @Test
    void testBoardRepresentationAfterMove() {
        Board board = new Board();
        board.movePiece("e2", "e4");
        String rep = board.getBoardRepresentation();
        System.out.println("\n--- Board After Move ---\n" + rep + "\n-----------------------\n");
        String[] lines = rep.split("\n");
    // e4 should be a white pawn, e2 should be empty
    // e4 is row 4, col 4 (since row 7 is top, row 0 is bottom)
    // Each line: rowNum [piece] [piece] ... [piece] rowNum
        for (int i = 1; i <= 8; i++) {
            String[] row = lines[i].split(" ");
            System.out.println("row " + (8 - (i - 1)) + ": " + java.util.Arrays.toString(row));
        }
        // e4 is row 4, col 5 (chess notation), which is lines[5], index 5
        String[] row4 = lines[5].split(" "); // row 4
        String[] row2 = lines[7].split(" "); // row 2
        assertEquals("P", row4[5]); // e4
        assertEquals(".", row2[5]); // e2
    }

    @Test
    void testBoardRepresentationAllEmpty() {
        Board board = new Board();
        // Remove all pieces
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                board.setPieceAt(board.convertCoordinatesToPosition(row, col), null);
            }
        }
        String rep = board.getBoardRepresentation();
        for (int i = 1; i <= 8; i++) {
            assertTrue(rep.contains(i + " . . . . . . . . " + i));
        }
    }
}
