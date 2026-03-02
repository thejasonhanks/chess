package handler;

import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import service.*;
import io.javalin.http.Context;

import java.util.Map;

public class UserHandler {
    private final Gson gson = new Gson();
    private final UserService service;

    public UserHandler(UserDAO userDAO, AuthDAO authDAO) {
        this.service = new UserService(userDAO, authDAO);
    }


    public void register(Context ctx) {
        try {
            RegisterRequest request =
                    gson.fromJson(ctx.body(), RegisterRequest.class);

            RegisterResult result = service.register(request);

            ctx.status(200);
            ctx.result(gson.toJson(result));
            ctx.contentType("application/json");
        } catch (BadRequestException e) {
            ctx.status(400);
            ctx.json(Map.of("message", "Error: " + e.getMessage()));
        } catch (AlreadyTakenException e) {
            ctx.status(403);
            ctx.json(Map.of("message", "Error: " + e.getMessage()));
        } catch (Exception e) {
            ctx.status(500);
            ctx.json(Map.of("message", "Error: " + e.getMessage()));
        }
    }
}
