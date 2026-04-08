package server;

import dataaccess.*;
import handler.ClearHandler;
import handler.GameHandler;
import handler.SessionHandler;
import handler.UserHandler;
import io.javalin.*;
import service.AlreadyTakenException;
import service.BadRequestException;
import service.UnauthorizedException;


public class Server {
    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));
        javalin.exception(BadRequestException.class, (e, ctx) -> {
           ctx.status(400);
           ctx.result("{\"message\":\"" + e.getMessage() + "\"}");
           ctx.contentType("application/json");
        });
        javalin.exception(UnauthorizedException.class, (e,ctx) -> {
            ctx.status(401);
            ctx.result("{\"message\":\"" + e.getMessage() + "\"}");
            ctx.contentType("application/json");
        });
        javalin.exception(AlreadyTakenException.class, (e, ctx) -> {
            ctx.status(403);
            ctx.result("{\"message\":\"" + e.getMessage() + "\"}");
            ctx.contentType("application/json");
        });
        javalin.exception(Exception.class, (e, ctx) -> {
            ctx.status(500);
            ctx.result("{\"message\":\"Error:" + e.getMessage() + "\"}");
            ctx.contentType("application/json");
        });

        UserDAO userDAO;
        AuthDAO authDAO;
        GameDAO gameDAO;
        try {
            userDAO = new MySqlUserDAO();
            authDAO = new MySqlAuthDAO();
            gameDAO = new MySqlGameDAO();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
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

        javalin.ws("/ws", ws -> {
            ws.onConnect(ctx -> {
                //store session
            });
            ws.onMessage(ctx -> {
                //parse incoming command
            });
            ws.onClose(ctx -> {
                //cleanup
            });
        });
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}