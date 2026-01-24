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
                if (board.getPiece(newPosition) == null && i == 0) {
                    // empty space
                    if ((color == WHITE && nRow == 8) | (color == BLACK && nRow == 1)){
                        positions.add(new ChessMove(myPosition, newPosition, new ChessPiece(color, BISHOP).getPieceType()));
                        positions.add(new ChessMove(myPosition, newPosition, new ChessPiece(color, ROOK).getPieceType()));
                        positions.add(new ChessMove(myPosition, newPosition, new ChessPiece(color, KNIGHT).getPieceType()));
                        positions.add(new ChessMove(myPosition, newPosition, new ChessPiece(color, QUEEN).getPieceType()));
                    }else {
                        positions.add(new ChessMove(myPosition, newPosition, null));
                    }
                }
                if ((color == WHITE && cRow == 2 && i == 1 && board.getPiece(newPosition) == null && board.getPiece(new ChessPosition(nRow-1, nCol)) == null) | (color == BLACK && cRow == 7 && i == 1 && board.getPiece(newPosition) == null && board.getPiece(new ChessPosition(nRow+1, nCol)) == null)) {
                    //starting position
                    if ((color == WHITE && nRow == 8) | (color == BLACK && nRow == 1)){
                        positions.add(new ChessMove(myPosition, newPosition, new ChessPiece(color, BISHOP).getPieceType()));
                        positions.add(new ChessMove(myPosition, newPosition, new ChessPiece(color, ROOK).getPieceType()));
                        positions.add(new ChessMove(myPosition, newPosition, new ChessPiece(color, KNIGHT).getPieceType()));
                        positions.add(new ChessMove(myPosition, newPosition, new ChessPiece(color, QUEEN).getPieceType()));
                    }else {
                        positions.add(new ChessMove(myPosition, newPosition, null));
                    }
                }
                if (i == 2 && board.getPiece(newPosition) != null && checkEnemy(board, newPosition, myPosition)) {
                    // left capture
                    if ((color == WHITE && nRow == 8) | (color == BLACK && nRow == 1)){
                        positions.add(new ChessMove(myPosition, newPosition, new ChessPiece(color, BISHOP).getPieceType()));
                        positions.add(new ChessMove(myPosition, newPosition, new ChessPiece(color, ROOK).getPieceType()));
                        positions.add(new ChessMove(myPosition, newPosition, new ChessPiece(color, KNIGHT).getPieceType()));
                        positions.add(new ChessMove(myPosition, newPosition, new ChessPiece(color, QUEEN).getPieceType()));
                    }else {
                        positions.add(new ChessMove(myPosition, newPosition, null));
                    }
                }
                if (i == 3 && board.getPiece(newPosition) != null && checkEnemy(board, newPosition, myPosition)) {
                    // right capture
                    if ((color == WHITE && nRow == 8) | (color == BLACK && nRow == 1)){
                        positions.add(new ChessMove(myPosition, newPosition, new ChessPiece(color, BISHOP).getPieceType()));
                        positions.add(new ChessMove(myPosition, newPosition, new ChessPiece(color, ROOK).getPieceType()));
                        positions.add(new ChessMove(myPosition, newPosition, new ChessPiece(color, KNIGHT).getPieceType()));
                        positions.add(new ChessMove(myPosition, newPosition, new ChessPiece(color, QUEEN).getPieceType()));
                    }else {
                        positions.add(new ChessMove(myPosition, newPosition, null));
                    }
                }
            }
            i++;
        }
    return positions;
    }
}
