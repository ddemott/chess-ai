import com.ddemott.chessai.engine.GameEngine;
import com.ddemott.chessai.pieces.*;
import com.ddemott.chessai.*;

public class DebugPromotion {
    public static void main(String[] args) {
        GameEngine engine = new GameEngine(1);
        engine.getGameState().getBoard().clearBoard();
        engine.getGameState().getBoard().setPieceAt("e7", new Pawn(Side.WHITE, "e7"));
        engine.getGameState().getBoard().setPieceAt("e1", new King(Side.WHITE, "e1"));
        // Leave e8 empty to test forward promotion
        engine.getGameState().setCurrentTurn("White");
        System.out.println(engine.getBoardRepresentation());
        // Simulate promotion on clone and check king safety
        Board b = engine.getGameState().getBoard();
        Board clone = b.clone();
        clone.setPieceAt("e7", null);
        clone.setPieceAt("e8", new Queen(Side.WHITE, "e8"));
        System.out.println("Clone white king in check after promotion? " + clone.isKingInCheck(Side.WHITE));
        IPiece pawn = b.getPieceAt("e7");
        int[] fromCoords = b.convertPositionToCoordinates("e7");
        int[] toCoords = b.convertPositionToCoordinates("e8");
        IPiece destPiece = b.getPieceAt("e8");
        System.out.println("Pawn from coords: " + fromCoords[0] + "," + fromCoords[1] + ", to coords: " + toCoords[0] + "," + toCoords[1]);
        System.out.println("Destination piece: " + (destPiece == null ? "null" : destPiece.getClass().getSimpleName()));
        System.out.println("Pawn isValidMove e7->e8? " + pawn.isValidMove("e8", b));
        boolean resultViaEngine = engine.movePiece("e7", "e8", "Q");
        boolean result = b.movePiece("e7", "e8", "Q");
        System.out.println("Promotion result (board.movePiece): " + result);
        System.out.println("Promotion result via engine: " + resultViaEngine);
        System.out.println("Promotion result: " + result);
        System.out.println(engine.getBoardRepresentation());
    }
}
