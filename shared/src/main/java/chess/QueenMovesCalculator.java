package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class QueenMovesCalculator extends PieceMovesCalculator{
    int[][] directions = {{1,1},{1,-1},{-1,1},{-1,-1},{0,1},{0,-1},{-1,0},{1,0}};
    @Override
    protected Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        List<ChessMove> positions = new ArrayList<>();
        int cRow = myPosition.getRow();
        int cCol = myPosition.getColumn();
        for (int[] d : directions){
            int nRow = cRow + d[0];
            int nCol = cCol + d[1];
            while (inBounds(nRow, nCol)){
                ChessPosition newPosition = new ChessPosition(nRow, nCol);
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
                nRow += d[0];
                nCol += d[1];
            }
        }
        return positions;
    }
}

