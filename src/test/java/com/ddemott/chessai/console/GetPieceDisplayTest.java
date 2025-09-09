package com.ddemott.chessai.console;

import com.ddemott.chessai.pieces.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GetPieceDisplayTest {
    @Test
    void testGetPieceDisplay() {
        EnhancedConsoleDisplay display = new EnhancedConsoleDisplay(null); // null state is fine for this test
        IPiece whiteQueen = new Queen("White", "d1");
        IPiece blackKnight = new Knight("Black", "g8");
        IPiece nullPiece = null;

        String whiteDisplay = display.getPieceDisplay(whiteQueen);
        String blackDisplay = display.getPieceDisplay(blackKnight);
        String emptyDisplay = display.getPieceDisplay(nullPiece);

        assertTrue(whiteDisplay.contains("Q"), "White Queen should display 'Q'");
        assertTrue(blackDisplay.toLowerCase().contains("n"), "Black Knight should display 'n'");
        assertEquals(" . ", emptyDisplay, "Null piece should display as ' . '");
    }
}
