package chess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import static chess.ChessGame.TeamColor.BLACK;
import static chess.ChessGame.TeamColor.WHITE;
import static chess.ChessPiece.PieceType.*;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    ChessPiece[][] board = new ChessPiece[8][8];
    public ChessBoard() {

    }
    public ChessBoard(ChessBoard other) {
        this.board = new ChessPiece[8][8];
        for (int row = 0; row <= 7; row++) {
            System.arraycopy(other.board[row], 0, this.board[row], 0, 8);
        }
    }
    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        board[position.getRow()-1][position.getColumn()-1] = piece;
    }
    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return board[position.getRow()-1][position.getColumn()-1];
    }

    public Iterable<ChessPosition> allPositions() {
        var positions = new ArrayList<ChessPosition>();
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                positions.add(new ChessPosition(row, col));
            }
        }
        return positions;
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void placeBackRank(int row, ChessGame.TeamColor color){
        ChessPiece.PieceType[] backRank = {
                ROOK, KNIGHT, BISHOP, QUEEN, KING, BISHOP, KNIGHT, ROOK
        };

        for (int col = 1; col <= 8; col++){
            addPiece(new ChessPosition(row, col), new ChessPiece(color, backRank[col-1]));
        }
    }
    public void resetBoard() {
        placeBackRank(1, WHITE);
        placeBackRank(8, BLACK);
        for (int col = 1; col<=8; col++){
            addPiece(new ChessPosition(2,col), new ChessPiece(WHITE, PAWN));
            addPiece(new ChessPosition(7,col), new ChessPiece(BLACK, PAWN));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(board, that.board);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(board);
    }
}

