package chess;

import java.util.Collection;

public abstract class PieceMovesCalculator {

    protected abstract Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition);

    protected boolean inBounds(int x, int y){
        return x > 0 && x <= 8 && y > 0 && y <= 8;
    }

    protected boolean checkEnemy(ChessBoard board, ChessPosition newPosition, ChessPosition myPosition){
        return board.getPiece(newPosition).getTeamColor() != board.getPiece(myPosition).getTeamColor();
    }
}
