package com.ddemott.chessai;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {
	@Test
	void testPlayerInitialization() {
		Player whitePlayer = new Player("Alice", Side.WHITE);
		Player blackPlayer = new Player("Bob", Side.BLACK);
		assertEquals("Alice", whitePlayer.getName());
		assertEquals("White", whitePlayer.getColor());
		assertEquals("Bob", blackPlayer.getName());
		assertEquals("Black", blackPlayer.getColor());
	}

	@Test
	void testPlayerNameGetter() {
		Player player = new Player("Charlie", Side.WHITE);
		assertEquals("Charlie", player.getName());
	}

	@Test
	void testPlayerColorGetter() {
		Player player = new Player("Dana", Side.BLACK);
		assertEquals("Black", player.getColor());
	}

	@Test
	void testNullName() {
		Player player = new Player(null, Side.WHITE);
		assertNull(player.getName());
		assertEquals("White", player.getColor());
	}

	@Test
	void testNullColor() {
		Player player = new Player("Eve", null);
		assertEquals("Eve", player.getName());
		assertThrows(NullPointerException.class, () -> player.getColor());
		// Assuming getColor() calls side.toString() which throws NPE if side is null
	}
}
