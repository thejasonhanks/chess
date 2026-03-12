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
        if (authToken == null || authToken.isEmpty()) {
            throw new BadRequestException("Error: auth token must be provided");
        }
        AuthData auth = authDAO.getAuth(authToken);
        if (auth == null){
            throw new UnauthorizedException("Error: unauthorized request");
        }
        ArrayList<GameData> games = gameDAO.listGames();
        return new ListResult(games);
    }

    public CreateResult createGame(String authToken, CreateRequest request) throws Exception {
        if (request.gameName() == null){
            throw new BadRequestException("Error: game name cannot be null");
        }

        AuthData auth = authDAO.getAuth(authToken);
        if (auth == null){
            throw new UnauthorizedException("Error: unauthorized request");
        }

        int id = gameDAO.createGame(request.gameName());
        return new CreateResult(id);
    }

    public void joinGame(String authToken, JoinRequest request) throws Exception {
        if (request.playerColor() == null || request.gameID() <= 0){
            throw new BadRequestException("Error: player color or gameID cannot be null");
        }

        AuthData auth = authDAO.getAuth(authToken);
        if (auth == null){
            throw new UnauthorizedException("Error: unauthorized request");
        }

        GameData game = gameDAO.getGame(request.gameID());
        GameData updatedGame;
        if (request.playerColor().equals("WHITE")){
            if (!(game.whiteUsername() == null)){
                throw new AlreadyTakenException("Error: player color already taken");
            }
            updatedGame = new GameData(request.gameID(), auth.username(),
                    game.blackUsername(), game.gameName(), game.game());
        }else if (request.playerColor().equals("BLACK")){
            if (!(game.blackUsername() == null)){
                throw new AlreadyTakenException("Error: player color already taken");
            }
            updatedGame = new GameData(request.gameID(), game.whiteUsername(),
                    auth.username(), game.gameName(), game.game());
        } else{
            throw new BadRequestException("Error: player color must be WHITE or BLACK");
        }

        try {
            gameDAO.updateGame(updatedGame);
        } catch(BadRequestException e){
            throw new BadRequestException("Error: bad request");
        }

    }

}
