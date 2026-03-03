package server;

import com.google.gson.Gson;
import dataaccess.*;
import handler.ClearHandler;
import handler.GameHandler;
import handler.SessionHandler;
import handler.UserHandler;
import io.javalin.*;
import org.eclipse.jetty.server.Authentication;
import service.AlreadyTakenException;
import service.BadRequestException;
import service.UnauthorizedException;


public class Server {
    private final Gson gson = new Gson();
    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));
        javalin.exception(BadRequestException.class, (e, ctx) -> {
           ctx.status(400);
           ctx.result("{\"message\":\"Error: bad request\"}");
           ctx.contentType("application/json");
        });
        javalin.exception(UnauthorizedException.class, (e,ctx) -> {
            ctx.status(401);
            ctx.result("{\"message\":\"Error: unauthorized\"}");
            ctx.contentType("application/json");
        });
        javalin.exception(AlreadyTakenException.class, (e, ctx) -> {
            ctx.status(403);
            ctx.result("{\"message\":\"Error: already taken\"}");
            ctx.contentType("application/json");
        });

        UserDAO userDAO = new MemoryUserDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        GameDAO gameDAO = new MemoryGameDAO();
        UserHandler userHandler = new UserHandler(userDAO, authDAO);
        GameHandler gameHandler = new GameHandler(gameDAO, authDAO);
        SessionHandler sessionHandler = new SessionHandler(userDAO, authDAO);
        ClearHandler clearHandler = new ClearHandler(userDAO, authDAO, gameDAO);

        javalin.post("/user", userHandler::register);

        javalin.post("/session", sessionHandler::login);
        javalin.delete("/session", sessionHandler::logout);

        javalin.get("/game", gameHandler::listGames);
        javalin.post("/game", gameHandler::createGame);
        javalin.put("/game", gameHandler::joinGame);

        javalin.delete("/db", clearHandler::clear);
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}