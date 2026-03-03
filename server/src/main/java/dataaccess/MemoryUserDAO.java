package dataaccess;

import model.UserData;

import java.util.HashMap;

public class MemoryUserDAO implements UserDAO{
    final private HashMap<String, UserData> users = new HashMap<>();

    public HashMap<String, UserData> getUsers() {
        return users;
    }

    @Override
    public void createUser(UserData user) throws DataAccessException {
        user = new UserData(user.username(), user.password(), user.email());

        users.put(user.username(), user);
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        return users.get(username);
    }

    @Override
    public void deleteUser(String username) throws DataAccessException {
        users.remove(username);
    }

    @Override
    public void clearUser() throws DataAccessException {
        users.clear();
    }

}
