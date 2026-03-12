package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class MySqlGameDAO implements GameDAO{
    public MySqlGameDAO() throws DataAccessException {
        configureDatabase();
    }

    @Override
    public int createGame(String gameName) throws DataAccessException {
        String statement = "INSERT INTO games (gameName) VALUES (?)";
        return DatabaseManager.executeUpdate(statement, gameName);
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        String statement = "SELECT * FROM games WHERE gameID = ?";
        try (Connection conn = DatabaseManager.getConnection();
            PreparedStatement ps = conn.prepareStatement(statement)) {
            ps.setInt(1, gameID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new GameData(
                            rs.getInt("gameID"),
                            rs.getString("whiteUsername"),
                            rs.getString("blackUsername"),
                            rs.getString("gameName"),
                            rs.getString("game") != null ?
                                    new Gson().fromJson(rs.getString("game"), ChessGame.class)
                                    : new ChessGame()
                    );
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("Unable to read access data: %s", e.getMessage()));
        }
    }

    @Override
    public ArrayList<GameData> listGames() throws DataAccessException {
        String statement = "SELECT * FROM games";
        ArrayList<GameData> games = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(statement);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                games.add(new GameData(
                        rs.getInt("gameID"),
                        rs.getString("whiteUsername"),
                        rs.getString("blackUsername"),
                        rs.getString("gameName"),
                        rs.getString("game") != null ?
                                new Gson().fromJson(rs.getString("game"), ChessGame.class)
                                : new ChessGame()
                ));
            }
        } catch (SQLException e){
            throw new DataAccessException(String.format("Unable to read access data: %s", e.getMessage()));
        }
        return games;
    }

    @Override
    public void updateGame(GameData updatedGame) throws DataAccessException {
        String statement = "UPDATE games SET whiteUsername = ?, blackUsername = ?, " +
                "gameName = ?, game = ? WHERE gameID = ?";
        DatabaseManager.executeUpdate(statement, updatedGame.whiteUsername(), updatedGame.blackUsername(),
                updatedGame.gameName(), new Gson().toJson(updatedGame.game()), updatedGame.gameID());
    }

    @Override
    public void clearGame() throws DataAccessException {
        String statement = "DELETE FROM games";
        DatabaseManager.executeUpdate(statement);
    }

    @Override
    public HashMap<Integer, GameData> getGames() throws DataAccessException {
        HashMap<Integer, GameData> games = new HashMap<>();
        String statement = "SELECT * FROM games";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(statement);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                GameData gameData = new GameData(
                        rs.getInt("gameID"),
                        rs.getString("whiteUsername"),
                        rs.getString("blackUsername"),
                        rs.getString("gameName"),
                        rs.getString("game") != null ?
                                new Gson().fromJson(rs.getString("game"), ChessGame.class)
                                : new ChessGame()
                );
                games.put(gameData.gameID(), gameData);
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
        return games;
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS games (
                `gameID` INT NOT NULL AUTO_INCREMENT,
                `whiteUsername` varchar(225),
                `blackUsername` varchar(225),
                `gameName` varchar(255) NOT NULL,
                `game` TEXT,
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
