package client.websocket;

import java.util.Scanner;

import client.websocket.NotificationHandler;
import client.ServerFacade;
import client.websocket.WebSocketFacade;

import static client.EscapeSequences.*;

public class Client implements NotificationHandler {
    private String name = null;
    private final ServerFacade server;
    private final WebSocketFacade ws;
    private State state = State.SIGNEDOUT;

    public Client(String serverUrl) throws Exception {
        server = new ServerFacade(serverUrl);
        ws = new WebSocketFacade(serverUrl, this);
    }

    public void run() {
        System.out.println(LOGO + "♕ Welcome to Chess. Sign in to start. ♕");
        System.out.print(help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while(!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = eval(line);
                System.out.print(BLUE + result);
            } catch(Throwable e){
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }
}
