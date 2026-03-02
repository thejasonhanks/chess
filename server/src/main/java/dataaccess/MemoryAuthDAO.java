package dataaccess;

import model.AuthData;
import model.GameData;

import java.util.HashMap;
import java.util.UUID;

public class MemoryAuthDAO implements AuthDAO{
    final private HashMap<String, AuthData> auths = new HashMap<>();
    public static String generateToken() {
        return UUID.randomUUID().toString();
    }

    @Override
    public String createAuth(String username) throws DataAccessException {
        String token = generateToken();
        AuthData authData = new AuthData(token, username);
        auths.put(token, authData);
        return token;
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        return auths.get(authToken);
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        auths.remove(authToken);
    }

    @Override
    public void clearAuth() throws DataAccessException {
        auths.clear();
    }
}
