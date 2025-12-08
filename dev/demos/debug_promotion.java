import com.ddemott.chessai.engine.GameEngine;
import com.ddemott.chessai.pieces.King;
import com.ddemott.chessai.pieces.Pawn;
import com.ddemott.chessai.pieces.Queen;

public class debug_promotion {
    public static void main(String[] args) {
        GameEngine engine = new GameEngine(1);
        
        // Clear board
        engine.getGameState().getBoard().clearBoard();
        
        // Set up pawn promotion scenario exactly like the test
        engine.getGameState().getBoard().setPieceAt("e7", new Pawn("White", "e7"));
        engine.getGameState().getBoard().setPieceAt("e1", new King("White", "e1"));
        engine.getGameState().getBoard().setPieceAt("e8", new King("Black", "e8"));
        
        engine.getGameState().setCurrentTurn("White");
        
        System.out.println("Initial board:");
        engine.getGameState().getBoard().printBoard();
        
        // Check if the pawn can make the move
        System.out.println("Pawn can make move e7 to e8: " + 
                          engine.getGameState().getBoard().getPieceAt("e7").isValidMove("e8", engine.getGameState().getBoard()));
        
        // Check if this would expose king to check
        System.out.println("Would expose king to check: " + 
                          engine.getGameState().getBoard().wouldExposeKingToCheck("e7", "e8"));
        
        // Try promotion
        boolean promotionResult = engine.movePiece("e7", "e8", "Q");
        System.out.println("Promotion result: " + promotionResult);
        
        System.out.println("Final board:");
        engine.getGameState().getBoard().printBoard();
    }
}
