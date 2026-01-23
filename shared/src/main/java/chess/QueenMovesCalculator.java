package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class QueenMovesCalculator extends PieceMovesCalculator{
    int[][] directions = {{1,1},{1,-1},{-1,1},{-1,-1},{0,1},{0,-1},{-1,0},{1,0}};
    @Override
    protected Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        List<ChessMove> positions = new ArrayList<>();
        int cx = myPosition.getRow();
        int cy = myPosition.getColumn();
        for (int[] d : directions){
            int x = cx + d[0];
            int y = cy + d[1];
            while (inBounds(x,y)){
                ChessPosition newPosition = new ChessPosition(x, y);
                if (board.getPiece(newPosition) == null) {
                    // empty space
                    positions.add(new ChessMove(myPosition, newPosition, null));
                } else {
                    if (checkEnemy(board, newPosition, myPosition)) {
                        // capture piece
                        positions.add(new ChessMove(myPosition, newPosition, null));
                    }
                    // white piece
                    break;
                }
                x += d[0];
                y += d[1];
            }
        }
        return positions;
    }
}

