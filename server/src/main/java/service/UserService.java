package service;

import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;

import java.util.Objects;
import java.util.UUID;

public class UserService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO){
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public RegisterResult register(RegisterRequest registerRequest) throws Exception {
        if (userDAO.getUser(registerRequest.username()) != null){
            throw new AlreadyTakenException("Error: username already taken");
        }

        UserData user = new UserData(
                registerRequest.username(),
                registerRequest.password(),
                registerRequest.email()
        );

        userDAO.createUser(user);

        String token = UUID.randomUUID().toString();
        authDAO.createAuth(new AuthData(token, registerRequest.username()));
        return new RegisterResult(registerRequest.username(), token);
    }
    public LoginResult login(LoginRequest loginRequest) throws Exception{
        if (userDAO.getUser(loginRequest.username()) == null){
            throw new BadRequest("Error: username doesn't exist");
        }

        UserData user = userDAO.getUser(loginRequest.username());

        if (!Objects.equals(user.password(), loginRequest.password())){
            throw new BadRequest("Error: password doesn't match");
        }
        String token = UUID.randomUUID().toString();
        authDAO.createAuth(new AuthData(token, loginRequest.username()));
        return new LoginResult(loginRequest.username(), token);
    }
    public void logout(LogoutRequest logoutRequest) {
        AuthData auth = authDAO.getAuth(logoutRequest.authToken());
        authDAO.deleteAuth(auth);
    }
}
