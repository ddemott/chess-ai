import com.ddemott.chessai.*;
import com.ddemott.chessai.pieces.*;
import com.ddemott.chessai.engine.*;
public class DebugRookMove {
    public static void main(String[] args) {
        GameEngine engine = new GameEngine(1);
        Board b = engine.getGameState().getBoard();
        b.clearBoard();
        b.setPieceAt("h8", new King(Side.BLACK, "h8"));
        b.setPieceAt("g7", new Rook(Side.WHITE, "g7"));
        b.setPieceAt("f7", new Knight(Side.WHITE, "f7"));
        b.setPieceAt("a1", new King(Side.WHITE, "a1"));
        IPiece rook = b.getPieceAt("g7");
        System.out.println("Rook pos: " + rook.getPosition());
        System.out.println("Rook can move to h8? " + rook.isValidMove("h8", b));
        System.out.println("Rook all moves: " + rook.getAllPossibleMoves(b));
    }
}
