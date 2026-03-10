package dataaccess;

import model.AuthData;

import java.util.HashMap;

public class MySqlAuthDAO implements AuthDAO{
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
}
