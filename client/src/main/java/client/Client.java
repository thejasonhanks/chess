package client;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import exception.ResponseException;


import static ui.EscapeSequences.*;

import model.GameData;
import request.*;
import result.*;

public class Client {
    private final ServerFacade server;
    private String username = null;
    private String authToken = null;
    private List<GameData> gameList = new ArrayList<>();
    private State state = State.LOGGEDOUT;

    public enum State {
        LOGGEDOUT,
        LOGGEDIN
    }

    public Client(String serverUrl) {
        server = new ServerFacade(serverUrl);
    }

    public void run() {
        System.out.println("♕ Welcome to Chess. Type 'help' to get started. ♕\n");

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while(!"quit".equals(result)) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = eval(line);
                System.out.print(result);
            } catch(Throwable e){
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }

    private void printPrompt() {
        if (state == State.LOGGEDIN) {
            System.out.print(SET_TEXT_COLOR_BLUE + "\n[LOGGED_IN: " + username + "] >>> " + RESET_TEXT_COLOR);
        } else {
            System.out.print("\n[LOGGED_OUT] " + ">>> ");
        }
    }

    public String eval(String input) {
        try {
            String[] tokens = input.split(" ");
            String cmd = tokens[0].toLowerCase();
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            if (state == State.LOGGEDOUT){
                return switch (cmd) {
                    case "register" -> register(params);
                    case "login" -> login(params);
                    case "quit" -> "quit";
                    case "help" -> help();
                    default -> "Please enter a valid response. Type 'help' to see your options.";
                };
            } else {
                return switch(cmd) {
                    case "create" -> createGame(params);
                    case "list" -> listGames();
                    case "join" -> joinGame(params);
                    case "observe" -> observeGame(params);
                    case "logout" -> logout();
                    case "quit" -> "quit";
                    case "help" -> help();
                    case "clear" -> clear();
                    default -> "Please enter a valid response. Type 'help' to see your options.";
                };
            }
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    public String register(String... params) throws ResponseException {
        if (params.length ==3) {
            var result = server.register(
                    new RegisterRequest(params[0], params[1], params[2])
            );

            authToken = result.authToken();
            username = result.username();
            state = State.LOGGEDIN;

            return "Registered and logged in as " + username;
        }

        throw new ResponseException(ResponseException.Code.ClientError, "Expected: <username> <password> <email>");
    }

    public String login(String... params) throws ResponseException {
        if (params.length == 2) {
            var result = server.login(new LoginRequest(params[0], params[1]));

            authToken = result.authToken();
            username = result.username();
            state = State.LOGGEDIN;

            return "Logged in as " + username + ". Type 'help' to see further options.";
        }

        throw new ResponseException(ResponseException.Code.ClientError, "Error: Expected: <username> <password>");
    }

    public String createGame(String... params) throws ResponseException {
        assertSignedIn();
        if (params.length == 1) {
            server.createGame(authToken, new CreateRequest(params[0]));

            return "New game created.";
        }
        throw new ResponseException(ResponseException.Code.ClientError, "Expected: <gameName>");
    }

    public String listGames() throws ResponseException {
        assertSignedIn();

        var result = server.listGames(authToken);
        gameList = result.games();

        StringBuilder sb = new StringBuilder();
        int i = 1;
        if (gameList.isEmpty()){
            return "No existing games. Type 'create <gameName>' to create a new game.";
        }
        for (var game : gameList){
            sb.append(i++)
                    .append(". ")
                    .append(game.gameName())
                    .append(" | White: ")
                    .append(game.whiteUsername() == null ? "-" : game.whiteUsername())
                    .append(" | Black: ")
                    .append(game.blackUsername() == null ? "-" : game.blackUsername())
                    .append("\n");
        }

        return sb.toString();
    }

    public String joinGame(String... params) throws ResponseException {
        assertSignedIn();

        if (params.length == 2) {
            int gameNumber = Integer.parseInt(params[0]);
            String color = params[1].toUpperCase();

            if (gameList.isEmpty()) {
                listGames();
            }

            int index = gameNumber - 1;

            if (index < 0 || index >= gameList.size()) {
                throw new ResponseException(ResponseException.Code.ClientError, "Invalid game number");
            }

            int gameID = gameList.get(index).gameID();

            server.joinGame(authToken, new JoinRequest(color, gameID));

            drawBoard(color.equals("WHITE"));

            return "Joined game " + gameNumber + " as " + color;
        }
        throw new ResponseException(ResponseException.Code.ClientError, "Expected: <gameID> <WHITE|BLACK>");
    }

    public String observeGame(String... params) throws ResponseException {
        assertSignedIn();

        if (params.length == 1){
            try {
                int index = Integer.parseInt(params[0]) - 1;
                if (index < 0 || index >= gameList.size()) {
                    throw new ResponseException(ResponseException.Code.ClientError, "Error: Invalid game number");
                }

                drawBoard(true);

                return "Observing game" + index;
            } catch(Throwable ex){
                throw new ResponseException(ResponseException.Code.ClientError, "Error: Invalid game number");
            }
        }

        throw new ResponseException(ResponseException.Code.ClientError, "Error: Expected <number>");

  }

    public String logout() throws ResponseException {
        assertSignedIn();

        server.logout(authToken);

        authToken = null;
        username = null;
        state = State.LOGGEDOUT;

        return "Logged out\n♕ Welcome to Chess. Type 'help' to get started. ♕";
    }

    public String help() {
        if (state == State.LOGGEDOUT) {
            return """
                    - register <username> <password> <email>
                    - login <username> <password>
                    - help
                    - quit
                    """;
        }
        return """
                - create <gameName>
                - list
                - join <gameID> [WHITE|BLACK]
                - observe <gameID>
                - logout
                - help
                - quit
                """;
    }

    private String clear() throws ResponseException {
        server.clear();
        state = State.LOGGEDOUT;
        return "Database cleared.";
    }

    private void assertSignedIn() throws ResponseException {
        if (state == State.LOGGEDOUT) {
            throw new ResponseException(ResponseException.Code.ClientError, "You must sign in");
        }
    }

    private void drawBoard(boolean whitePerspective) {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        out.print(ERASE_SCREEN);
        String[] whiteBack = {WHITE_ROOK, WHITE_KNIGHT, WHITE_BISHOP, WHITE_QUEEN,
                WHITE_KING, WHITE_BISHOP, WHITE_KNIGHT, WHITE_ROOK};
        String[] blackBack = {BLACK_ROOK, BLACK_KNIGHT, BLACK_BISHOP, BLACK_QUEEN,
                BLACK_KING, BLACK_BISHOP, BLACK_KNIGHT, BLACK_ROOK};

        int start = whitePerspective ? 7 : 0;
        int end = whitePerspective ? -1 : 8;
        int step = whitePerspective ? -1 : 1;

        if (whitePerspective) {out.println("   a   b   c  d   e  f   g   h");}
        else {out.println("   h   g   f  e   d  c   b   a");}
        for (int r = start; r != end; r += step) {

            out.print((r+1) + " ");

            for (int c = 0; c < 8; c++) {
                int col = whitePerspective ? c : 7 - c;

                boolean isLightSquare = (r + col) % 2 != 0;
                if (isLightSquare) {out.print(SET_BG_COLOR_LIGHT_GREY);}
                else {out.print(SET_BG_COLOR_DARK_GREEN);}

                String piece = getPiece(r, col, whiteBack, blackBack);

                out.print(piece);
                out.print(RESET_BG_COLOR);
                out.print(RESET_TEXT_COLOR);
            }
            out.print(" " + (r+1));
            out.println();
        }

        if (whitePerspective) {out.println("   a   b   c  d   e  f   g   h");}
        else {out.println("   h   g   f  e   d  c   b   a");}
    }

    private String getPiece(int r, int col, String[] whiteBack, String[] blackBack) {
        if (r == 1) {
            return WHITE_PAWN;
        }
        if (r == 6) {
            return SET_TEXT_COLOR_BLACK + BLACK_PAWN;
        }
        if (r == 0) {
            return whiteBack[col];
        }
        if (r == 7) {
            return SET_TEXT_COLOR_BLACK + blackBack[col];
        }
        return EMPTY;
    }
}
