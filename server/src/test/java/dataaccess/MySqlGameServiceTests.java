package dataaccess;

import chess.ChessGame;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.*;

import static org.junit.jupiter.api.Assertions.*;

public class MySqlGameServiceTests {
    private GameDAO gameDAO;
    private AuthDAO authDAO;
    private GameService service;

    public MySqlGameServiceTests() throws Exception {
    }

    @BeforeEach
    void setUp() {
        gameDAO = new MemoryGameDAO();
        authDAO = new MemoryAuthDAO();
        service = new GameService(gameDAO, authDAO);
    }

    @Test
    void listPositive() throws Exception {
        String token = authDAO.createAuth("myUser");
        CreateRequest request = new CreateRequest("Name");
        service.createGame(token, request);

        ListResult result = service.listGames(token);
        assertNotNull(result);
        assertEquals(1, result.games().size());
    }

    @Test
    void listNegative() throws Exception{
        String token = authDAO.createAuth("myUser");
        assertThrows(UnauthorizedException.class, () -> {
            service.listGames(token + "123");
        });
    }

    @Test
    void createPositive() throws Exception {
        String token = authDAO.createAuth("myUser");
        CreateRequest request = new CreateRequest("Name");
        CreateResult result = service.createGame(token, request);

        assertNotNull(result);
        assertNotNull(gameDAO.getGame(result.gameID()));
    }

    @Test
    void createNegative() throws Exception {
        String token = authDAO.createAuth("myUser");
        CreateRequest request = new CreateRequest("Name");
        assertThrows(UnauthorizedException.class, () -> {
            service.createGame(token + "123", request);
        });
    }

    @Test
    void joinPositive() throws Exception {
        String token = authDAO.createAuth("myUser");
        CreateRequest request1 = new CreateRequest("Name");
        CreateResult result = service.createGame(token, request1);

        JoinRequest request2 = new JoinRequest("WHITE", result.gameID());
        service.joinGame(token, request2);

        GameData game = gameDAO.getGame(result.gameID());
        assertNotNull(game.whiteUsername());
        assertEquals("myUser", game.whiteUsername());
    }

    @Test
    void joinNegative() throws Exception {
        String token = authDAO.createAuth("myUser");
        CreateRequest request1 = new CreateRequest("Name");
        CreateResult result = service.createGame(token, request1);

        GameData newGame = new GameData(result.gameID(), "whiteUser", null,
                "Name", new ChessGame());
        gameDAO.updateGame(newGame);

        JoinRequest request2 = new JoinRequest("WHITE", result.gameID());

        assertThrows(AlreadyTakenException.class, () -> {
            service.joinGame(token, request2);
        });
    }
}