import com.ddemott.chessai.*;
import com.ddemott.chessai.pieces.*;
public class PrintEscapesLegals {
  public static void main(String[] args){
    Board b = new Board(); b.clearBoard();
    b.setPieceAt("e8", new King(Side.BLACK, "e8"));
    b.setPieceAt("d7", new Pawn(Side.BLACK, "d7"));
    b.setPieceAt("e7", new Pawn(Side.BLACK, "e7"));
    b.setPieceAt("f7", new Pawn(Side.BLACK, "f7"));
    b.setPieceAt("f8", new Bishop(Side.BLACK, "f8"));
    b.setPieceAt("f6", new Knight(Side.WHITE, "f6"));
    b.setPieceAt("e1", new Rook(Side.WHITE, "e1"));
    b.setPieceAt("a1", new King(Side.WHITE, "a1"));
    System.out.println(b.getBoardRepresentation());
    System.out.println("Black in check? " + b.isKingInCheck(Side.BLACK));
    for (int r=0;r<8;r++) for(int c=0;c<8;c++){
      String from = b.convertCoordinatesToPosition(r,c);
      IPiece p = b.getPieceAt(from);
      if(p!=null && p.getSide()==Side.BLACK){
        System.out.println("Black piece: " + p.getClass().getSimpleName() + " at " + from);
        for(int rr=0; rr<8; rr++) for(int cc=0; cc<8; cc++){
          String to = b.convertCoordinatesToPosition(rr,cc);
          String old = p.getPosition(); p.setPosition(from);
          boolean valid = p.isValidMove(to,b);
          p.setPosition(old);
          if(valid){
            Board cloned = b.clone(); IPiece cp = cloned.getPieceAt(from); if(cp!=null){ cp.setPosition(to); cloned.setPieceAt(to,cp); cloned.setPieceAt(from,null); boolean kingInCheck=cloned.isKingInCheck(Side.BLACK); System.out.println("  Move: " + from + "->" + to + ", kingInCheckAfter=" + kingInCheck); }
          }
        }
      }
    }
  }
}
