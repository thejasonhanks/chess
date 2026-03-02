package dataaccess;

import model.GameData;

import java.util.ArrayList;

public interface GameDAO {
    int createGame(String gameName) throws DataAccessException;
    GameData getGame(int gameID) throws DataAccessException;
    ArrayList<GameData> listGames() throws DataAccessException;
    void updateGame(GameData updatedGame) throws DataAccessException;
    void clearGame() throws DataAccessException;
}
