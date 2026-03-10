package dataaccess;

import model.UserData;

import java.util.HashMap;

public class MySqlUserDAO implements UserDAO{
    @Override
    public void createUser(UserData user) throws DataAccessException {

    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        return null;
    }

    @Override
    public void clearUser() throws DataAccessException {

    }

    @Override
    public HashMap<String, UserData> getUsers() {
        return null;
    }
}
