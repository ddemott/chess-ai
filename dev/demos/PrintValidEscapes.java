import com.ddemott.chessai.*;
import com.ddemott.chessai.pieces.*;

public class PrintValidEscapes {
    public static void main(String[] args) {
        Board b = new Board();
        b.clearBoard();
        b.setPieceAt("h8", new King(Side.BLACK, "h8"));
        b.setPieceAt("g7", new Rook(Side.WHITE, "g7"));
        b.setPieceAt("f7", new Knight(Side.WHITE, "f7"));
        b.setPieceAt("a1", new King(Side.WHITE, "a1"));

        System.out.println(b.getBoardRepresentation());
        System.out.println("Black in check? " + b.isKingInCheck(Side.BLACK));
        for (int row=0; row<8; row++) {
            for (int col=0; col<8; col++) {
                String from = b.convertCoordinatesToPosition(row, col);
                IPiece p = b.getPieceAt(from);
                if (p != null && p.getSide()==Side.BLACK) {
                    System.out.println("Piece: " + p.getClass().getSimpleName() + " at " + from);
                    for (int r=0;r<8;r++){
                        for (int c=0;c<8;c++){
                            String to = b.convertCoordinatesToPosition(r,c);
                            String oldPpos = p.getPosition();
                            p.setPosition(from);
                            boolean valid = p.isValidMove(to, b);
                            p.setPosition(oldPpos);
                            if (valid) {
                                Board clone = b.clone();
                                IPiece cp = clone.getPieceAt(from);
                                if (cp!=null) {
                                    cp.setPosition(to);
                                    clone.setPieceAt(to, cp);
                                    clone.setPieceAt(from, null);
                                    boolean stillInCheck = clone.isKingInCheck(Side.BLACK);
                                    if (!stillInCheck) {
                                        System.out.println("   Escape: " + from + " -> " + to + " by " + p.getClass().getSimpleName());
                                    } else {
                                        System.out.println("   Valid but still in check: " + from + " -> " + to);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

    }
}
