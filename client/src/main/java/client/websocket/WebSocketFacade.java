package client.websocket;

import chess.ChessMove;
import com.google.gson.Gson;
import exception.ResponseException;
import websocket.commands.*;
import websocket.messages.*;

import jakarta.websocket.*;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

@ClientEndpoint
public class WebSocketFacade extends Endpoint {
    private Session session;
    private final NotificationHandler notificationHandler;
    private final Gson gson = new Gson();

    public WebSocketFacade(String url, NotificationHandler notificationHandler) throws ResponseException{
        this.notificationHandler = notificationHandler;
        try {
            url = url.replace("http", "ws") + "/ws";
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            container.connectToServer(this, new URI(url));
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ResponseException(ResponseException.Code.ServerError, ex.getMessage());
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
        this.session = session;
        this.session.addMessageHandler((MessageHandler.Whole<String>) this::handleMessage);
    }

    private void handleMessage(String message) {
        try {
            ServerMessage base = gson.fromJson(message, ServerMessage.class);

            switch (base.getServerMessageType()) {
                case LOAD_GAME -> notificationHandler.loadGame(gson.fromJson(message, LoadGameMessage.class));
                case NOTIFICATION -> notificationHandler.notify(gson.fromJson(message, NotificationMessage.class));
                case ERROR -> notificationHandler.error(gson.fromJson(message, ErrorMessage.class));
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void sendConnect(String authToken, int gameID) throws IOException {
        UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID);
        send(command);
    }

    public void sendMakeMove(String authToken, int gameID, ChessMove move) throws IOException {
        MakeMoveCommand command = new MakeMoveCommand(authToken, gameID, move);
        send(command);
    }

    public void sendLeave(String authToken, int gameID) throws IOException {
        UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID);
        send(command);
    }

    public void sendResign(String authToken, int gameID) throws IOException {
        UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.RESIGN, authToken, gameID);
        send(command);
    }

    private void send(Object command) throws IOException {
        if (session != null && session.isOpen()) {
            session.getBasicRemote().sendText(gson.toJson(command));
        } else {
            throw new IOException("WebSocket is not open");
        }
    }
}
