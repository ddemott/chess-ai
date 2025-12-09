import com.ddemott.chessai.*;
import com.ddemott.chessai.pieces.*;
public class DebugStalemate {
  public static void main(String[] args) {
    GameEngine engine = new GameEngine(1);
    Board b = engine.getGameState().getBoard();
    b.clearBoard();
    b.setPieceAt("a8", new King(Side.BLACK, "a8"));
    b.setPieceAt("a7", new Pawn(Side.WHITE, "a7"));
    b.setPieceAt("b7", new Queen(Side.WHITE, "b7"));
    b.setPieceAt("c6", new Bishop(Side.WHITE, "c6"));
    b.setPieceAt("e1", new King(Side.WHITE, "e1"));
    System.out.println(b.getBoardRepresentation());
    System.out.println("Black in check? " + b.isKingInCheck(Side.BLACK));
    System.out.println("Black isStalemate? " + b.isStalemate(Side.BLACK));
    // Enumerate black moves leading to still in check or not
    for (int r=0; r<8; r++){
      for (int c=0;c<8;c++){
        String from = b.convertCoordinatesToPosition(r,c);
        IPiece p = b.getPieceAt(from);
        if (p!=null && p.getSide()==Side.BLACK){
          System.out.println("Black piece:" + p.getClass().getSimpleName() + " at " + from);
          for (int rr=0; rr<8; rr++){
            for (int cc=0; cc<8; cc++){
              String to = b.convertCoordinatesToPosition(rr,cc);
              String old = p.getPosition(); p.setPosition(from);
              boolean val = p.isValidMove(to, b);
              p.setPosition(old);
              if(val){
                Board clone = b.clone(); IPiece cp = clone.getPieceAt(from); if (cp!=null){ cp.setPosition(to); clone.setPieceAt(to,cp); clone.setPieceAt(from,null); boolean kingInCheck = clone.isKingInCheck(Side.BLACK); System.out.println("  Valid move " + from + "->"+to + ", kingInCheckAfter=" + kingInCheck); }
              }
            }
          }
        }
      }
    }
  }
}
