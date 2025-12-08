import com.ddemott.chessai.*;
import com.ddemott.chessai.pieces.*;
public class DebugH1Stalemate {
  public static void main(String[] args) {
    Board b = new Board(); b.clearBoard();
    b.setPieceAt("h1", new King(Side.WHITE, "h1"));
    b.setPieceAt("f2", new King(Side.BLACK, "f2"));
    b.setPieceAt("g2", new Queen(Side.BLACK, "g2"));
    System.out.println(b.getBoardRepresentation());
    System.out.println("White king possible moves: " + ((com.ddemott.chessai.pieces.King)b.getPieceAt("h1")).getAllPossibleMoves(b));
    System.out.println("isStalemate(White): " + b.isStalemate(Side.WHITE));
  }
}
