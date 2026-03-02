package dataaccess;

import model.GameData;

import java.util.ArrayList;

public interface GameDAO {
    int createGame(String gameName);
    GameData getGame(int gameID);
    ArrayList<GameData> listGames();
    void updateGame(GameData updatedGame);
    void clearGame();
}
