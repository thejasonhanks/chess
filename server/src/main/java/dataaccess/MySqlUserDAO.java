package dataaccess;

import model.UserData;

import java.util.HashMap;

import model.*;

import java.sql.*;


public class MySqlUserDAO implements UserDAO{
    public MySqlUserDAO() throws DataAccessException{
        configureDatabase();
    }

    @Override
    public void createUser(UserData user) throws DataAccessException {
        var statement = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        try {
            DatabaseManager.executeUpdate(statement, user.username(), user.password(), user.email());
        } catch (DataAccessException e){
            throw new DataAccessException(String.format("Unable to access data: %s", e.getMessage()));
        }
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username, password, email FROM users WHERE username = ?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return new UserData(
                                rs.getString("username"),
                                rs.getString("password"),
                                rs.getString("email")
                        );
                    } else {
                        return null;
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
    }

    @Override
    public void clearUser() throws DataAccessException {
        String statement = "DELETE FROM users";
        try {
            DatabaseManager.executeUpdate(statement);
        } catch (DataAccessException e) {
            throw new DataAccessException(String.format("Unable to read access data: %s", e.getMessage()));
        }
    }


    @Override
    public HashMap<String, UserData> getUsers() throws DataAccessException {
        HashMap<String, UserData> users = new HashMap<>();
        String statement = "SELECT username, password, email FROM users";
        try (Connection conn = DatabaseManager.getConnection();
            PreparedStatement ps = conn.prepareStatement(statement);
            ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                UserData user = new UserData(
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("email")
                );
                users.put(user.username(), user);
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
        return users;
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS users (
                `username` varchar(225) NOT NULL,
                `password` varchar(225) NOT NULL,
                `email` varchar(255) NOT NULL,
                PRIMARY KEY(`username`)
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
