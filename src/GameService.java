import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;


public class GameService implements Runnable {
	private Scanner in;
	private PrintWriter out;
	public boolean login = false;
	private final GameState game;
    private int playerId;
    private boolean isPlayerTurn;
    static boolean primary = true;



	public GameService(GameState game, Socket socket, int playerId) {
        this.playerId = playerId;
		this.game = game;
        isPlayerTurn = false;

		try {
			in = new Scanner(socket.getInputStream());
			out = new PrintWriter(socket.getOutputStream(), true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

    @Override
    public void run() {
        loginAndWait();

        while (login) {
            while(isPlayerTurn) {
                try {
                    Request request = Request.parse(in.nextLine());
                    String response = execute(game, request);
                    out.println(response + "\r\n");

                    Thread.sleep(1000);
                } catch (NoSuchElementException | InterruptedException e) {
                    login = false;
                }
            }try {
                Thread.sleep(2000);
            } catch (InterruptedException e){
                e.printStackTrace();
            }
        }
        logout();
    }

    void sendMessage(String message){
        out.println(message);
    }


	private void loginAndWait(){
        if(primary) {
            out.println("Welcome!\n");
            out.println("How many players should play: ");
            GameServer.setNumberPlayers(Integer.parseInt(in.nextLine()));
            out.println("Enter number of bots: ");
            GameServer.setNumberBots(Integer.parseInt(in.nextLine()));
            primary = false;
        }
        GameServer.incNumPlayers();
        login = true;
        if(GameServer.getMaxPlayers() != 0){
            game.addPlayer(playerId);
        }
	}


	public void logout() {
		try {
			Thread.sleep(2000);
			in.close();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

    void nextPlayerTurn(){
        isPlayerTurn = !isPlayerTurn;
    }
    boolean isPlayerTurn(){
        return isPlayerTurn;
    }


	public String execute(GameState game, Request request) {
		try {
            switch (request.type) {
                case PLAYERS:
                    return ""+game.getNumberOfPlayers();
                case HELP:
                    return "COMMANDS - PLAYERS: Number of players, MOVE (influenceCard, coord1, coord2): Puts a stone on the board, LOGOUT, STOPSERVER.  Influence cards available: NONE, DOUBLE, FREEDOM, REPLACEMENT";
                case INVALID:
                    return "Invalid command.";
                case MOVE:

                    InfluenceCard influenceCard = getInfluenceCard(request.params[0].toUpperCase());
                    if (!game.getPlayerList().get(playerId - 1).isCardUsed(influenceCard)){
                        return influenceCard + " influenceCard has already been used!";
                    }

                    int x = Integer.parseInt(request.params[2])-1;
                    int y = Integer.parseInt(request.params[1])-1;

                    Move move = new Move(influenceCard, new Coordinates(x, y));

                    if(game.isMoveLegal(move, playerId)){
                        game.setBoard(move, playerId);
                        if(influenceCard != InfluenceCard.DOUBLE)
                            nextPlayerTurn();
                        if(influenceCard != InfluenceCard.NONE)
                            game.playersList.get(playerId - 1).removeCard(influenceCard);
                        if(influenceCard == InfluenceCard.NONE) {
                            GameServer.sendMessageAllClients("Player " + playerId + " has moved at x: " + (x+1) + " y: " + (y+1));
                            return "Next player is Player "+ (playerId);
                        }
                        else if(influenceCard == InfluenceCard.DOUBLE) {
                            GameServer.displayBoard(game);
                            GameServer.sendMessageAllClients("Player " + playerId + " has moved at x: " + (x+1) + " y: " + (y+1) + " and used a double card");
                            return "Player " + playerId + "can move again.";
                        }
                        else if(influenceCard == InfluenceCard.FREEDOM) {
                            GameServer.sendMessageAllClients("Player " + playerId + "has moved at x: " + (x+1) + " y: " + (y+1) + " with freedom card");
                            return "Next player is Player "+ (playerId + 1);
                        }
                        else if(influenceCard == InfluenceCard.REPLACEMENT){
                            GameServer.sendMessageAllClients("Player " + playerId + "has moved at x: " + (x+1) + " y: " + (y+1) + " with replacement card");
                            return "Next player's turn ";
                        }
                    } else {
                        switch(influenceCard){
                            case NONE:
                                return "Cannot place stone. Cell must be adjacent and empty.";
                            case DOUBLE:
                                return "Cannot use double card. Cell must be adjacent and empty.";
                            case FREEDOM:
                                return "Freedom card can only be placed on empty cell";
                            case REPLACEMENT:
                                return "Cannot use replacement card if cell is not adjacent.";
                        }
                    }
                case LOGOUT:
                    login = false;
                    return "Goodbye!";
                case STOPSERVER:
                    GameServer.sendMessageAllClients("Server has been stopped.");
                    System.exit(0);
                case EASTEREGG:
                    return "( ⚆ _ ⚆ ) You found me!";
                default:
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
    private InfluenceCard getInfluenceCard(String param) {
        InfluenceCard influenceCard = InfluenceCard.NONE;
        for(InfluenceCard card : InfluenceCard.values()){
            if(card.name().equals(param))
                influenceCard = card;
        }
        return influenceCard;
    }

}
