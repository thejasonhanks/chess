package handler;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import io.javalin.http.Context;
import service.ClearService;

public class ClearHandler {
    private final ClearService service;

    public ClearHandler(UserDAO userDAO, AuthDAO authDAO, GameDAO gameDAO) {
        this.service = new ClearService(userDAO, gameDAO, authDAO);
    }

    public void clear(Context ctx) throws DataAccessException {
        service.clear();
        ctx.status(200);
    }
}
