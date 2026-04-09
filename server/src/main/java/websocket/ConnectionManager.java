package websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<Integer, ConcurrentHashMap<Session, Session>> connections = new ConcurrentHashMap<>();

    public void add(int gameID, Session session) {
        connections.putIfAbsent(gameID, new ConcurrentHashMap<>());
        connections.get(gameID).put(session, session);
    }

    public void remove(int gameID, Session session) {
        var gameConnections = connections.get(gameID);
        if (gameConnections != null) {
            gameConnections.remove(session);
        }
    }

    public void broadcast(int gameID, Session excludeSession, ServerMessage message) throws IOException {
        var gameConnections = connections.get(gameID);
        if (gameConnections == null) {return;}
        String msg = new Gson().toJson(message);

        for (Session c : gameConnections.values()) {
            if (c.isOpen()) {
                if (!c.equals(excludeSession)) {
                    c.getRemote().sendString(msg);
                }
            }
        }
    }
}
