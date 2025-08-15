package com.ddemott.chessai;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import com.ddemott.chessai.ai.AIDifficulty;
import com.ddemott.chessai.console.AIvsAIChessGame;
import com.ddemott.chessai.engine.GameEngine;

/**
 * JUnit 5 test suite for AI vs AI functionality
 */
public class AIvsAITest {

    @Test
    void testAIvsAIGameCreation() {
        assertDoesNotThrow(() -> {
            AIvsAIChessGame game = new AIvsAIChessGame(AIDifficulty.BEGINNER, AIDifficulty.ADVANCED);
            assertNotNull(game);
        });
    }

    @Test
    void testDifferentDifficultiesCompete() {
        AIDifficulty[] difficulties = {AIDifficulty.BEGINNER, AIDifficulty.INTERMEDIATE, AIDifficulty.EXPERT};
        for (AIDifficulty white : difficulties) {
            for (AIDifficulty black : difficulties) {
                GameEngine engine = new GameEngine(white, black);
                assertNotNull(engine, "Engine should be created for " + white + " vs " + black);
            }
        }
    }

    @Test
    void testGameProgression() {
        GameEngine engine = new GameEngine(AIDifficulty.BEGINNER, AIDifficulty.EASY);
        int movesPlayed = 0;
        int maxMoves = 10;
        while (movesPlayed < maxMoves) {
            String currentPlayer = engine.getCurrentTurn();
            if (currentPlayer.equals("White")) {
                engine.setAIDifficulty(AIDifficulty.BEGINNER);
            } else {
                engine.setAIDifficulty(AIDifficulty.EASY);
            }
            String aiMove = engine.getBestMove();
            if (aiMove == null) break;
            String[] positions = aiMove.split(" ");
            assertEquals(2, positions.length, "AI move format should be 'from to'");
            boolean moveSuccess = engine.movePiece(positions[0], positions[1]);
            assertTrue(moveSuccess, "AI move should succeed: " + aiMove);
            movesPlayed++;
            String newPlayer = engine.getCurrentTurn();
            assertNotEquals(currentPlayer, newPlayer, "Turn should alternate");
        }
        assertTrue(movesPlayed > 0, "At least one move should be played");
    }

    @Test
    void testGameStateManagement() {
        GameEngine engine = new GameEngine(AIDifficulty.INTERMEDIATE, AIDifficulty.ADVANCED);
        String initialPlayer = engine.getCurrentTurn();
        int initialMoveCount = engine.getMoveHistory().getMoves().size();
        engine.makeAIMove();
        String newPlayer = engine.getCurrentTurn();
        int newMoveCount = engine.getMoveHistory().getMoves().size();
        assertNotEquals(initialPlayer, newPlayer, "Turn should change after AI move");
        assertEquals(initialMoveCount + 1, newMoveCount, "Move count should increment");
    }

    @Test
    void testAISwitching() {
        GameEngine engine = new GameEngine(AIDifficulty.BEGINNER);
        assertEquals(AIDifficulty.BEGINNER, engine.getAIDifficulty(), "Initial AI difficulty should be BEGINNER");
        engine.setAIDifficulty(AIDifficulty.EXPERT);
        assertEquals(AIDifficulty.EXPERT, engine.getAIDifficulty(), "AI difficulty should switch to EXPERT");
        String aiMove = engine.getBestMove();
        assertNotNull(aiMove, "AI should be able to make a move after switching difficulty");
        assertFalse(aiMove.isEmpty(), "AI move should not be empty");
    }
}
