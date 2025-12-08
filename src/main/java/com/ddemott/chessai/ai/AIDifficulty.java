package com.ddemott.chessai.ai;

/**
 * Enum representing different AI difficulty levels
 */
public enum AIDifficulty {
	BEGINNER(1, "Beginner", "Very weak, makes basic moves"), EASY(2, "Easy", "Weak, looks ahead 2 moves"), INTERMEDIATE(
			3, "Intermediate", "Moderate difficulty, looks ahead 3 moves"), ADVANCED(4, "Advanced",
					"Strong, looks ahead 4 moves"), EXPERT(5, "Expert", "Very strong, looks ahead 5 moves"), MASTER(6,
							"Master", "Extremely strong, looks ahead 6 moves");

	private final int depth;
	private final String displayName;
	private final String description;

	AIDifficulty(int depth, String displayName, String description) {
		this.depth = depth;
		this.displayName = displayName;
		this.description = description;
	}

	public int getDepth() {
		return depth;
	}

	public String getDisplayName() {
		return displayName;
	}

	public String getDescription() {
		return description;
	}

	@Override
	public String toString() {
		return displayName + " (Depth " + depth + "): " + description;
	}

	/**
	 * Get difficulty level by depth
	 */
	public static AIDifficulty fromDepth(int depth) {
		for (AIDifficulty difficulty : values()) {
			if (difficulty.depth == depth) {
				return difficulty;
			}
		}
		// Default to closest match
		if (depth <= 1)
			return BEGINNER;
		if (depth >= 6)
			return MASTER;
		return values()[Math.min(depth - 1, values().length - 1)];
	}

	/**
	 * Get all difficulty levels for display
	 */
	public static String getAllDifficulties() {
		StringBuilder sb = new StringBuilder();
		sb.append("Available AI Difficulty Levels:\n");
		for (AIDifficulty diff : values()) {
			sb.append("  ").append(diff.ordinal() + 1).append(". ").append(diff).append("\n");
		}
		return sb.toString();
	}
}
