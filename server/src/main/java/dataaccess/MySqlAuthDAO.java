package dataaccess;

import model.AuthData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

public class MySqlAuthDAO implements AuthDAO{
    public MySqlAuthDAO() throws DataAccessException{
        configureDatabase();
    }

    @Override
    public String createAuth(String username) throws DataAccessException {
        String token = UUID.randomUUID().toString();
        String statement = "INSERT INTO auth (username, authToken) VALUES (?, ?)";
        try{
            DatabaseManager.executeUpdate(statement, username, token);
        } catch (DataAccessException e) {
            throw new DataAccessException(String.format("Unable to access data: %s", e.getMessage()));
        }
        return token;
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        String statement = "SELECT username, authToken FROM auth WHERE authToken = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(statement)) {
            ps.setString(1, authToken);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new AuthData(
                            rs.getString("authToken"),
                            rs.getString("username")
                    );
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("Unable to access data: %s", e.getMessage()));
        }
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        String statement = "DELETE FROM auth WHERE authToken = ?";
        DatabaseManager.executeUpdate(statement, authToken);
    }

    @Override
    public void clearAuth() throws DataAccessException {
        String statement = "DELETE FROM auth";
        try {
            DatabaseManager.executeUpdate(statement);
        } catch (DataAccessException e) {
            throw new DataAccessException(String.format("Unable to read access data: %s", e.getMessage()));
        }
    }

    @Override
    public HashMap<String, AuthData> getAuths() throws DataAccessException {
        HashMap<String, AuthData> auths = new HashMap<>();
        String statement = "SELECT username, authToken FROM auth";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(statement);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                AuthData authData = new AuthData(
                        rs.getString("authToken"),
                        rs.getString("username")
                );
                auths.put(authData.authToken(), authData);
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
        return auths;
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS auth (
                `username` VARCHAR(225) NOT NULL,
                `authToken` VARCHAR(255) NOT NULL UNIQUE,
                PRIMARY KEY(`username`),
                INDEX(authToken)
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
