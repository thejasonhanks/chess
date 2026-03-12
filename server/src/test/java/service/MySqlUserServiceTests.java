package service;

import dataaccess.*;
import model.UserData;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class MySqlUserServiceTests {
    private UserDAO userDAO;
    private AuthDAO authDAO;
    private UserService service;

    @BeforeEach
    void setUp() throws DataAccessException {
        userDAO = new MySqlUserDAO();
        authDAO = new MySqlAuthDAO();
        service = new UserService(userDAO, authDAO);

        // clear database before each test
        userDAO.clearUser();
        authDAO.clearAuth();
    }

    @Test
    void registerPositive() throws Exception {
        var request = new RegisterRequest("user1", "pass1", "user1@email.com");
        var result = service.register(request);

        assertNotNull(result);
        assertEquals("user1", result.username());
        assertNotNull(result.authToken());

        assertNotNull(userDAO.getUser("user1"));
        assertNotNull(authDAO.getAuth(result.authToken()));
    }

    @Test
    void registerNegativeAlreadyTaken() throws Exception {
        userDAO.createUser(new UserData("user1", "hash", "user1@email.com"));
        var request = new RegisterRequest("user1", "pass1", "user1@email.com");

        assertThrows(AlreadyTakenException.class, () -> service.register(request));
    }

    @Test
    void registerNegativeBadRequest() {
        var request = new RegisterRequest(null, null, null);
        assertThrows(BadRequestException.class, () -> service.register(request));
    }
}
