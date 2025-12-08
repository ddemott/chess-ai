import com.ddemott.chessai.*;
import com.ddemott.chessai.pieces.*;
import com.ddemott.chessai.engine.*;

public class TestDiscoveredCheck {
    public static void main(String[] args) {
        GameEngine engine = new GameEngine(3);
        Board board = engine.getGameState().getBoard();
        board.clearBoard();
        board.setPieceAt("e1", new King(Side.WHITE, "e1"));
        board.setPieceAt("e5", new Bishop(Side.BLACK, "e5"));
        board.setPieceAt("e3", new Knight(Side.BLACK, "e3"));
        board.setPieceAt("a5", new King(Side.BLACK, "a5"));

        engine.getGameState().setCurrentTurn("Black");
        System.out.println("Initial: isWhiteInCheck=" + board.isKingInCheck(Side.WHITE));
        boolean ok = engine.movePiece("e3","c4");
        System.out.println("Move knight e3->c4 returned: " + ok);
        System.out.println("After move: isWhiteInCheck=" + board.isKingInCheck(Side.WHITE));
    }
}
