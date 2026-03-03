# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)

## Modules

The application has three modules.

- **Client**: The command line program used to play a game of chess over the network.
- **Server**: The command line program that listens for network requests from the client and manages users and games.
- **Shared**: Code that is used by both the client and the server. This includes the rules of chess and tracking the state of a game.

## Starter Code

As you create your chess application you will move through specific phases of development. This starts with implementing the moves of chess and finishes with sending game moves over the network between your client and server. You will start each phase by copying course provided [starter-code](starter-code/) for that phase into the source code of the project. Do not copy a phases' starter code before you are ready to begin work on that phase.

## IntelliJ Support

Open the project directory in IntelliJ in order to develop, run, and debug your code using an IDE.

## Maven Support

You can use the following commands to build, test, package, and run your code.

| Command                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| `mvn compile`              | Builds the code                                 |
| `mvn package`              | Run the tests and build an Uber jar file        |
| `mvn package -DskipTests`  | Build an Uber jar file                          |
| `mvn install`              | Installs the packages into the local repository |
| `mvn test`                 | Run all the tests                               |
| `mvn -pl shared test`      | Run all the shared tests                        |
| `mvn -pl client exec:java` | Build and run the client `Main`                 |
| `mvn -pl server exec:java` | Build and run the server `Main`                 |

These commands are configured by the `pom.xml` (Project Object Model) files. There is a POM file in the root of the project, and one in each of the modules. The root POM defines any global dependencies and references the module POM files.

## Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.

```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```

## Sequence Diagram Link

https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAE5M9qBACu2AMQALADMABwATG4gMP7I9gAWYDoIPoYASij2SKoWckgQaJiIqKQAtAB85JQ0UABcMADaAAoA8mQAKgC6MAD0PgZQADpoAN4ARP2UaMAAtihjtWMwYwA0y7jqAO7QHAtLq8soM8BICHvLAL6YwjUwFazsXJT145NQ03PnB2MbqttQu0WyzWYyOJzOQLGVzYnG4sHuN1E9SgmWyYEoAAoMlkcpQMgBHVI5ACU12qojulVk8iUKnU9XsKDAAFUBhi3h8UKTqYplGpVJSjDpagAxJCcGCsyg8mA6SwwDmzMQ6FHAADWkoGME2SDA8QVA05MGACFVHHlKAAHmiNDzafy7gjySp6lKoDyySIVI7KjdnjAFKaUMBze11egAKKWlTYAgFT23Ur3YrmeqBJzBYbjObqYCMhbLCNQbx1A1TJXGoMh+XyNXoKFmTiYO189Q+qpelD1NA+BAIBMU+4tumqWogVXot3sgY87nae1t+7GWoKDgcTXS7QD71D+et0fj4PohQ+PUY4Cn+Kz5t7keC5er9cnvUexE7+4wp6l7FovFqXtYJ+cLtn6pavIaSpLPU+wgheertBAdZoFByyXAmlDtimGD1OEThOFmEwQZ8MDQcCyxwfECFISh+xXOgHCmF4vgBNA7CMjEIpwBG0hwAoMAADIQFkhRYcwTrUP6zRtF0vQGOo+RoFmyyKp80HfL8-yAssSxXKBgpAc8IwqSgawaTs0KPMB4lUEiMAIEJ4oYoJwkEkSYCkm+hi7jS+4MkyU7GXOPl3kuwowGKEpujKcplu8So3sFDpJr6zpdhu7pbp5gqefUNRIAAZvKZkAjAxxgCA8RqNuXlUre-L1IecgoM+8Tnpe17DkllTLgGa4Bu1mWpe2Bmls54oZKoAGYCNIHVP6RlESZxqXlR6C6XN8LJsgqYwLh+GjMpi17N8FGrchiw6Y2DGeN4fj+F4KDoDEcSJA9T3Ob4WCidlG31A00gRvxEbtBG3Q9HJqgKcMp2Ieg6HwpUI31DDSHTZZGHWbZ9n2J9TlCZ9rlqO51WCp19IwIyYAtRiLUdXVi7dWFABqlAFfKtNbmTArJR2Nkuv1L6DZ2pP06ORgoNwx6XjTA3yEFvIhYz9TSBLTKGBz8gJQrSWY-zPZ9iTPNIwJ+OnhNU0zTzoEvDA60SZtlSiTheFKXRTY3cx-gouu-jYOKGr8WiMAAOJKho33WZJwfA2D9hKtDK2w4UekfujpYo3DlspZ29TIDkoc5k5aKEyShu1Yl5OU9TGvAPLC7c0rMAs1AbMC1enOi7NOdt6+Q3edr5N52ABdqBidf7veYUZCGlYICHSoelzXd82lgc5AAPCPPLlIbqewv6Q8j6o5sIIBafL-NYxxzmBaNOM18oAAktIBYAIzhMEgQgps8S6igbqcmOiCZIoA1QAMghdEED8ABySoLoXBgJ0O2iYtolDAM7fa98w63waFguYz834fy-ssH+f9wGqTGN8EBIAwFHUgcsGBcDKEIKQVdRit0AgcAAOxuCcCgJwMQIzBDgFxAAbPACchgR4wCKNtMo2cah-VaB0WO8dpiJyQlmRhcxkEY0RmnZGGj0BaKVLAnRaN94I15rZRq6IR4YjgJIkuxMsr93rn5Km0sa7j0VkKeozdW41y1vXZetkgmuPLgPMWtiUD2K3toHxXU-ESKPFIheHcK7c11mleJmtXF7y-A1SRI8T5n0sRfMCDClQEPqO-T+tt4aYTkeg3aLsDpX2qS-WpRCGn0XYZ7SwEt7KbGekgBIYBBl9ggCMgAUhAcU885gxGoWqWRaCfr2yUcyGSPQH4J3gknLM2AEDAEGVAOAEB7JQDWA-Z+uirHGwzopUYxzTmUAuVcm5nSLLlOyfUOZ4p7EArQM4jyfdInuIpkyamFE6aZMnv41mhUe4ZKiaE-m4TwUyFFh4+xtzpCJIZskiK65cnAGCRPK2qV6hkrLtizJ9Q-BaDsUqDEZLCUN2ScybAzK0lzEXp3Kl3daX5P0ZY-58yQX-lPhYr8FTagjAaSnVBO09quxgBqzVbCPZ3S8Kc0Z4y9XykQMGWAwBsDHMIHkAoMiI4KMkgDIGIMwbGEaQUuEtRZVWQUbZDgqt0QYj9ZLFAoK6VcwatwPA1MOUIpkP69WstyURPpVEiNpqpwxtCsreN6Ve7Czcb5GAIBI1QHsZmxuKtg2LJQAK+FQqV7dl7P2UVDxxVxuDaUr1GNI6VPuU0tBGDXZsKAA
