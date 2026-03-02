package dataaccess;

import chess.ChessGame;
import model.GameData;
import service.BadRequestException;

import java.util.ArrayList;
import java.util.HashMap;

public class MemoryGameDAO implements GameDAO{
    final private HashMap<Integer, GameData> games = new HashMap<>();
    private int nextGameID = 1;

    @Override
    public int createGame(String gameName) throws DataAccessException {
        int id = nextGameID++;
        GameData game = new GameData(id, null, null, gameName, new ChessGame());

        games.put(game.gameID(), game);

        return id;
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        return games.get(gameID);
    }

    @Override
    public ArrayList<GameData> listGames() throws DataAccessException {
        return new ArrayList<>(games.values());
    }

    @Override
    public void updateGame(GameData updatedGame) throws DataAccessException {
        int id = updatedGame.gameID();

        if (!games.containsKey(id)){
            throw new BadRequestException("game doesn't exist");
        }

        games.put(id, updatedGame);
    }

    @Override
    public void clearGame() throws DataAccessException {

    }
}
