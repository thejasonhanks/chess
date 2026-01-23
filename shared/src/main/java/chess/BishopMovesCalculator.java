package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BishopMovesCalculator extends PieceMovesCalculator {
    @Override
    protected Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        List<ChessMove> positions = new ArrayList<>();
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                if (Math.abs(j - myPosition.getColumn()) == Math.abs(i - myPosition.getRow()) && Math.abs(j - myPosition.getColumn()) > 0) {
                    positions.add(new ChessMove(new ChessPosition(myPosition.getRow(), myPosition.getColumn()), new ChessPosition(i, j), null));
                }
            }
        }
        return positions;
    }
}
