import com.ddemott.chessai.engine.GameEngine;
import com.ddemott.chessai.pieces.*;
import com.ddemott.chessai.*;

public class DebugDiscovered {
    public static void main(String[] args) {
        GameEngine engine = new GameEngine(3);
        Board board = engine.getGameState().getBoard();
        board.clearBoard();
        board.setPieceAt("e1", new King(Side.WHITE, "e1"));
        board.setPieceAt("e4", new Bishop(Side.BLACK, "e4"));
        board.setPieceAt("e3", new Knight(Side.BLACK, "e3"));
        board.setPieceAt("a5", new King(Side.BLACK, "a5"));
        engine.getGameState().setCurrentTurn("Black");
        System.out.println(board.getBoardRepresentation());
        System.out.println("White in check? " + board.isKingInCheck(Side.WHITE));
        boolean moveResult = engine.movePiece("e3", "c4");
        System.out.println("Move result: " + moveResult);
        System.out.println(board.getBoardRepresentation());
        System.out.println("White in check? " + board.isKingInCheck(Side.WHITE));
    }
}
