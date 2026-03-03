package handler;

import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import service.*;
import io.javalin.http.Context;

public class UserHandler {
    private final Gson gson = new Gson();
    private final UserService service;

    public UserHandler(UserDAO userDAO, AuthDAO authDAO) {
        this.service = new UserService(userDAO, authDAO);
    }


    public void register(Context ctx) throws Exception {
        RegisterRequest request =
                gson.fromJson(ctx.body(), RegisterRequest.class);

        RegisterResult result = service.register(request);

        ctx.status(200);
        ctx.result(gson.toJson(result));
        ctx.contentType("application/json");
    }
}
