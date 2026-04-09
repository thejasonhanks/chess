package ui;



import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import client.websocket.NotificationHandler;
import client.websocket.WebSocketFacade;
import websocket.messages.*;

import static chess.ChessGame.TeamColor.BLACK;
import static chess.ChessGame.TeamColor.WHITE;
import static ui.EscapeSequences.*;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.Scanner;

public class GameplayUI implements NotificationHandler {
    private WebSocketFacade ws;
    private final Scanner scanner;
    private final PrintStream out;
    private final boolean whitePerspective;
    private ChessGame currentGame;

    public GameplayUI(WebSocketFacade ws, boolean whitePerspective, Scanner scanner, PrintStream out) {
        this.ws = ws;
        this.whitePerspective = whitePerspective;
        this.scanner = scanner;
        this.out = out;
    }

    public void setWebSocket(WebSocketFacade ws) {
        this.ws = ws;
    }

    private volatile boolean promptPrinted = false;
    private void printPrompt(){
        out.print(SET_TEXT_COLOR_BLUE + "[GAMEPLAY] >>> " + RESET_TEXT_COLOR);
        promptPrinted = true;
    }

    @Override
    public void notify(NotificationMessage notification) {
        synchronized (out) {
            if (promptPrinted) {out.println();}
            out.println(SET_TEXT_COLOR_YELLOW + notification.getMessage() + RESET_TEXT_COLOR);
            printPrompt();
        }
    }

    public void error(ErrorMessage e) {
        synchronized (out) {
            out.println(SET_TEXT_COLOR_RED + e.getErrorMessage() + RESET_TEXT_COLOR);
            printPrompt();
        }
    }

    @Override
    public void loadGame(LoadGameMessage game) {
        this.currentGame = game.getGame();
        synchronized(out) {
            if (promptPrinted) {out.println();}
            drawBoard(currentGame, whitePerspective, null, null);
            printPrompt();
        }
    }

    public void runGameplayLoop(String authToken, int gameID) {
        while (true) {
            String line = scanner.nextLine();
            promptPrinted = false;
            String[] tokens = line.split(" ");
            String command = tokens[0].toLowerCase();

            try {
                switch (command) {
                    case "move" -> {
                        ChessMove move = parseMove(tokens[1]);
                        ws.sendMakeMove(authToken, gameID, move);
                    }
                    case "resign" -> {
                        out.print("Are you sure you want to resign? (y/n): ");
                        String confirm = scanner.nextLine().trim().toLowerCase();
                        confirmResign(confirm, authToken, gameID);
                    }
                    case "leave" -> {
                        ws.sendLeave(authToken, gameID);
                        return;
                    }
                    case "help" -> help();
                    case "redraw" -> redraw();
                    case "highlight" -> {
                        ChessPosition pos = parsePosition(tokens[1]);
                        highlightMoves(pos);
                        printPrompt();
                    }
                    default -> {
                        out.println("Please enter a valid response. Type 'help' to see your options.");
                        printPrompt();
                    }
                }
            } catch (Exception e) {
                out.println(SET_TEXT_COLOR_RED + e.getMessage() + RESET_TEXT_COLOR);
                printPrompt();
            }
        }
    }

    private void drawBoard(ChessGame game, boolean whitePerspective, Set<ChessPosition> highlights, ChessPosition currentPos) {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        out.print(ERASE_SCREEN);

        int start = whitePerspective ? 7 : 0;
        int end = whitePerspective ? -1 : 8;
        int step = whitePerspective ? -1 : 1;

        if (whitePerspective) {
            out.println("   a   b   c  d   e  f   g   h");
        } else {
            out.println("   h   g   f  e   d  c   b   a");
        }
        for (int r = start; r != end; r += step) {

            out.print((r + 1) + " ");

            for (int c = 0; c < 8; c++) {
                int col = whitePerspective ? c : 7 - c;
                ChessPosition pos = new ChessPosition(r + 1, col + 1);
                boolean isHighlighted = highlights != null && highlights.contains(pos);
                boolean isLightSquare = (r + col) % 2 != 0;

                if (isHighlighted) {
                    if (isLightSquare){
                        out.print(SET_BG_COLOR_GREEN);
                    }else {
                        out.print(SET_BG_COLOR_DARK_GREEN);
                    }
                } else if (pos.equals(currentPos)) {
                    out.print(SET_TEXT_COLOR_BLACK);
                    out.print(SET_BG_COLOR_YELLOW);
                } else {
                    out.print(isLightSquare ? SET_BG_COLOR_LIGHT_GREY : SET_BG_COLOR_BLACK);
                }

                ChessPiece piece = game.getBoard().getPiece(pos);

                if (piece == null){
                    out.print(EMPTY);
                }
                else if (piece.getTeamColor() == WHITE){
                    out.print(SET_TEXT_COLOR_BLUE);
                    out.print(getPieceString(piece));
                }else if (piece.getTeamColor() == BLACK){
                    out.print(SET_TEXT_COLOR_RED);
                    out.print(getPieceString(piece));
                }
                out.print(RESET_BG_COLOR);
                out.print(RESET_TEXT_COLOR);
            }
            out.print(" " + (r + 1));
            out.println();
        }

        if (whitePerspective) {
            out.println("   a   b   c  d   e  f   g   h");
        } else {
            out.println("   h   g   f  e   d  c   b   a");
        }
    }

