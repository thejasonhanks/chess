package chess;

import java.util.Collection;

public abstract class PieceMovesCalculator {
    private ChessBoard board;
    private ChessPosition myPosition;
    protected abstract Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition);

    public ChessBoard getBoard() {
        return board;
    }

    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    public ChessPosition getMyPosition() {
        return myPosition;
    }

    public void setMyPosition(ChessPosition myPosition) {
        this.myPosition = myPosition;
    }
}
