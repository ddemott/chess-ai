package com.ddemott.chessai;

import com.ddemott.chessai.ai.AIDifficulty;
import com.ddemott.chessai.engine.GameEngine;

/**
 * Test suite for AI difficulty levels and related functionality
 */
public class AIDifficultyTest {

	public static void main(String[] args) {
		System.out.println("=== AI Difficulty Test Suite ===\n");

		int totalTests = 0;
		int passedTests = 0;

		// Test 1: Difficulty enum values
		totalTests++;
		if (testDifficultyEnumValues()) {
			passedTests++;
			System.out.println("✅ Test 1: Difficulty Enum Values - PASSED");
		} else {
			System.out.println("❌ Test 1: Difficulty Enum Values - FAILED");
		}

		// Test 2: Depth mapping
		totalTests++;
		if (testDepthMapping()) {
			passedTests++;
			System.out.println("✅ Test 2: Depth Mapping - PASSED");
		} else {
			System.out.println("❌ Test 2: Depth Mapping - FAILED");
		}

		// Test 3: GameEngine constructor with difficulty
		totalTests++;
		if (testGameEngineConstructor()) {
			passedTests++;
			System.out.println("✅ Test 3: GameEngine Constructor - PASSED");
		} else {
			System.out.println("❌ Test 3: GameEngine Constructor - FAILED");
		}

		// Test 4: Dynamic difficulty change
		totalTests++;
		if (testDynamicDifficultyChange()) {
			passedTests++;
			System.out.println("✅ Test 4: Dynamic Difficulty Change - PASSED");
		} else {
			System.out.println("❌ Test 4: Dynamic Difficulty Change - FAILED");
		}

		// Test 5: AI move quality at different levels
		totalTests++;
		if (testAIMoveQuality()) {
			passedTests++;
			System.out.println("✅ Test 5: AI Move Quality - PASSED");
		} else {
			System.out.println("❌ Test 5: AI Move Quality - FAILED");
		}

		// Test 6: Difficulty string representations
		totalTests++;
		if (testDifficultyStrings()) {
			passedTests++;
			System.out.println("✅ Test 6: Difficulty Strings - PASSED");
		} else {
			System.out.println("❌ Test 6: Difficulty Strings - FAILED");
		}

		System.out.println("\\n=== Test Summary ===");
		System.out.println("Tests Passed: " + passedTests + "/" + totalTests);
		if (passedTests == totalTests) {
			System.out.println("🎉 ALL AI DIFFICULTY TESTS PASSED!");
		} else {
			System.out.println("⚠️  Some tests failed - check implementation");
		}
	}

	private static boolean testDifficultyEnumValues() {
		try {
			// Test that all expected difficulties exist
			AIDifficulty[] difficulties = AIDifficulty.values();

			return difficulties.length == 6 && difficulties[0] == AIDifficulty.BEGINNER
					&& difficulties[1] == AIDifficulty.EASY && difficulties[2] == AIDifficulty.INTERMEDIATE
					&& difficulties[3] == AIDifficulty.ADVANCED && difficulties[4] == AIDifficulty.EXPERT
					&& difficulties[5] == AIDifficulty.MASTER;
		} catch (Exception e) {
			System.out.println("Exception in enum values test: " + e.getMessage());
			return false;
		}
	}

	private static boolean testDepthMapping() {
		try {
			// Test depth mappings
			return AIDifficulty.BEGINNER.getDepth() == 1 && AIDifficulty.EASY.getDepth() == 2
					&& AIDifficulty.INTERMEDIATE.getDepth() == 3 && AIDifficulty.ADVANCED.getDepth() == 4
					&& AIDifficulty.EXPERT.getDepth() == 5 && AIDifficulty.MASTER.getDepth() == 6;
		} catch (Exception e) {
			System.out.println("Exception in depth mapping test: " + e.getMessage());
			return false;
		}
	}

