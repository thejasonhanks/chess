package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static chess.ChessGame.TeamColor.BLACK;
import static chess.ChessGame.TeamColor.WHITE;
import static chess.ChessPiece.PieceType.*;


public class PawnMovesCalculator extends PieceMovesCalculator {
    int[][] directions = {{1, 0}, {2, 0}, {1, -1}, {1, 1}};

    @Override
    protected Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        List<ChessMove> positions = new ArrayList<>();
        int cRow = myPosition.getRow();
        int cCol = myPosition.getColumn();
        int i = 0;
        ChessGame.TeamColor color = board.getPiece(myPosition).getTeamColor();
        for (int[] d : directions) {
            int nRow;
            int nCol;
            if (color == WHITE) {
                nRow = cRow + d[0];
                nCol = cCol + d[1];
            }else{
                nRow = cRow - d[0];
                nCol = cCol - d[1];
            }
            if (inBounds(nRow, nCol)) {
                ChessPosition newPosition = new ChessPosition(nRow, nCol);

                // empty space
                if (board.getPiece(newPosition) == null && i == 0)
                    addMove(positions, color, nRow, myPosition, newPosition);

                //starting position
                if (i == 1 && board.getPiece(newPosition) == null) {
                    if ((color == WHITE && cRow == 2 && board.getPiece(new ChessPosition(nRow - 1, nCol)) == null) ||
                        (color == BLACK && cRow == 7 && board.getPiece(new ChessPosition(nRow + 1, nCol)) == null)) {
                        addMove(positions, color, nRow, myPosition, newPosition);
                    }
                }

                // left capture
                if (i == 2 && board.getPiece(newPosition) != null && checkEnemy(board, newPosition, myPosition))
                    addMove(positions, color, nRow, myPosition, newPosition);

                // right capture
                if (i == 3 && board.getPiece(newPosition) != null && checkEnemy(board, newPosition, myPosition))
                    addMove(positions, color, nRow, myPosition, newPosition);
            }
            i++;
        }
    return positions;
    }

    void addMove(Collection<ChessMove> positions, ChessGame.TeamColor color,
                 int row, ChessPosition myPosition, ChessPosition newPosition){
        if ((color == WHITE && row == 8) || (color == BLACK && row == 1)){
            addPromotionMove(positions, myPosition, newPosition, color);
        }else {
            positions.add(new ChessMove(myPosition, newPosition, null));
        }
    }

    void addPromotionMove(Collection<ChessMove> positions, ChessPosition myPosition,
                          ChessPosition newPosition, ChessGame.TeamColor color){
        ChessPiece.PieceType[] promotionTypes = {QUEEN, ROOK, BISHOP, KNIGHT};
        for (ChessPiece.PieceType type : promotionTypes) {
            ChessPiece promoPiece = new ChessPiece(color, type);
            positions.add(new ChessMove(myPosition, newPosition, promoPiece.getPieceType()));
        }
    }
}