    private String getPieceString(ChessPiece piece) {
        return switch (piece.getPieceType()) {
            case KING -> piece.getTeamColor() == WHITE ? WHITE_KING : BLACK_KING;
            case QUEEN -> piece.getTeamColor() == WHITE ? WHITE_QUEEN : BLACK_QUEEN;
            case BISHOP -> piece.getTeamColor() == WHITE ? WHITE_BISHOP : BLACK_BISHOP;
            case KNIGHT -> piece.getTeamColor() == WHITE ? WHITE_KNIGHT : BLACK_KNIGHT;
            case ROOK -> piece.getTeamColor() == WHITE ? WHITE_ROOK : BLACK_ROOK;
            case PAWN -> piece.getTeamColor() == WHITE ? WHITE_PAWN : BLACK_PAWN;
        };
    }

    private void confirmResign (String confirm, String authToken, int gameID) throws Exception{
        if (confirm.equals("y") || confirm.equals("yes")) {
            ws.sendResign(authToken, gameID);
            out.println("You have resigned the game.");
        } else if (confirm.equals("n") || confirm.equals("no")) {
            out.println("Resign cancelled.");
            printPrompt();
        } else {
            out.println("Please enter yes or no: ");
        }
    }

    private void help() {
        out.println("""
                Gameplay options:
                  move <from><to><promotion piece> - make a chess move. if applicable, enter the promotion piece
                    as follows: q - queen, r - rook, b - bishop, n - knight. example of valid inputs: e2e4, e2e4q
                  highlight <piece position> - highlight legal moves. example of valid input: e4
                  resign - forfeit the game
                  leave - exit game
                  redraw - redraw chess board
                  help - print gameplay options
                """);
        printPrompt();
    }

    private void redraw() {
        synchronized (out) {
            drawBoard(currentGame, whitePerspective, null, null);
            printPrompt();
        }
    }

    private ChessMove parseMove(String input) {
        ChessPiece.PieceType promotionPiece = null;
        if (input.length() < 4 || input.length() > 5) {
            throw new IllegalArgumentException("Error: Invalid move format. Correct examples: e2e4, e2e4q");
        }

        int startCol = input.charAt(0) - 'a' + 1;
        int startRow = input.charAt(1) - '0';
        int endCol = input.charAt(2) - 'a' + 1;
        int endRow = input.charAt(3) - '0';

        if (!Character.isLetter(input.charAt(0)) || !Character.isLetter(input.charAt(2))
                || !Character.isDigit(input.charAt(1)) || !Character.isDigit(input.charAt(3))){
            throw new IllegalArgumentException("Error: Invalid move format. Correct examples: e2e4, e2e4q");
        }else if (input.charAt(0) < 'a' || input.charAt(0) > 'h' ||
                input.charAt(2) < 'a' || input.charAt(2) > 'h' ||
                input.charAt(1) < '1' || input.charAt(1) > '8' ||
                input.charAt(3) < '1' || input.charAt(3) > '8'){
            throw new IllegalArgumentException("Error: Invalid move format. Correct examples: e2e4, e2e4q");
        }
        if (input.length() == 5) {
            char promotion = input.charAt(4);
            switch(promotion) {
                case 'q' -> promotionPiece = ChessPiece.PieceType.QUEEN;
                case 'r' -> promotionPiece = ChessPiece.PieceType.ROOK;
                case 'b' -> promotionPiece = ChessPiece.PieceType.BISHOP;
                case 'n' -> promotionPiece = ChessPiece.PieceType.KNIGHT;
            }
        }
        ChessPosition start = new ChessPosition(startRow, startCol);
        ChessPosition end = new ChessPosition(endRow, endCol);
        return new ChessMove(start, end, promotionPiece);
    }

    private ChessPosition parsePosition(String input) {
        int col = input.charAt(0) - 'a' + 1;
        int row = input.charAt(1) - '0';
        return new ChessPosition(row, col);
    }

    private void highlightMoves(ChessPosition pos) {
        if (currentGame == null) {
            System.out.println("No game loaded");
            return;
        }

        Collection<ChessMove> moves = currentGame.validMoves(pos);
        if (moves == null || moves.isEmpty()) {
            System.out.println("No legal moves for that piece");
            return;
        }

        Set<ChessPosition> endPositions = new HashSet<>();
        for (ChessMove move : moves){
            endPositions.add(move.getEndPosition());
        }

        drawBoard(currentGame, whitePerspective, endPositions, pos);
    }
}