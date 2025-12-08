import com.ddemott.chessai.*;
import com.ddemott.chessai.pieces.*;
public class DebugArabian {
    public static void main(String[] args) {
        Board b = new Board();
        b.clearBoard();
        b.setPieceAt("g8", new King(Side.BLACK, "g8"));
        b.setPieceAt("g7", new Rook(Side.WHITE, "g7"));
        b.setPieceAt("e6", new Knight(Side.WHITE, "e6"));
        b.setPieceAt("f6", new Queen(Side.WHITE, "f6"));
        b.setPieceAt("a1", new King(Side.WHITE, "a1"));
        System.out.println(b.getBoardRepresentation());
        System.out.println("Black in check? " + b.isKingInCheck(Side.BLACK));
        System.out.println("Square h8 under attack? " + b.isSquareUnderAttack("h8", Side.BLACK));
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                String from = b.convertCoordinatesToPosition(r, c);
                IPiece p = b.getPieceAt(from);
                if (p != null && p.getSide() == Side.BLACK) {
                    System.out.println("Black piece: " + p.getClass().getSimpleName() + " at " + from);
                    for (int rr = 0; rr < 8; rr++) {
                        for (int cc = 0; cc < 8; cc++) {
                            String to = b.convertCoordinatesToPosition(rr, cc);
                            String old = p.getPosition();
                            p.setPosition(from);
                            boolean valid = p.isValidMove(to, b);
                            p.setPosition(old);
                            if (valid) {
                                Board clone = b.clone();
                                IPiece cp = clone.getPieceAt(from);
                                if (cp != null) {
                                    cp.setPosition(to);
                                    clone.setPieceAt(to, cp);
                                    clone.setPieceAt(from, null);
                                    boolean kingInCheck = clone.isKingInCheck(Side.BLACK);
                                    System.out.println("  Move: " + from + " -> " + to + ", kingInCheckAfter=" + kingInCheck);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
