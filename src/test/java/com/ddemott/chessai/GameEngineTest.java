package com.ddemott.chessai;

import com.ddemott.chessai.engine.GameEngine;
import com.ddemott.chessai.ai.AIDifficulty;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit 5 test suite for GameEngine and State core functionality.
 */
public class GameEngineTest {

	@Test
	void testInitialState() {
		GameEngine engine = new GameEngine(3);
		assertEquals("White", engine.getCurrentTurn(), "Initial turn should be White");
	}

	@Test
	void testBasicMove() {
		GameEngine engine = new GameEngine(3);
		boolean moveResult = engine.movePiece("e2", "e4");
		assertTrue(moveResult, "Move e2-e4 should succeed");
		assertEquals("Black", engine.getCurrentTurn(), "Turn should switch to Black after White moves");
	}

	@Test
	void testAIMoveGenerationAndExecution() {
		GameEngine engine = new GameEngine(3);
		String aiMove = engine.getBestMove();
		assertNotNull(aiMove, "AI should suggest a move");
		assertFalse(aiMove.isEmpty(), "AI move should not be empty");
		String currentTurn = engine.getCurrentTurn();
		engine.makeAIMove();
		String expectedTurn = currentTurn.equals("Black") ? "White" : "Black";
		assertEquals(expectedTurn, engine.getCurrentTurn(), "Turn should switch after AI move");
	}

	@Test
	void testInvalidMoves() {
		GameEngine engine = new GameEngine(3);
		// Try to move a piece that doesn't exist
		boolean invalidMove1 = engine.movePiece("a3", "a4");
		assertFalse(invalidMove1, "Invalid move a3-a4 (no piece) should be rejected");
		// Try to move opponent's piece
		String opponentMove = engine.getCurrentTurn().equals("White") ? "e7 e5" : "e2 e4";
		String[] opponentPos = opponentMove.split(" ");
		boolean invalidMove2 = engine.movePiece(opponentPos[0], opponentPos[1]);
		assertFalse(invalidMove2, "Invalid move (opponent's piece) should be rejected");
	}

	@Test
	void testBoardStateAccess() {
		GameEngine engine = new GameEngine(2);
		String boardState = engine.getBoardRepresentation();
		assertNotNull(boardState, "Board state string should not be null");
		assertTrue(boardState.contains("a b c d e f g h"), "Board state should contain column headers");
		State gameState = engine.getGameState();
		assertNotNull(gameState, "Game state should be accessible");
		assertEquals(engine.getCurrentTurn(), gameState.getCurrentTurn(),
				"Current turn should match between engine and state");
	}

	@Test
	void testMoveHistoryAndUndoRedo() {
		GameEngine engine = new GameEngine(3);
		assertTrue(engine.movePiece("e2", "e4"));
		assertTrue(engine.movePiece("e7", "e5"));
		assertTrue(engine.undoLastMove(), "Undo should succeed");
		assertEquals("Black", engine.getCurrentTurn(), "Turn should revert to Black after undo");
		assertTrue(engine.redoLastMove(), "Redo should succeed");
		assertEquals("White", engine.getCurrentTurn(), "Turn should revert to White after redo");
	}

	@Test
	void testAIDifficultySwitching() {
		GameEngine engine = new GameEngine(AIDifficulty.BEGINNER);
		assertEquals(AIDifficulty.BEGINNER, engine.getAIDifficulty());
		engine.setAIDifficulty(AIDifficulty.EXPERT);
		assertEquals(AIDifficulty.EXPERT, engine.getAIDifficulty());
	}

	@Test
	void testPGNExportAndImport() {
		GameEngine engine = new GameEngine(3);
		engine.movePiece("e2", "e4");
		engine.movePiece("e7", "e5");
		String pgn = engine.exportGameToPGN("WhitePlayer", "BlackPlayer", "*");
		assertNotNull(pgn, "PGN export should not be null");
		assertTrue(pgn.contains("e4"), "PGN should contain move e4");
		assertTrue(pgn.contains("e5"), "PGN should contain move e5");
		// Test import (simulate saving and loading)
		boolean loaded = engine.loadGameFromPGNData(new com.ddemott.chessai.MoveHistory.PGNGameData());
		assertTrue(loaded, "Loading empty PGN data should succeed (no moves)");
	}
}
