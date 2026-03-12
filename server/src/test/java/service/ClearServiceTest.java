package service;

import dataaccess.*;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ClearServiceTest {
    private GameDAO gameDAO;
    private AuthDAO authDAO;
    private UserDAO userDAO;
    private ClearService service;

    public ClearServiceTest() throws Exception {
    }

    @BeforeEach
    void setUp() throws DataAccessException {
        gameDAO = new MemoryGameDAO();
        authDAO = new MemoryAuthDAO();
        userDAO = new MemoryUserDAO();
        service = new ClearService(userDAO, gameDAO, authDAO);
    }

    @Test
    void clearPositive() throws Exception {
        gameDAO.createGame("Name");
        authDAO.createAuth("myUser");
        userDAO.createUser(new UserData("myUser", "1234", "email@email.com"));

        service.clear();

        assertEquals(0, gameDAO.getGames().size());
        assertEquals(0, userDAO.getUsers().size());
        assertEquals(0, authDAO.getAuths().size());
    }
}
