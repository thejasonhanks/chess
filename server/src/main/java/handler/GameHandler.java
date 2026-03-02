package handler;

import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import service.*;
import io.javalin.http.Context;

import java.util.Map;

public class GameHandler {
    private final Gson gson = new Gson();
    private final GameService service;

    public GameHandler(GameDAO gameDAO, AuthDAO authDAO) {
        this.service = new GameService(gameDAO, authDAO);
    }

    public void listGames(Context ctx) {
        try {
            String authToken = ctx.header("authorization");

            ListResult result = service.listGames(authToken);

            ctx.status(200);
            ctx.result(gson.toJson(result));
            ctx.contentType("application/json");
        } catch (UnauthorizedException e) {
            ctx.status(401);
            ctx.json(Map.of("message", "Error: " + e.getMessage()));
        } catch (Exception e) {
            ctx.status(500);
            ctx.json(Map.of("message", "Error: " + e.getMessage()));
        }
    }

    public void createGame(Context ctx){
        try {
            String authToken = ctx.header("authorization");

            CreateRequest request =
                    gson.fromJson(ctx.body(), CreateRequest.class);

            CreateResult result = service.createGame(authToken, request);

            ctx.status(200);
            ctx.result(gson.toJson(result));
            ctx.contentType("application/json");
        } catch (BadRequestException e) {
            ctx.status(400);
            ctx.json(Map.of("message", "Error: " + e.getMessage()));
        } catch (UnauthorizedException e) {
            ctx.status(401);
            ctx.json(Map.of("message", "Error: " + e.getMessage()));
        } catch (Exception e) {
            ctx.status(500);
            ctx.json(Map.of("message", "Error: " + e.getMessage()));
        }
    }

    public void joinGame(Context ctx){
        try {
            String authToken = ctx.header("authorization");

            JoinRequest request =
                    gson.fromJson(ctx.body(), JoinRequest.class);

            service.joinGame(authToken, request);

            ctx.status(200);
            ctx.contentType("application/json");
        } catch (BadRequestException e) {
            ctx.status(400);
            ctx.json(Map.of("message", "Error: " + e.getMessage()));
        } catch (UnauthorizedException e) {
            ctx.status(401);
            ctx.json(Map.of("message", "Error: " + e.getMessage()));
        } catch (Exception e) {
            ctx.status(500);
            ctx.json(Map.of("message", "Error: " + e.getMessage()));
        }
    }
}
