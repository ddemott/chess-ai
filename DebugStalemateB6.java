import com.ddemott.chessai.*;
import com.ddemott.chessai.pieces.*;

public class DebugStalemateB6 {
  public static void main(String[] args) {
    Board b = new Board();
    b.clearBoard();
    b.setPieceAt("a8", new King(Side.BLACK, "a8"));
    b.setPieceAt("a7", new Pawn(Side.WHITE, "a7"));
    b.setPieceAt("b6", new Queen(Side.WHITE, "b6"));
    b.setPieceAt("c6", new Bishop(Side.WHITE, "c6"));
    b.setPieceAt("c6", new King(Side.WHITE, "c6"));
    System.out.println(b.getBoardRepresentation());
    System.out.println("Black in check? " + b.isKingInCheck(Side.BLACK));
    System.out.println("Square a7 under attack? " + b.isSquareUnderAttack("a7", Side.BLACK));
    // Show which white pieces are attacking a8
    for (int r=0; r<8; r++) for (int c=0; c<8; c++) { String pos = b.convertCoordinatesToPosition(r,c); IPiece p = b.getPieceAt(pos); if(p!=null && p.getSide()==Side.WHITE) { String old = p.getPosition(); p.setPosition(pos); if(p.isValidMove("a8", b)) { System.out.println("White piece " + p.getClass().getSimpleName() + " at " + pos + " attacks a8"); } p.setPosition(old); } }
    System.out.println("Black isStalemate? " + b.isStalemate(Side.BLACK));
    // Enumerate black moves
    for (int r=0;r<8;r++) for (int c=0;c<8;c++){ String from = b.convertCoordinatesToPosition(r,c); IPiece p=b.getPieceAt(from); if(p!=null && p.getSide()==Side.BLACK){ System.out.println("Black piece: " + p.getClass().getSimpleName() + " at " + from); for (int rr=0; rr<8; rr++) for (int cc=0; cc<8; cc++){ String to=b.convertCoordinatesToPosition(rr,cc); String old=p.getPosition(); p.setPosition(from); boolean val=p.isValidMove(to,b); p.setPosition(old); if(val){ Board clone=b.clone(); IPiece cp=clone.getPieceAt(from); if(cp!=null){ cp.setPosition(to); clone.setPieceAt(to, cp); clone.setPieceAt(from, null); boolean k=clone.isKingInCheck(Side.BLACK); System.out.println("  Valid move: " + from + "->" + to + ", kingInCheckAfter=" + k); } } } } }
  }
}
