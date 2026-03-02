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

    public ListResult listGames(String authToken){
        AuthData auth = authDAO.getAuth(authToken);
        if (auth == null){
            throw new UnauthorizedException("unauthorized");
        }
        ArrayList<GameData> games = gameDAO.listGames();
        return new ListResult(games);
    }

    public CreateResult createGame(String authToken, CreateRequest request){
        if (request.gameName() == null){
            throw new BadRequestException("missing game name");
        }

        AuthData auth = authDAO.getAuth(authToken);
        if (auth == null){
            throw new UnauthorizedException("unauthorized");
        }

        int id = gameDAO.createGame(request.gameName());
        return new CreateResult(id);
    }

    public void joinGame(String authToken, JoinRequest request){
        if (request.playerColor() == null || request.gameID() <= 0){
            throw new BadRequestException("missing player color or game ID");
        }

        AuthData auth = authDAO.getAuth(authToken);
        if (auth == null){
            throw new UnauthorizedException("unauthorized");
        }

        GameData game = gameDAO.getGame(request.gameID());
        GameData updatedGame;
        if (request.playerColor().equals("WHITE")){
            if (!(game.whiteUsername() == null)){
                throw new AlreadyTakenException("white already taken");
            }
            updatedGame = new GameData(request.gameID(), auth.username(), game.blackUsername(), game.gameName(), game.game());
        }else{
            if (!(game.blackUsername() == null)){
                throw new AlreadyTakenException("white already taken");
            }
            updatedGame = new GameData(request.gameID(), game.whiteUsername(), auth.username(), game.gameName(), game.game());
        }

        try {
            gameDAO.updateGame(updatedGame);
        } catch(BadRequestException e){
            throw new BadRequestException("game does not exist");
        }

    }

}
