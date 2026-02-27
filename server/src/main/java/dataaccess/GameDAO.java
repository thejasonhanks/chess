package dataaccess;

import model.GameData;
import java.util.List;

public interface GameDAO {
    void createGame(GameData game);
    GameData getGame(int gameID);
    List<GameData> listGames();
    GameData updateGame(int gameID);
    void clearGame();
}
