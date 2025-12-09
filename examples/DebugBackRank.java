import com.ddemott.chessai.*;
import com.ddemott.chessai.pieces.*;

public class DebugBackRank {
    public static void main(String[] args) {
        Board b = new Board();
        b.clearBoard();
        b.setPieceAt("g8", new King(Side.BLACK, "g8"));
        b.setPieceAt("f7", new Pawn(Side.BLACK, "f7"));
        b.setPieceAt("g7", new Pawn(Side.BLACK, "g7"));
        b.setPieceAt("h7", new Pawn(Side.BLACK, "h7"));
        b.setPieceAt("a8", new Queen(Side.WHITE, "a8"));
        b.setPieceAt("e1", new King(Side.WHITE, "e1"));
        System.out.println(b.getBoardRepresentation());
        System.out.println("Black in check? " + b.isKingInCheck(Side.BLACK));
    }
}
