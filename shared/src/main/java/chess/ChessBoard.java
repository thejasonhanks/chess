package chess;

import java.util.Arrays;
import java.util.Objects;

import static chess.ChessPiece.PieceType.BISHOP;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    ChessPiece[][] checkers = new ChessPiece[8][8];
    public ChessBoard() {
        
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        checkers[position.getRow()-1][position.getColumn()-1] = piece;
    }
    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return checkers[position.getRow()-1][position.getColumn()-1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        for (int row = 0; row < 8; row++){
            for (int col = 0; col<8; col++){
                if (row == 1){
                    addPiece(new ChessPosition(row,col), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN));
                }
                else if (row == 6){
                    addPiece(new ChessPosition(row,col), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN));
                }
                else if (row == 0) {
                    if (col == 0 | col == 7) {
                        addPiece(new ChessPosition(row, col), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK));
                    }
                    else if (col == 1 | col == 6){
                        addPiece(new ChessPosition(row,col), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT));
                    }
                    else if (col == 2 | col == 5){
                        addPiece(new ChessPosition(row,col), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP));
                    }
                    else if (col == 3){
                        addPiece(new ChessPosition(row,col), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN));
                    }
                    else{
                        addPiece(new ChessPosition(row,col), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING));
                    }
                }
                else if (row == 7){
                    if (col == 0 | col == 7) {
                        addPiece(new ChessPosition(row, col), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK));
                    }
                    else if (col == 1 | col == 6){
                        addPiece(new ChessPosition(row,col), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT));
                    }
                    else if (col == 2 | col == 5){
                        addPiece(new ChessPosition(row,col), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP));
                    }
                    else if (col == 3){
                        addPiece(new ChessPosition(row,col), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN));
                    }
                    else{
                        addPiece(new ChessPosition(row,col), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING));
                    }
                }
                else{
                    checkers[row][col] = null;
                }
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(checkers, that.checkers);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(checkers);
    }
}