	private static boolean testGameEngineConstructor() {
		try {
			// Test new constructor with difficulty enum
			GameEngine engine1 = new GameEngine(AIDifficulty.INTERMEDIATE);
			AIDifficulty difficulty1 = engine1.getAIDifficulty();

			// Test original constructor still works
			GameEngine engine2 = new GameEngine(3);
			AIDifficulty difficulty2 = engine2.getAIDifficulty();

			return difficulty1 == AIDifficulty.INTERMEDIATE && difficulty2.getDepth() == 3;
		} catch (Exception e) {
			System.out.println("Exception in constructor test: " + e.getMessage());
			return false;
		}
	}

	private static boolean testDynamicDifficultyChange() {
		try {
			GameEngine engine = new GameEngine(AIDifficulty.BEGINNER);

			// Initial difficulty
			if (engine.getAIDifficulty() != AIDifficulty.BEGINNER) {
				return false;
			}

			// Change difficulty
			engine.setAIDifficulty(AIDifficulty.EXPERT);
			if (engine.getAIDifficulty() != AIDifficulty.EXPERT) {
				return false;
			}

			// Change again
			engine.setAIDifficulty(AIDifficulty.INTERMEDIATE);
			return engine.getAIDifficulty() == AIDifficulty.INTERMEDIATE;

		} catch (Exception e) {
			System.out.println("Exception in dynamic change test: " + e.getMessage());
			return false;
		}
	}

	private static boolean testAIMoveQuality() {
		try {
			// Test that AI can make valid moves at all difficulty levels
			for (AIDifficulty difficulty : AIDifficulty.values()) {
				GameEngine engine = new GameEngine(difficulty);

				// Make a human move first
				boolean humanMoveSuccess = engine.movePiece("e2", "e4");
				if (!humanMoveSuccess) {
					System.out.println("Failed to make human move at difficulty: " + difficulty);
					return false;
				}

				// Let AI make a move
				String aiMove = engine.getBestMove();
				if (aiMove == null || aiMove.isEmpty()) {
					System.out.println("AI failed to generate move at difficulty: " + difficulty);
					return false;
				}

				// Verify move format
				String[] parts = aiMove.split(" ");
				if (parts.length != 2) {
					System.out.println("Invalid AI move format at difficulty: " + difficulty + " - " + aiMove);
					return false;
				}
			}

			return true;
		} catch (Exception e) {
			System.out.println("Exception in AI move quality test: " + e.getMessage());
			return false;
		}
	}

	private static boolean testDifficultyStrings() {
		try {
			// Test toString and display methods
			String beginnerString = AIDifficulty.BEGINNER.toString();
			String allDifficulties = AIDifficulty.getAllDifficulties();

			return beginnerString.contains("Beginner") && beginnerString.contains("Depth 1")
					&& allDifficulties.contains("Available AI Difficulty Levels")
					&& allDifficulties.contains("Beginner") && allDifficulties.contains("Master");
		} catch (Exception e) {
			System.out.println("Exception in string test: " + e.getMessage());
			return false;
		}
	}

	/**
	 * Performance comparison demo - shows that higher difficulty takes longer
	 */
	public static void performanceDemo() {
		System.out.println("\\n=== AI Performance Demo ===");
		System.out.println("Comparing AI thinking time at different difficulty levels...");

		AIDifficulty[] testDifficulties = {AIDifficulty.BEGINNER, AIDifficulty.INTERMEDIATE, AIDifficulty.EXPERT};

		for (AIDifficulty difficulty : testDifficulties) {
			System.out.println("\\nTesting " + difficulty.getDisplayName() + " (Depth " + difficulty.getDepth() + "):");

			GameEngine engine = new GameEngine(difficulty);
			engine.movePiece("e2", "e4"); // Setup position

			long startTime = System.currentTimeMillis();
			String aiMove = engine.getBestMove();
			long endTime = System.currentTimeMillis();

			System.out.println("  AI Move: " + aiMove);
			System.out.println("  Think Time: " + (endTime - startTime) + "ms");
		}
	}
}
