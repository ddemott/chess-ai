import com.ddemott.chessai.*;
import com.ddemott.chessai.pieces.*;
import com.ddemott.chessai.engine.*;

public class TestDiscoveredCheck2 {
    public static void main(String[] args) {
        GameEngine engine = new GameEngine(3);
        Board board = engine.getGameState().getBoard();
        board.clearBoard();
        board.setPieceAt("e1", new King(Side.WHITE, "e1"));
        board.setPieceAt("e5", new Bishop(Side.BLACK, "e5"));
        board.setPieceAt("e3", new Knight(Side.BLACK, "e3"));
        board.setPieceAt("a5", new King(Side.BLACK, "a5"));

        IPiece bishop = board.getPieceAt("e5");
        System.out.println("Bishop moves: " + bishop.getAllPossibleMoves(board));
        System.out.println("Is initial check? " + board.isKingInCheck(Side.WHITE));
        
        engine.getGameState().setCurrentTurn("Black");
        boolean ok = engine.movePiece("e3","c4");
        System.out.println("Moved knight: " + ok);
        System.out.println("Bishop moves after knight moved: " + bishop.getAllPossibleMoves(board));
        System.out.println("Is after move check? " + board.isKingInCheck(Side.WHITE));
    }
}
