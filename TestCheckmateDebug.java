import com.ddemott.chessai.*;
import com.ddemott.chessai.pieces.*;
import com.ddemott.chessai.engine.*;

public class TestCheckmateDebug {
    public static void main(String[] args) {
        // Test the back-rank mate scenario
        GameEngine engine = new GameEngine(1);
        Board board = engine.getGameState().getBoard();
        board.clearBoard();
        
        // Set up a back-rank mate
        board.setPieceAt("g8", new King("Black", "g8"));
        board.setPieceAt("f7", new Pawn("Black", "f7"));
        board.setPieceAt("g7", new Pawn("Black", "g7"));
        board.setPieceAt("h7", new Pawn("Black", "h7"));
        board.setPieceAt("a8", new Queen("White", "a8"));
        board.setPieceAt("e1", new King("White", "e1"));
        
        System.out.println("=== Back-rank mate test ===");
        System.out.println("Is Black in check? " + board.isKingInCheck(Side.BLACK));
        
        // Check if queen can actually attack the king
        IPiece queen = board.getPieceAt("a8");
        System.out.println("Queen at a8, position field: " + queen.getPosition());
        System.out.println("Queen.isValidMove('g8', board): " + queen.isValidMove("g8", board));
        
        // Check king's possible escapes
        IPiece king = board.getPieceAt("g8");
        System.out.println("King at g8, position field: " + king.getPosition());
        System.out.println("King can move to f8? " + king.isValidMove("f8", board));
        System.out.println("King can move to h8? " + king.isValidMove("h8", board));
        
        System.out.println("\nIs Black in checkmate? " + board.isCheckmate(Side.BLACK));
    }
}
