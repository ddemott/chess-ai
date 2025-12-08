import com.ddemott.chessai.*;
import com.ddemott.chessai.pieces.*;

public class test_checkmate {
    public static void main(String[] args) {
        Board board = new Board();
        board.clearBoard();
        
        // Set up a simple back-rank mate
        board.setPieceAt("g8", new King("Black", "g8"));
        board.setPieceAt("f7", new Pawn("Black", "f7"));
        board.setPieceAt("g7", new Pawn("Black", "g7"));
        board.setPieceAt("h7", new Pawn("Black", "h7"));
        board.setPieceAt("a8", new Queen("White", "a8"));
        board.setPieceAt("e1", new King("White", "e1"));
        
        System.out.println("Is Black in check? " + board.isKingInCheck("Black"));
        System.out.println("Is Black in checkmate? " + board.isCheckmate("Black"));
        
        // Test if the queen can actually attack the king
        IPiece queen = board.getPieceAt("a8");
        System.out.println("Queen position: " + queen.getPosition());
        System.out.println("Queen can move to g8? " + queen.isValidMove("g8", board));
    }
}
