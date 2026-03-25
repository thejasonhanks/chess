package client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import com.google.gson.Gson;
import exception.ResponseException;
//import client.websocket.NotificationHandler;
//import client.websocket.WebSocketFacade;
//import webSocketMessages.Notification;

import static ui.EscapeSequences.*;

import model.GameData;
import request.*;
import result.*;

public class Client {
    private final ServerFacade server;
    private String username = null;
    private String authToken = null;
    private List<GameData> gameList = new ArrayList<>();
    //private final WebSocketFacade ws;
    private State state = State.LOGGEDOUT;

    public enum State {
        LOGGEDOUT,
        LOGGEDIN
    }

    public Client(String serverUrl) throws Exception {
        server = new ServerFacade(serverUrl);
        //ws = new WebSocketFacade(serverUrl, this);
    }

    public void run() {
        System.out.println("♕ Welcome to Chess. Sign in to start. ♕");
        System.out.print(help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while(!"quit".equals(result)) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = eval(line);
                System.out.print(SET_TEXT_COLOR_GREEN + result);
            } catch(Throwable e){
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }

//    public void notify(Notification notiication) {
//        System.out.println(SET_BG_COLOR_RED + notification.message());
//        printPrompt();
//    }

    private void printPrompt() {
        if (state == State.LOGGEDIN) {
            System.out.print(SET_TEXT_COLOR_BLUE + "\n[" + username + "] >>> ");
        } else {
            System.out.print(SET_TEXT_COLOR_WHITE + "\n[LOGGED_OUT] " + ">>> ");
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
                    default -> help();
                };
            } else {
                return switch(cmd) {
                    case "create" -> createGame(params);
                    case "list" -> listGames();
                    case "join" -> joinGame(params);
                    case "observe" -> observeGame(params);
                    case "logout" -> logout();
                    case "quit" -> "quit";
                    default -> help();
                };
            }
        } catch (Exception ex) {
            return "Error: " + ex.getMessage();
        }
    }
    public String register(String... params) throws ResponseException {
        if (params.length >=3) {
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
        if (params.length >= 2) {
            var result = server.login(new LoginRequest(params[0], params[1]));

            authToken = result.authToken();
            username = result.username();
            state = State.LOGGEDIN;

            return "Logged in as " + username;
        }

        throw new ResponseException(ResponseException.Code.ClientError, "Expected: <username> <password>");
    }

    public String createGame(String... params) throws ResponseException {
        assertSignedIn();
        if (params.length >= 1) {
            var result = server.createGame(authToken, new CreateRequest(params[0]));

            return "Game created with ID: " + result.gameID();
        }
        throw new ResponseException(ResponseException.Code.ClientError, "Expected: <gameName>");
    }

    public String listGames() throws ResponseException {
        assertSignedIn();

        var result = server.listGames(authToken);
        gameList = result.games();

        StringBuilder sb = new StringBuilder();
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
        if (params.length >= 2) {
            int index = Integer.parseInt(params[0]) - 1;
            String color = params[1].toUpperCase();

            if (index < 0 || index >= gameList.size()) {
                throw new ResponseException(ResponseException.Code.ClientError, "Invalid game number");
            }

            int gameID = gameList.get(index).gameID();

            server.joinGame(authToken, new JoinRequest(color, gameID));

            drawBoard(color.equals("WHITE"));

            return "Joined game " + index + " as " + color;
        }
        throw new ResponseException(ResponseException.Code.ClientError, "Expected: <gameID> <WHITE|BLACK>");
    }

    public String observeGame(String... params) throws ResponseException {
        assertSignedIn();

        if (params.length >=1){
            int index = Integer.parseInt(params[0]) - 1);

            if (index < 0 || index >= gameList.size()) {
                throw new ResponseException(ResponseException.Code.ClientError, "Invalid game number");
            }

            drawBoard(true);

            return "Observing game" + index;
        }

        throw new ResponseException(ResponseException.Code.ClientError, "Expected <number>");

  }

    public String logout() throws ResponseException {
        assertSignedIn();

        server.logout(authToken);

        authToken = null;
        username = null;
        state = State.LOGGEDOUT;

        return "Logged out";
    }

    public String help() {
        if (state == State.LOGGEDOUT) {
            return """
                    - register <username> <password> <email>
                    - login <username> <password>
                    - quit
                    """;
        }
        return """
                - create <gameName>
                - list
                - join <gameID> <WHITE|BLACK>
                - logout
                - quit
                """;
    }

    private void assertSignedIn() throws ResponseException {
        if (state == State.LOGGEDOUT) {
            throw new ResponseException(ResponseException.Code.ClientError, "You must sign in");
        }
    }
}
