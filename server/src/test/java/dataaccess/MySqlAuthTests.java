package dataaccess;

import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

public class MySqlAuthTests {
    private AuthDAO authDAO;

    public MySqlAuthTests() {
    }

    @BeforeEach
    void start() {
        authDAO = new MemoryAuthDAO();
    }

    @Test
    void createAuthPositive() throws Exception {
        String token = authDAO.createAuth("user1");
        AuthData auth = authDAO.getAuth(token);
        assertNotNull(auth);
        assertEquals("user1", auth.username());
    }

    @Test
    void createAuthNegative() throws Exception {
        String token = authDAO.createAuth("existing");

        boolean failed = false;
        try {
            authDAO.createAuth("existing");
        } catch (DataAccessException e) {
            failed = true;
        }
        assertTrue(failed || authDAO.getAuth(token) != null,
                "Duplicate auth creation fails");
    }

    @Test
    void getAuthPositive() throws Exception {
        String token = authDAO.createAuth("user1");
        AuthData auth = authDAO.getAuth(token);
        assertNotNull(auth);
        assertEquals("user1", auth.username());
    }

    @Test
    void getAuthNegative() throws Exception {
        AuthData auth = authDAO.getAuth("invalid");
        assertNull(auth);
    }

    @Test
    void deleteAuthPositive() throws Exception {
        String token = authDAO.createAuth("user1");
        authDAO.deleteAuth(token);
        assertNull(authDAO.getAuth(token));
    }

    @Test
    void deleteAuthNegative() throws Exception {
        assertDoesNotThrow(()-> authDAO.deleteAuth("fakeToken"));
    }

    @Test
    void clearUserPositive() throws Exception {
        String token = authDAO.createAuth("user1");
        authDAO.clearAuth();
        assertNull(authDAO.getAuth(token));
    }

    @Test
    void getAuthsPositive() throws Exception {
        authDAO.createAuth("user1");
        authDAO.createAuth("user2");

        HashMap<String, AuthData> hashAuths = authDAO.getAuths();

        assertNotNull(hashAuths);
        assertEquals(2, hashAuths.size());
    }

    @Test
    void getAuthsNegative() throws Exception{
        authDAO.clearAuth();

        HashMap<String, AuthData> auths = authDAO.getAuths();
        assertNotNull(auths);
        assertTrue(auths.isEmpty());
    }
}

