package handler;

import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import service.*;
import io.javalin.http.Context;

public class GameHandler {
    private final Gson gson = new Gson();
    private final GameService service;

    public GameHandler(GameDAO gameDAO, AuthDAO authDAO) {
        this.service = new GameService(gameDAO, authDAO);
    }

    public void listGames(Context ctx) throws Exception {
        String authToken = ctx.header("authorization");

        ListResult result = service.listGames(authToken);

        ctx.status(200);
        ctx.result(gson.toJson(result));
        ctx.contentType("application/json");
    }

    public void createGame(Context ctx) throws Exception {
        String authToken = ctx.header("authorization");

        CreateRequest request =
                gson.fromJson(ctx.body(), CreateRequest.class);

        CreateResult result = service.createGame(authToken, request);

        ctx.status(200);
        ctx.result(gson.toJson(result));
        ctx.contentType("application/json");
    }

    public void joinGame(Context ctx) throws Exception {
        String authToken = ctx.header("authorization");

        JoinRequest request =
                gson.fromJson(ctx.body(), JoinRequest.class);

        service.joinGame(authToken, request);

        ctx.status(200);
        ctx.contentType("application/json");
    }
}
