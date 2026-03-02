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
        if (registerRequest.username() == null || registerRequest.password() == null){
            throw new BadRequestException("missing username or password");
        }
        if (userDAO.getUser(registerRequest.username()) != null){
            throw new AlreadyTakenException("username already taken");
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
        if (loginRequest.username() == null || loginRequest.password() == null){
            throw new BadRequestException("missing username or password");
        }
        if (userDAO.getUser(loginRequest.username()) == null){
            throw new BadRequestException("username doesn't exist");
        }

        UserData user = userDAO.getUser(loginRequest.username());

        if (!Objects.equals(user.password(), loginRequest.password())){
            throw new BadRequestException("password doesn't match");
        }
        String token = UUID.randomUUID().toString();
        authDAO.createAuth(new AuthData(token, loginRequest.username()));
        return new LoginResult(loginRequest.username(), token);
    }

    public void logout(String authToken) {
        if (authToken == null){
            throw new UnauthorizedException("unauthorized");
        }
        AuthData auth = authDAO.getAuth(authToken);
        if (auth == null){
            throw new UnauthorizedException("unauthorized");
        }
        authDAO.deleteAuth(auth);
    }
}
