package service;

import dataaccess.*;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTests {
    private UserDAO userDAO;
    private AuthDAO authDAO;
    private UserService service;

    public UserServiceTests() throws Exception {
    }

    @BeforeEach
    void setUp() {
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        service = new UserService(userDAO, authDAO);
    }

    @Test
    void registerPositive() throws Exception {
        RegisterRequest request = new RegisterRequest("myUser", "1234", "email@email.com");
        RegisterResult result = service.register(request);

        assertNotNull(result);
        assertEquals("myUser", result.username());
        assertNotNull(result.authToken());

        assertNotNull(userDAO.getUser("myUser"));
        assertNotNull(authDAO.getAuth(result.authToken()));
    }

    @Test
    void registerNegative() throws Exception{
        userDAO.createUser(new UserData("myUser", "password", "email@email.com"));
        RegisterRequest request = new RegisterRequest("myUser", "1234", "email@email.com");

        assertThrows(AlreadyTakenException.class, () -> {
            service.register(request);
        });
    }

    @Test
    void loginPositive() throws Exception {
        String hashed = BCrypt.hashpw("1234", BCrypt.gensalt());
        userDAO.createUser(new UserData("myUser", hashed, "email@email.com"));
        LoginRequest request = new LoginRequest("myUser", "1234");
        LoginResult result = service.login(request);

        assertNotNull(authDAO.getAuth(result.authToken()));
        assertEquals("myUser", result.username());
        assertNotNull(result.authToken());
    }

    @Test
    void loginNegative() throws Exception {
        String hashed = BCrypt.hashpw("1234", BCrypt.gensalt());
        userDAO.createUser(new UserData("myUser", hashed, "email@email.com"));
        LoginRequest request = new LoginRequest("myUser", "4321");
        assertThrows(UnauthorizedException.class, () -> {
            service.login(request);
        });
    }

    @Test
    void logoutPositive() throws Exception {
        String token = authDAO.createAuth("myUser");
        service.logout(token);

        assertNull(authDAO.getAuth(token));
    }

    @Test
    void logoutNegative() throws Exception {
        String token = authDAO.createAuth("myUser");
        assertThrows(UnauthorizedException.class, () -> {
            service.logout(token + "123");
        });
    }
}
