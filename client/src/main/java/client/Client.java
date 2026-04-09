package client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import client.websocket.WebSocketFacade;
import exception.ResponseException;


import static ui.EscapeSequences.*;

import model.GameData;
import request.*;
import result.*;
import ui.GameplayUI;

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
        System.out.println("♕ Welcome to Chess. Type 'help' to get started. ♕");

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

            return "Registered and logged in as " + username + ". Type 'help' to see further options.";
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
                throw new ResponseException(ResponseException.Code.ClientError, "Error: Invalid game number");
            }

            int gameID = gameList.get(index).gameID();

            server.joinGame(authToken, new JoinRequest(color, gameID));
            boolean whitePerspective = color.equals("WHITE");

            try{
                var gameplayUI = new GameplayUI(null, whitePerspective, new Scanner(System.in), System.out);
                var ws = new WebSocketFacade(server.getServerUrl(), gameplayUI);
                gameplayUI.setWebSocket(ws);
                ws.sendConnect(authToken, gameID);
                gameplayUI.runGameplayLoop(authToken, gameID);
            } catch(Exception e) {
                throw new ResponseException(ResponseException.Code.ServerError, e.getMessage());
            }

            return "Returned to menu\n";
        }
        throw new ResponseException(ResponseException.Code.ClientError, "Error: Expected: <gameID> <WHITE|BLACK>");
    }

    public String observeGame(String... params) throws ResponseException {
        assertSignedIn();

        if (params.length == 1){
            int index = Integer.parseInt(params[0]) - 1;
            if (index < 0 || index >= gameList.size()) {
                throw new ResponseException(ResponseException.Code.ClientError, "Error: Invalid game number");
            }

            int gameID = gameList.get(index).gameID();
            try {
                boolean whitePerspective = true;

                var gameplayUI = new GameplayUI(null, whitePerspective, new Scanner(System.in), System.out);
                var ws = new WebSocketFacade(server.getServerUrl(), gameplayUI);
                gameplayUI.setWebSocket(ws);
                ws.sendConnect(authToken, gameID);
                gameplayUI.runGameplayLoop(authToken, gameID);
            } catch(Exception e) {
                throw new ResponseException(ResponseException.Code.ServerError, e.getMessage());
            }

            return "Returned to menu\n";
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
                    Options:
                      register <username> <password> <email> - register a new user
                      login <username> <password> - login an existing user
                      help - print command options
                      quit - exit server
                    """;
        }
        return """
                Options:
                  create <gameName> - create new chess game
                  list - list existing chess games
                  join <gameID> [WHITE|BLACK] - join existing chess game as either white or black
                  observe <gameID> - watch existing chess game
                  logout - logout current user
                  help - print command options
                  quit - exit server
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
}
