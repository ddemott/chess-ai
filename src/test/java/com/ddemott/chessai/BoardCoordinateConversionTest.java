package com.ddemott.chessai;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BoardCoordinateConversionTest {
	@Test
	void testConvertPositionToCoordinatesInvalid() {
		Board board = new Board();
		assertNull(board.convertPositionToCoordinates("i1"));
		assertNull(board.convertPositionToCoordinates("a0"));
		assertNull(board.convertPositionToCoordinates("z9"));
		assertNull(board.convertPositionToCoordinates(""));
		assertNull(board.convertPositionToCoordinates(null));
	}

	@Test
	void testConvertCoordinatesToPositionValid() {
		Board board = new Board();
		assertEquals("a1", board.convertCoordinatesToPosition(0, 0));
		assertEquals("h8", board.convertCoordinatesToPosition(7, 7));
		assertEquals("e2", board.convertCoordinatesToPosition(1, 4));
		assertEquals("d7", board.convertCoordinatesToPosition(6, 3));
	}

	@Test
	void testConvertCoordinatesToPositionInvalid() {
		Board board = new Board();
		assertNull(board.convertCoordinatesToPosition(-1, 0));
		assertNull(board.convertCoordinatesToPosition(0, -1));
		assertNull(board.convertCoordinatesToPosition(8, 0));
		assertNull(board.convertCoordinatesToPosition(0, 8));
		assertNull(board.convertCoordinatesToPosition(100, 100));
	}
}
