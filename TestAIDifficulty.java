import com.ddemott.chessai.ai.AIDifficulty;
import com.ddemott.chessai.ai.MinMaxStrategy;

public class TestAIDifficulty {
    public static void main(String[] args) {
        System.out.println("Testing AI Difficulty Levels:");
        System.out.println("============================");

        for (AIDifficulty diff : AIDifficulty.values()) {
            System.out.println("Difficulty: " + diff.getDisplayName() +
                             " (Depth: " + diff.getDepth() + ")");
        }

        System.out.println("\nTesting MinMaxStrategy with different depths:");
        System.out.println("=============================================");

        // Test each difficulty level
        for (AIDifficulty diff : AIDifficulty.values()) {
            MinMaxStrategy strategy = new MinMaxStrategy(diff.getDepth());
            System.out.println("Strategy depth: " + diff.getDepth() +
                             " -> MinMaxStrategy maxDepth: " + diff.getDepth());
        }
    }
}