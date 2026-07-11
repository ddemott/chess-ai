import com.ddemott.chessai.ai.AIDifficulty;
import com.ddemott.chessai.ai.MinMaxStrategy;
import com.ddemott.chessai.State;
import com.ddemott.chessai.Board;

public class AIDifficultyDemo {
    public static void main(String[] args) {
        System.out.println("Chess AI Difficulty Analysis");
        System.out.println("============================");

        // Show all difficulty levels
        System.out.println("Available AI Difficulty Levels:");
        for (AIDifficulty diff : AIDifficulty.values()) {
            System.out.println("  " + diff.getDisplayName() +
                             " (Depth: " + diff.getDepth() + ")");
        }

        System.out.println("\nAI Strategy Depth Analysis:");
        System.out.println("===========================");

        // Test each difficulty level
        for (AIDifficulty diff : AIDifficulty.values()) {
            int depth = diff.getDepth();
            MinMaxStrategy strategy = new MinMaxStrategy(depth);
            System.out.println("Difficulty: " + diff.getDisplayName() +
                             " -> Strategy Max Depth: " + depth);
        }

        // Test that fromDepth works correctly
        System.out.println("\nTesting fromDepth method:");
        System.out.println("========================");

        for (int i = 0; i <= 7; i++) {
            AIDifficulty diff = AIDifficulty.fromDepth(i);
            System.out.println("Depth " + i + " -> " + diff.getDisplayName());
        }
    }
}