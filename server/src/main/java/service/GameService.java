package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import model.AuthData;
import model.GameData;

import java.util.ArrayList;

public class GameService {
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;


    public GameService(GameDAO gameDAO, AuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public ListResult listGames(String authToken) throws Exception {
        AuthData auth = authDAO.getAuth(authToken);
        if (auth == null){
            throw new UnauthorizedException();
        }
        ArrayList<GameData> games = gameDAO.listGames();
        return new ListResult(games);
    }

    public CreateResult createGame(String authToken, CreateRequest request) throws Exception {
        if (request.gameName() == null){
            throw new BadRequestException();
        }

        AuthData auth = authDAO.getAuth(authToken);
        if (auth == null){
            throw new UnauthorizedException();
        }

        int id = gameDAO.createGame(request.gameName());
        return new CreateResult(id);
    }

    public void joinGame(String authToken, JoinRequest request) throws Exception {
        if (request.playerColor() == null || request.gameID() <= 0){
            throw new BadRequestException();
        }

        AuthData auth = authDAO.getAuth(authToken);
        if (auth == null){
            throw new UnauthorizedException();
        }

        GameData game = gameDAO.getGame(request.gameID());
        GameData updatedGame;
        if (request.playerColor().equals("WHITE")){
            if (!(game.whiteUsername() == null)){
                throw new AlreadyTakenException();
            }
            updatedGame = new GameData(request.gameID(), auth.username(),
                    game.blackUsername(), game.gameName(), game.game());
        }else if (request.playerColor().equals("BLACK")){
            if (!(game.blackUsername() == null)){
                throw new AlreadyTakenException();
            }
            updatedGame = new GameData(request.gameID(), game.whiteUsername(),
                    auth.username(), game.gameName(), game.game());
        } else{
            throw new BadRequestException();
        }

        try {
            gameDAO.updateGame(updatedGame);
        } catch(BadRequestException e){
            throw new BadRequestException();
        }

    }

}
