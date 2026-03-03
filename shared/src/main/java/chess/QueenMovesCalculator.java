package chess;

import java.util.Collection;

public class QueenMovesCalculator extends PieceMovesCalculator{
    int[][] directions = {{1,1},{1,-1},{-1,1},{-1,-1},{0,1},{0,-1},{-1,0},{1,0}};
    @Override
    protected Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        return slideMoves(board, myPosition, directions);
    }
}

