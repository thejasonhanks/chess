package client.websocket;

import com.google.gson.Gson;

public class WebSocketFacade {
    private Session session;

    public WebSocketFacade(String url, NotificationHandler handler){
        // connect to ws://localhost:port/ws
    }

    public void sendCommand(Object command) {
        session.getRemote().sendString(new Gson().toJson(command));
    }
}
