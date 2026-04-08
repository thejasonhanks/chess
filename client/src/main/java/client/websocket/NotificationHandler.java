package client.websocket;

import websocket.messages.*;

public interface NotificationHandler {
    void notify(NotificationMessage notification);
    void loadGame(LoadGameMessage game);
    void error(ErrorMessage e);
}
