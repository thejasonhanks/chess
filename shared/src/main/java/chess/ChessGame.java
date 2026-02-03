package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    public TeamColor turn;
    public ChessBoard board;
    public ChessGame() {
        setTeamTurn(TeamColor.WHITE);
        board.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return turn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        turn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        List<ChessMove> valid = new ArrayList<>();
        ChessPiece piece = board.getPiece(startPosition);
        if (piece == null) {
            return null;
        } else {
            Collection<ChessMove> allMoves = piece.pieceMoves(board, startPosition);
            for (ChessMove m : allMoves) {
                if (!isInCheck(getTeamTurn())) {
                    valid.add(m);
                }
            }
        }
        return valid;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        if (teams turn){
            if (move valid){
                make move
            }else{
                throw new InvalidMoveException("Invalid move");
            }
        }else{
            throw new InvalidMoveException("Not your turn");
        }

        if (getTeamTurn() == TeamColor.BLACK) {
            setTeamTurn(TeamColor.WHITE);
        }else{
            setTeamTurn(TeamColor.BLACK);
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        king = board.getpiece(position) == king
        for piece in other color:
            if validmoves includes king.position{
                return true;
        }
            else return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        for piece on board{
            if isInCheck() && validMoves() != null{
                return false
            }
        }
        else return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        for piece on board{
            if validMoves() != null{
                return false
            }
        }
        else return true;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        for (square in board){
            set new board = old board;
        }
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }


}
