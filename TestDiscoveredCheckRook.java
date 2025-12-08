import com.ddemott.chessai.*;
import com.ddemott.chessai.pieces.*;
public class TestDiscoveredCheckRook {
    public static void main(String[] args) {
        Board b = new Board();
        b.clearBoard();
        b.setPieceAt("e8", new King(Side.BLACK, "e8"));
        b.setPieceAt("e1", new Rook(Side.WHITE, "e1"));
        b.setPieceAt("e4", new Bishop(Side.WHITE, "e4"));
        System.out.println("initial check? " + b.isKingInCheck(Side.BLACK));
        b.setPieceAt("e4", null);
        System.out.println("after clearing e4, check? " + b.isKingInCheck(Side.BLACK));
    }
}
