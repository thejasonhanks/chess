package chess;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece piece = board.getPiece(myPosition);

        if (piece.getPieceType() == PieceType.BISHOP) {
            BishopMovesCalculator BMC = new BishopMovesCalculator();
            return BMC.pieceMoves(board, myPosition);
        }

        else if (piece.getPieceType() == PieceType.KING) {
            KingMovesCalculator KMC = new KingMovesCalculator();
            return KMC.pieceMoves(board, myPosition);

        } else if (piece.getPieceType() == PieceType.KNIGHT) {
            KnightMovesCalculator NMC = new KnightMovesCalculator();
            return NMC.pieceMoves(board, myPosition);

        } else if (piece.getPieceType() == PieceType.PAWN) {
            PawnMovesCalculator PMC = new PawnMovesCalculator();
            return PMC.pieceMoves(board, myPosition);

        } else if (piece.getPieceType() == PieceType.QUEEN) {
            QueenMovesCalculator QMC = new QueenMovesCalculator();
            return QMC.pieceMoves(board, myPosition);

        } else if (piece.getPieceType() == PieceType.ROOK) {
            RookMovesCalculator RMC = new RookMovesCalculator();
            return RMC.pieceMoves(board, myPosition);
        }
        return List.of();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }

    @Override
    public String toString() {
        return "ChessPiece{" +
                "pieceColor=" + pieceColor +
                ", type=" + type +
                '}';
    }
}
