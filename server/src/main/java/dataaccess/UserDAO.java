package dataaccess;

import model.UserData;

import java.util.HashMap;

public interface UserDAO {
    void createUser(UserData user) throws DataAccessException;
    UserData getUser(String username) throws DataAccessException;
    void clearUser() throws DataAccessException;

    HashMap<String, UserData> getUsers() throws DataAccessException;
}
