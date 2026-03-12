package dataaccess;

import model.AuthData;

import java.util.HashMap;

public interface AuthDAO {
    String createAuth(String username) throws DataAccessException;
    AuthData getAuth(String authToken) throws DataAccessException;
    void deleteAuth(String authToken) throws DataAccessException;
    void clearAuth() throws DataAccessException;

    HashMap<String, AuthData> getAuths() throws DataAccessException;
}
