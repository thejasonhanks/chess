package dataaccess;

import model.AuthData;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

public class MySqlAuthDAO implements AuthDAO{
    public MySqlAuthDAO() throws DataAccessException{
        configureDatabase();
    }

    @Override
    public String createAuth(String username) throws DataAccessException {
        return "";
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        return null;
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {

    }

    @Override
    public void clearAuth() throws DataAccessException {

    }

    @Override
    public HashMap<String, AuthData> getAuths() {
        return null;
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS auth (
                `username` varchar(225) NOT NULL,
                `authToken` varchar(255) NOT NULL,
                PRIMARY KEY(`authToken`),
                INDEX(username)
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
