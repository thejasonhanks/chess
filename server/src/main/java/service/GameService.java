package service;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
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
        if (request.playerColor() == null || request.gameID() <= 0) {
            throw new BadRequestException("Error: player color or gameID cannot be null");
        }

        AuthData auth = authDAO.getAuth(authToken);
        if (auth == null) {
            throw new UnauthorizedException("Error: unauthorized request");
        }

        GameData game = gameDAO.getGame(request.gameID());
        GameData updatedGame;
        String white = game.whiteUsername();
        String black = game.blackUsername();
        if (request.playerColor().equals("WHITE")) {
            if (white != null && !white.equals(auth.username())) {
                throw new AlreadyTakenException("Error: player color already taken");
            }
            updatedGame = new GameData(request.gameID(), auth.username(),
                    game.blackUsername(), game.gameName(), game.game());
        } else if (request.playerColor().equals("BLACK")) {
            if (black != null && !black.equals(auth.username())) {
                throw new AlreadyTakenException("Error: player color already taken");
            }
            updatedGame = new GameData(request.gameID(), game.whiteUsername(),
                    auth.username(), game.gameName(), game.game());
        } else {
            throw new BadRequestException("Error: player color must be WHITE or BLACK");
        }

        try {
            gameDAO.updateGame(updatedGame);
        } catch (BadRequestException e) {
            throw new BadRequestException("Error: bad request");
        }

    }

    public void makeMove(String authToken, int gameID, ChessMove move) throws Exception{
        AuthData auth = authDAO.getAuth(authToken);
        if (auth == null) {
            throw new UnauthorizedException("Error: unauthorized request");
        }

        GameData gameData = gameDAO.getGame(gameID);
        if (gameData == null) {
            throw new BadRequestException("Error: game not found");
        }

        String username = auth.username();
        boolean isWhite = username.equals(gameData.whiteUsername());
        boolean isBlack = username.equals(gameData.blackUsername());
        if (!isWhite && !isBlack){
            throw new UnauthorizedException("Error: observers cannot make moves");
        }

        ChessGame game = gameData.game();
        ChessGame.TeamColor turn = game.getTeamTurn();

        if (game.isGameOver()) {
            throw new BadRequestException("Error: game is over");
        }

        if (turn == ChessGame.TeamColor.WHITE && !isWhite) {
            throw new InvalidMoveException("Error: not your turn");
        }
        if (turn == ChessGame.TeamColor.BLACK && !isBlack) {
            throw new InvalidMoveException("Error: not your turn");
        }

        game.makeMove(move);

        GameData updated = new GameData(
                gameID,
                gameData.whiteUsername(),
                gameData.blackUsername(),
                gameData.gameName(),
                game
        );

        gameDAO.updateGame(updated);
    }

    public void leave(String authToken, int gameID) throws Exception {
        AuthData auth = authDAO.getAuth(authToken);
        if (auth == null) {
            throw new UnauthorizedException("Error: unauthorized");
        }

        GameData gameData = gameDAO.getGame(gameID);
        if (gameData == null) {
            throw new BadRequestException("Error: game not found");
        }

        String username = auth.username();
        GameData updated;
        if (username.equals(gameData.whiteUsername())) {
            updated = new GameData(gameData.gameID(), null,
                    gameData.blackUsername(), gameData.gameName(), gameData.game());
        } else if (username.equals(gameData.blackUsername())) {
            updated = new GameData(gameData.gameID(), gameData.whiteUsername(),
                    null, gameData.gameName(), gameData.game());
        } else {
            updated = gameData;
        }

        gameDAO.updateGame(updated);
    }

    public void resign(String authToken, int gameID) throws Exception{
        AuthData auth = authDAO.getAuth(authToken);
        if (auth == null) {
            throw new UnauthorizedException("Error: unauthorized");
        }

        GameData gameData = gameDAO.getGame(gameID);
        if (gameData == null) {
            throw new BadRequestException("Error: game not found");
        }

        String username = auth.username();
        boolean isWhite = username.equals(gameData.whiteUsername());
        boolean isBlack = username.equals(gameData.blackUsername());

        if (!isWhite && !isBlack) {
            throw new UnauthorizedException("Error: observer cannot resign");
        }

        ChessGame game = gameData.game();
        if (game.isGameOver()) {
            throw new BadRequestException("Error: game already over");
        }

        game.setGameOver(true);

        GameData updated = new GameData(
                gameID,
                gameData.whiteUsername(),
                gameData.blackUsername(),
                gameData.gameName(),
                game
        );

        gameDAO.updateGame(updated);
    }

}
