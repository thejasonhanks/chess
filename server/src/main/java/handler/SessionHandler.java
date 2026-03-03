package handler;

import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import io.javalin.http.Context;
import service.*;

public class SessionHandler {
    private final Gson gson = new Gson();
    private final UserService service;

    public SessionHandler(UserDAO userDAO, AuthDAO authDAO) {
        this.service = new UserService(userDAO, authDAO);
    }

    public void login(Context ctx) throws Exception {
        LoginRequest request =
                gson.fromJson(ctx.body(), LoginRequest.class);

        LoginResult result = service.login(request);

        ctx.status(200);
        ctx.result(gson.toJson(result));
        ctx.contentType("application/json");
    }

    public void logout(Context ctx) throws Exception {
        String authToken = ctx.header("authorization");

        service.logout(authToken);

        ctx.status(200);
    }
}
