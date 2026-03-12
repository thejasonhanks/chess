package service;

import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Objects;

public class UserService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO){
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public RegisterResult register(RegisterRequest registerRequest) throws Exception {
        if (registerRequest.username() == null || registerRequest.password() == null){
            throw new BadRequestException("Invalid Request: username or password cannot be null");
        }
        if (userDAO.getUser(registerRequest.username()) != null){
            throw new AlreadyTakenException("Username is already taken");
        }

        String hashedPassword = BCrypt.hashpw(registerRequest.password(), BCrypt.gensalt());

        UserData user = new UserData(
                registerRequest.username(),
                hashedPassword,
                registerRequest.email()
        );

        userDAO.createUser(user);

        String token = authDAO.createAuth(registerRequest.username());
        return new RegisterResult(registerRequest.username(), token);
    }

    public LoginResult login(LoginRequest loginRequest) throws Exception{
        if (loginRequest.username() == null || loginRequest.password() == null){
            throw new BadRequestException("Invalid Request: username or password cannot be null");
        }
        if (userDAO.getUser(loginRequest.username()) == null){
            throw new UnauthorizedException("Invalid request: unauthorized");
        }

        UserData user = userDAO.getUser(loginRequest.username());

        String hashedPassword = BCrypt.hashpw(user.password(), BCrypt.gensalt());
        if (BCrypt.checkpw(user.password(), hashedPassword)){
            throw new UnauthorizedException("Invalid request: wrong password");
        }

        String token = authDAO.createAuth(loginRequest.username());
        return new LoginResult(loginRequest.username(), token);
    }

    public void logout(String authToken) throws Exception{
        if (authToken == null){
            throw new UnauthorizedException("Invalid request: unauthorized");
        }
        AuthData auth = authDAO.getAuth(authToken);
        if (auth == null){
            throw new UnauthorizedException("Invalid request: unauthorized");
        }
        authDAO.deleteAuth(authToken);
    }
}
