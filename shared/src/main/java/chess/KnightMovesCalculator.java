package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class KnightMovesCalculator extends PieceMovesCalculator{
    int[][] directions = {{2,1},{2,-1},{-2,-1},{-2,1},{1,2},{1,-2},{-1,2},{-1,-2}};
    @Override
    protected Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        List<ChessMove> positions = new ArrayList<>();
        int cx = myPosition.getRow();
        int cy = myPosition.getColumn();
        for (int[] d : directions) {
            int x = cx + d[0];
            int y = cy + d[1];
            if (inBounds(x,y)) {
                ChessPosition newPosition = new ChessPosition(x, y);
                if ((board.getPiece(newPosition) == null)){
                    positions.add(new ChessMove(myPosition, newPosition, null));
                }else if (checkEnemy(board, newPosition, myPosition)){
                    positions.add(new ChessMove(myPosition, newPosition, null));
                }
                // empty space or capture piece
            }
        }
        return positions;
    }
}