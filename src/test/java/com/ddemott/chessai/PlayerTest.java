package com.ddemott.chessai;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {
    @Test
    void testPlayerInitialization() {
        Player whitePlayer = new Player("Alice", "White");
        Player blackPlayer = new Player("Bob", "Black");
        assertEquals("Alice", whitePlayer.getName());
        assertEquals("White", whitePlayer.getColor());
        assertEquals("Bob", blackPlayer.getName());
        assertEquals("Black", blackPlayer.getColor());
    }

    @Test
    void testPlayerNameGetter() {
        Player player = new Player("Charlie", "White");
        assertEquals("Charlie", player.getName());
    }

    @Test
    void testPlayerColorGetter() {
        Player player = new Player("Dana", "Black");
        assertEquals("Black", player.getColor());
    }

    @Test
    void testNullName() {
        Player player = new Player(null, "White");
        assertNull(player.getName());
        assertEquals("White", player.getColor());
    }

    @Test
    void testNullColor() {
        Player player = new Player("Eve", null);
        assertEquals("Eve", player.getName());
        assertNull(player.getColor());
    }
}
