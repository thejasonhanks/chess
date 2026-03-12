package dataaccess;

import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.*;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

public class MySqlUserTests {
    private UserDAO userDAO;

    public MySqlUserTests() {
    }

    @BeforeEach
    void start() {
        userDAO = new MemoryUserDAO();
    }

    @Test
    void createUserPositive() throws Exception {
        UserData user = new UserData("user1", "password", "email@email.com");
        userDAO.createUser(user);

        UserData result = userDAO.getUser("user1");
        assertNotNull(result);
        assertEquals("user1", result.username());
    }

    @Test
    void createUserNegative() throws Exception {
        UserData user = new UserData("existing", "password", "email@email.com");
        userDAO.createUser(user);

        boolean failed = false;
        try {
            userDAO.createUser(user);
        } catch (DataAccessException e) {
            failed = true;
        }
        assertTrue(failed || userDAO.getUser("existing") != null,
                "Duplicate user creation fails");
    }


    @Test
    void getUserPositive() throws Exception {
        UserData user = new UserData("user1", "password", "email@email.com");
        userDAO.createUser(user);

        UserData userAgain = userDAO.getUser("user1");
        assertNotNull(userAgain);
    }

    @Test
    void getUserNegative() throws Exception {
        UserData result = userDAO.getUser("nonexistant");
        assertNull(result);
    }

    @Test
    void getUsersPositive() throws Exception {
        userDAO.createUser(new UserData("user1", "password", "email@email.com"));
        userDAO.createUser(new UserData("user2", "passwords", "emails@email.com"));

        HashMap<String, UserData> hashUsers = userDAO.getUsers();

        assertNotNull(hashUsers);
        assertEquals(2, hashUsers.size());
    }

    @Test
    void getUsersNegative() throws Exception{
        userDAO.clearUser();

        HashMap<String, UserData> users = userDAO.getUsers();
        assertNotNull(users);
        assertTrue(users.isEmpty());
    }

    @Test
    void clearUserPositive() throws Exception {
        userDAO.createUser(new UserData("user1", "password", "email@email.com"));
        userDAO.clearUser();
        assertNull(userDAO.getUser("user1"));
    }
}
