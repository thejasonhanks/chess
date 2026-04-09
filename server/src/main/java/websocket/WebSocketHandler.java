package websocket;

import chess.ChessGame;
import chess.ChessPosition;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import io.javalin.websocket.*;
import com.google.gson.Gson;
import model.GameData;
import org.jetbrains.annotations.NotNull;
import service.BadRequestException;
import service.GameService;
import service.UnauthorizedException;
import websocket.commands.*;
import websocket.messages.*;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final ConnectionManager connections = new ConnectionManager();
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;
    private final Gson gson = new Gson();

    public WebSocketHandler(AuthDAO authDAO, GameDAO gameDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    @Override
    public void handleConnect(WsConnectContext ctx) {
        System.out.println("Websocket connected");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(@NotNull WsMessageContext ctx) {
        try {
            UserGameCommand command = gson.fromJson(ctx.message(), UserGameCommand.class);
            switch (command.getCommandType()) {
                case CONNECT -> connect(command, ctx);
                case MAKE_MOVE -> {
                    MakeMoveCommand moveCommand = gson.fromJson(ctx.message(), MakeMoveCommand.class);
                    makeMove(moveCommand, ctx);
                }
                case LEAVE -> leave(command, ctx);
                case RESIGN -> resign(command);
            }
        } catch (Exception ex) {
            var error = new ErrorMessage("Error: " + ex.getMessage());
            ctx.send(gson.toJson(error));
        }
    }

    @Override
    public void handleClose(@NotNull WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }

    private void connect(UserGameCommand command, WsMessageContext ctx) throws Exception {
        String authToken = command.getAuthToken();
        int gameID = command.getGameID();

        if (authDAO.getAuth(authToken) == null) {
            throw new UnauthorizedException("Error: unauthorized");
        }
        var gameData = gameDAO.getGame(gameID);
        if (gameData == null) {
            throw new BadRequestException("Error: game not found");
        }

        connections.add(gameID, ctx.session);

        var game = gameData.game();
        var loadMessage = new LoadGameMessage(game);
        ctx.send(gson.toJson(loadMessage));

        String username = authDAO.getAuth(authToken).username();
        String message;
        if (username.equals(gameData.whiteUsername())) {
            message = username + " joined the game as white";
        } else if (username.equals(gameData.blackUsername())) {
            message = username + " joined the game as black";
        } else {
            message = username + " joined the game as an observer";
        }

        var notification = new NotificationMessage(message);
        connections.broadcast(gameID, ctx.session, notification);
    }

    public void makeMove(MakeMoveCommand command, WsMessageContext ctx) throws Exception {
        GameService gameService = new GameService(gameDAO, authDAO);

        gameService.makeMove(
                command.getAuthToken(),
                command.getGameID(),
                command.getMove()
        );

        GameData gameData = gameDAO.getGame(command.getGameID());
        ChessGame game = gameData.game();

        var loadMessage = new LoadGameMessage(game);
        connections.broadcast(command.getGameID(), null, loadMessage);

        String username = authDAO.getAuth(command.getAuthToken()).username();
        var notification = new NotificationMessage(username + " moved from " +
                positionToString(command.getMove().getStartPosition()) + " to " +
                positionToString(command.getMove().getEndPosition()));
        connections.broadcast(command.getGameID(), ctx.session, notification);

        ChessGame.TeamColor nextTurn = game.getTeamTurn();
        String nextPlayer = nextTurn == ChessGame.TeamColor.WHITE ? gameData.whiteUsername() : gameData.blackUsername();

        if (game.isInCheckmate(nextTurn)) {
            connections.broadcast(command.getGameID(), null, new NotificationMessage(nextPlayer + " is in checkmate"));
        }
        else if (game.isInStalemate(nextTurn)) {
            connections.broadcast(command.getGameID(), null, new NotificationMessage(nextPlayer + " is in stalemate"));
        }
        else if (game.isInCheck(nextTurn)) {
            connections.broadcast(command.getGameID(), null, new NotificationMessage(nextPlayer + " is in check"));
        }
    }

    private void leave(UserGameCommand command, WsMessageContext ctx) throws Exception {
        GameService gameService = new GameService(gameDAO, authDAO);
        gameService.leave(command.getAuthToken(), command.getGameID());

        String username = authDAO.getAuth(command.getAuthToken()).username();
        var notification = new NotificationMessage(username + " left the game");
        connections.broadcast(command.getGameID(), ctx.session, notification);
        connections.remove(command.getGameID(), ctx.session);
    }

    private void resign(UserGameCommand command) throws Exception{
        GameService gameService = new GameService(gameDAO, authDAO);
        gameService.resign(command.getAuthToken(), command.getGameID());

        String username = authDAO.getAuth(command.getAuthToken()).username();
        var notification = new NotificationMessage(username + " resigned the game");
        connections.broadcast(command.getGameID(), null, notification);
    }

    private String positionToString(ChessPosition pos) {
        char col = (char) ('a' + pos.getColumn() - 1);
        int row = pos.getRow();
        return "" + col + row;
    }
}