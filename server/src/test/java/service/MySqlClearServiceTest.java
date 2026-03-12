package service;

import dataaccess.*;
import model.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class MySqlClearServiceTest {
    private UserDAO userDAO;
    private AuthDAO authDAO;
    private GameDAO gameDAO;
    private ClearService clearService;

    @BeforeEach
    void setUp() throws DataAccessException {
        userDAO = new MySqlUserDAO();
        authDAO = new MySqlAuthDAO();
        gameDAO = new MySqlGameDAO();
        clearService = new ClearService(userDAO, gameDAO, authDAO);

        // Ensure database starts empty
        userDAO.clearUser();
        authDAO.clearAuth();
        gameDAO.clearGame();
    }

    @Test
    void clearPositive() throws Exception {
        // Add some sample data
        userDAO.createUser(new UserData("user1", "passhash", "user1@email.com"));
        authDAO.createAuth("user1");
        gameDAO.createGame("Test Game");

        // Call the clear method
        clearService.clear();

        // Assert all tables are empty
        assertTrue(userDAO.getUsers().isEmpty(), "Users table should be empty after clear");
        assertTrue(authDAO.getAuths().isEmpty(), "Auth table should be empty after clear");
        assertTrue(gameDAO.getGames().isEmpty(), "Games table should be empty after clear");
    }
}
