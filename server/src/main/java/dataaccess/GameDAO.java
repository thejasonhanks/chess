package dataaccess;

import model.GameData;
import model.UserData;
import service.CreateRequest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public interface GameDAO {
    int createGame(String gameName) throws DataAccessException;
    GameData getGame(int gameID) throws DataAccessException;
    ArrayList<GameData> listGames() throws DataAccessException;
    void updateGame(GameData updatedGame) throws DataAccessException;
    void clearGame() throws DataAccessException;

    HashMap<Integer, GameData> getGames() throws DataAccessException;
}
