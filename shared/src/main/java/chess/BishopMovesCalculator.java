package chess;

import java.util.Collection;

public class BishopMovesCalculator extends PieceMovesCalculator {
    int[][] directions = {{1,1},{1,-1},{-1,1},{-1,-1}};
    // possible moves
    @Override
    protected Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        return slideMoves(board, myPosition, directions);
    }
}
