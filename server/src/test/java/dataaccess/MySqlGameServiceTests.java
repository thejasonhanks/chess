package dataaccess;

import model.*;
import org.junit.jupiter.api.*;
import service.*;

import static org.junit.jupiter.api.Assertions.*;

public class MySqlGameServiceTests {
    private GameDAO gameDAO;
    private AuthDAO authDAO;
    private GameService service;

    @BeforeEach
    void setUp() throws DataAccessException {
        gameDAO = new MySqlGameDAO();
        authDAO = new MySqlAuthDAO();
        service = new GameService(gameDAO, authDAO);

        // clear database before each test
        gameDAO.clearGame();
        authDAO.clearAuth();
    }

    @Test
    void createGamePositive() throws Exception {
        String token = authDAO.createAuth("user1");
        CreateRequest request = new CreateRequest("My Game");
        CreateResult result = service.createGame(token, request);

        assertNotNull(result);
        assertTrue(result.gameID() > 0);

        GameData game = gameDAO.getGame(result.gameID());
        assertNotNull(game);
        assertEquals("My Game", game.gameName());
    }

    @Test
    void createGameNegativeUnauthorized() {
        CreateRequest request = new CreateRequest("My Game");
        assertThrows(UnauthorizedException.class, () -> service.createGame("badToken", request));
    }

    @Test
    void joinGamePositive() throws Exception {
        String token = authDAO.createAuth("user1");
        int gameId = gameDAO.createGame("Game 1");
        JoinRequest join = new JoinRequest("WHITE", gameId);

        service.joinGame(token, join);

        GameData game = gameDAO.getGame(gameId);
        assertEquals("user1", game.whiteUsername());
    }

    @Test
    void joinGameNegativeAlreadyTaken() throws Exception {
        String token1 = authDAO.createAuth("user1");
        int gameId = gameDAO.createGame("Game 1");

        // Manually set white player
        GameData game = gameDAO.getGame(gameId);
        GameData updated = new GameData(game.gameID(), "user2", game.blackUsername(), game.gameName(), game.game());
        gameDAO.updateGame(updated);

        JoinRequest join = new JoinRequest("WHITE", gameId);
        assertThrows(AlreadyTakenException.class, () -> service.joinGame(token1, join));
    }
}