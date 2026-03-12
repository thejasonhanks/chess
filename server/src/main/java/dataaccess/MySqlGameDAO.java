package dataaccess;

import model.GameData;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class MySqlGameDAO implements GameDAO{
    public MySqlGameDAO() throws DataAccessException {
        configureDatabase();
    }

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

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS games (
                `gameID` INT NOT NULL AUTO_INCREMENT,
                `whiteUsername` varchar(225),
                `blackUsername` varchar(225),
                `gameName` varchar(255) NOT NULL,
                `game` TEXT DEFAULT NULL,
                PRIMARY KEY(`gameID`),
                INDEX(whiteUsername),
                INDEX(blackUsername),
                INDEX(gameName)
                )
"""
    };

    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (Connection conn = DatabaseManager.getConnection()) {
            for (String statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }
}
