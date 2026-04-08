package websocket;

import io.javalin.websocket.*;
import com.google.gson.Gson;
import exception.ResponseException;
import org.eclipse.jetty.websocket.api.Session;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage.*;

import java.io.IOException;

import java.util.HashMap;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final ConnectionManager connections = new ConnectionManager();

    @Override
    public void handleClose(WsCloseContext ctx) throws Exception {
        System.out.println("Websocket closed");
    }

    @Override
    public void handleConnect(WsConnectContext ctx) throws Exception {
        System.out.println("Websocket connected");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(WsMessageContext ctx) throws Exception {
        try {
            Action action = new Gson().fromJson(ctx.message(), Action.class);
            switch (action.type()) {
                case ENTER -> enter(action.visitorName(), ctx.session);
                case EXIT -> exit(action.visitorName(), ctx.session);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void enter(String visitorName, Session session) throws IOException {
        connections.add(session);
        var message = String.format("%s is in the shop", visitorName);
        var notification = new Notification(Notification.Type.ARRIVAL, message);
        connections.broadcast(session, notification);
    }

    private void exit(String visitorName, Session session) throws IOException {
        var message = String.format("%s left the shop", visitorName);
        var notification = new Notification(Notification.Type.DEPARTURE, message);
        connections.broadcast(session, notification);
        connections.remove(session);
    }

    public void makeNoise(String petName, String sound) throws ResponseException {
        try {
            var message = String.format("%s says %s", petName, sound);
            var notification = new Notification(Notification.Type.NOISE, message);
            connections.broadcast(null, notification);
        } catch (Exception ex) {
            throw new ResponseException(ResponseException.Code.ServerError, ex.getMessage());
        }
    }
}

