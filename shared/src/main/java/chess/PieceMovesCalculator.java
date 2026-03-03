package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class PieceMovesCalculator {

    protected abstract Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition);

    protected boolean inBounds(int x, int y){
        return x > 0 && x <= 8 && y > 0 && y <= 8;
    }

    protected boolean checkEnemy(ChessBoard board, ChessPosition newPosition, ChessPosition myPosition){
        return board.getPiece(newPosition).getTeamColor() != board.getPiece(myPosition).getTeamColor();
    }

    protected Collection<ChessMove> slideMoves(ChessBoard board, ChessPosition myPosition, int[][] directions){
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
