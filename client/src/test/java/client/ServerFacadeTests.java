package client;

import exception.ResponseException;
import org.junit.jupiter.api.*;
import request.CreateRequest;
import request.JoinRequest;
import request.LoginRequest;
import request.RegisterRequest;
import server.Server;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade("http://localhost:" + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @BeforeEach
    void clearDB() throws Exception {
        facade.clear();
    }

    @Test
    void registerPositive() throws Exception {
        var result = facade.register(new RegisterRequest("user", "password", "email@email.com"));
        assertTrue(result.authToken().length() > 10);
    }

    @Test
    void registerNegative() throws Exception {
        facade.register(new RegisterRequest("user", "password", "email@email.com"));

        assertThrows(ResponseException.class, () -> {
            facade.register(new RegisterRequest("user", "password", "email@email.com"));
        });
    }

    @Test
    void loginPositive() throws Exception {
        facade.register(new RegisterRequest("user", "password", "email@email.com"));

        var result = facade.login(new LoginRequest("user", "password"));
        assertNotNull(result.authToken());
    }

    @Test
    void loginNegative() throws Exception{
        assertThrows(ResponseException.class, () -> {
            facade.login(new LoginRequest("bad", "wrong"));
        });
    }

    @Test
    void logoutPositive() throws Exception {
        var user = facade.register(new RegisterRequest("user", "password", "email@email.com"));

        assertDoesNotThrow(() -> {
            facade.logout((user.authToken()));
        });
    }

    @Test
    void logoutNegative() throws Exception {
        assertThrows(ResponseException.class, ()->{
            facade.logout("badToken");
        });
    }

    @Test
    void createGamePositive() throws Exception{
        var user = facade.register(new RegisterRequest("user", "password", "email@email.com"));

        var result = facade.createGame(user.authToken(), new CreateRequest("name"));
        assertTrue(result.gameID() > 0);
    }

    @Test
    void createGameNegative() throws Exception{
        assertThrows(ResponseException.class, () ->{
            facade.createGame("badToken", new CreateRequest("name"));
        });
    }

    @Test
    void listGamesPositive() throws Exception {
        var user = facade.register(new RegisterRequest("user", "password", "email@email.com"));

        facade.createGame(user.authToken(), new CreateRequest("name"));
        var list = facade.listGames(user.authToken());

        assertFalse(list.games().isEmpty());
    }

    @Test
    void listGamesNegative() throws Exception {
        assertThrows(ResponseException.class, () -> {
            facade.listGames("badToken");
        });
    }

    @Test
    void joinGamePositive() throws Exception{
        var user = facade.register(new RegisterRequest("user", "password", "email@email.com"));
        var game = facade.createGame(user.authToken(), new CreateRequest("game"));

        assertDoesNotThrow(() -> {
            facade.joinGame(user.authToken(), new JoinRequest("WHITE", game.gameID()));
        });
    }

    @Test
    void joinGameNegative() throws Exception {
        var user = facade.register(new RegisterRequest("user", "password", "email@email.com"));
        var game = facade.createGame(user.authToken(), new CreateRequest("name"));

        assertThrows(ResponseException.class, () -> {
            facade.joinGame("badToken", new JoinRequest("WHITE", game.gameID()));
        });
    }

    @Test
    void clearPositive() throws Exception{
        var user = facade.register(new RegisterRequest("user", "password", "email@email.com"));

        facade.clear();

        assertThrows(ResponseException.class, ()-> {
            facade.listGames(user.authToken());
        });
    }

}
