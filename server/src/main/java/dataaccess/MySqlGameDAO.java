package dataaccess;

import model.GameData;

import java.util.ArrayList;
import java.util.HashMap;

public class MySqlGameDAO implements GameDAO{
    @Override
    public int createGame(String gameName) throws DataAccessException {
        return 0;
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        return null;
    }

    @Override
    public ArrayList<GameData> listGames() throws DataAccessException {
        return null;
    }

    @Override
    public void updateGame(GameData updatedGame) throws DataAccessException {

    }

    @Override
    public void clearGame() throws DataAccessException {

    }

    @Override
    public HashMap<Integer, GameData> getGames() {
        return null;
    }
}
