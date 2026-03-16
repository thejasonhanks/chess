package dataaccess;

import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.*;

import java.util.Collection;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

public class MySqlGameTests {
    private GameDAO gameDAO;
    private AuthDAO authDAO;
    private GameService service;

    public MySqlGameTests() {
    }

    @BeforeEach
    void start() throws DataAccessException {
        gameDAO = new MySqlGameDAO();
        authDAO = new MySqlAuthDAO();
        service = new GameService(gameDAO, authDAO);
    }

    @Test
    void createGamePositive() throws Exception {
        int id = gameDAO.createGame("Name");

        GameData game = gameDAO.getGame(id);

        assertNotNull(game);
        assertEquals("Name", game.gameName());
    }

    @Test
    void createGameNegative() throws Exception {
        assertThrows(DataAccessException.class, () -> {
            gameDAO.createGame(null);
        });
    }

    @Test
    void getGamePositive() throws Exception {
        int id = gameDAO.createGame("Name");
        GameData game = gameDAO.getGame(id);

        assertNotNull(game);
        assertEquals(id, game.gameID());
    }

    @Test
    void getGameNegative() throws Exception {
        GameData game = gameDAO.getGame(67);
        assertNull(game);
    }

    @Test
    void listGamesPositive() throws Exception {
        gameDAO.createGame("Name");
        gameDAO.createGame("Name2");

        Collection<GameData> games = gameDAO.listGames();

        assertNotNull(games);
        assertEquals(2, games.size());
    }

    @Test
    void listGamesNegative() throws Exception{
        String authToken = authDAO.createAuth("user1");
        assertThrows(UnauthorizedException.class, () -> {
            service.listGames(authToken + "321");
        });
    }

    @Test
    void updateGamePositive() throws Exception {
        int id = gameDAO.createGame("Name");
        GameData game = gameDAO.getGame(id);
        GameData updated = new GameData(id, "white",
                "black", "Name2", game.game());
        gameDAO.updateGame(updated);

        GameData newGame = gameDAO.getGame(id);
        assertEquals("Name2", newGame.gameName());
        assertEquals("white", newGame.whiteUsername());
    }

    @Test
    void updateGameNegative() throws Exception {
        GameData game = new GameData(67, "white", "black", "nonexistent", null);

        boolean failed = false;
        try {
            gameDAO.updateGame(game);
        } catch (DataAccessException e) {
            failed = true;
        }

        assertTrue(failed || gameDAO.getGame(67) == null,
                "Updating a nonexistent game fails");
    }

    @Test
    void getGamesPositive() throws Exception {
        gameDAO.createGame("Name1");
        gameDAO.createGame("Name3");

        HashMap<Integer, GameData> hashGames = gameDAO.getGames();

        assertNotNull(hashGames);
        assertTrue(hashGames.size() >= 2);
    }

    @Test
    void getGamesNegative() throws Exception{
        gameDAO.clearGame();

        HashMap<Integer, GameData> games = gameDAO.getGames();
        assertNotNull(games);
        assertTrue(games.isEmpty());
    }

    @Test
    void clearGamePositive() throws Exception {
        int id = gameDAO.createGame("Name");
        gameDAO.clearGame();
        assertNull(gameDAO.getGame(id));
    }
}