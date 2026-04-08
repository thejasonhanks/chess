package client.websocket;

public interface NotificationHandler {
    void notify(Notification notification);
    void loadGame(GameData game);
    void error(Exception e);
}
