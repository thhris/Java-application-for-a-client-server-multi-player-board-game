/*Teodor Hristoforov*/

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class GameServer {
	public static final int PORT = 8888;
	private static List<GameService> gameServices = new ArrayList<>();
    static int playersConnected;
    static boolean gameRunning = true;
    private static int numPlayers;
    private static int numBots;
    static int[][] finalSore;

    //Takes a string and sends it to each connected client
    public static void sendMessageAllClients(String message){
        for (GameService service:gameServices) {
            service.sendMessage(message);
        }
    }

    static void incNumPlayers(){
        playersConnected++;
    }


    public static void displayBoard(GameState game){
        int num = 1;

        sendMessageAllClients("    1   2   3   4   5   6   7   8   9   10");
        sendMessageAllClients("-------------------------------------------");
        for(int[] i : game.getBoard()){

            sendMessageAllClients(num + " " + Arrays.toString(i).replace("[", "| ").replace("]", " |").replace(", ", " | "));
            num++;
        }
    }
    static void setNumberPlayers(int n){
        numPlayers = n;
    }

    static void setNumberBots(int n){
        numBots = n;
    }
    static int getMaxPlayers(){
        return (numPlayers+numBots);
    }

	public static void main(String[] args) throws IOException, InterruptedException {
        GameState game = new GameState();
		ServerSocket server = new ServerSocket(PORT);
		System.out.println("\nStarted GameServer at port " + PORT);
		System.out.println("Waiting for players to connect...");

        Socket socket = server.accept();
        GameService service = new GameService(game, socket, 1);
        gameServices.add(service);
        Thread t = new Thread(service);
        t.start();

        System.out.println("Player 1 is connected");

        while(!service.login) {
            t.join(2000);
        }

		for (int i = 1; i < (numPlayers+numBots); i++) {
		    int playerId = i+1;
			socket = server.accept();
			service = new GameService(game, socket, playerId);
            gameServices.add(service);
			t = new Thread(service);
			t.start();
			service.sendMessage("Player " + playerId + " is connected");
        }
        for (int i = numPlayers; i < (numBots + numPlayers); i++) {
            game.addBot(i + 1);
        }

        sendMessageAllClients("###### Game has started ######\n");
        sendMessageAllClients("Initialising players on board.\n");
        game.addRandomPlayerPositions();
		sendMessageAllClients("Players added to board: \n");
		displayBoard(game);

        while(gameRunning){
            for (int i = 0; i < (numPlayers+numBots); i++) {
                gameServices.get(i).nextPlayerTurn();
                if(game.isBlocked(i+1)){
                    sendMessageAllClients("Player " +(i+1) + "cannot make another move.");
                }
                sendMessageAllClients("It's Player " +(i+1) + "'s turn to make a move.");

                while(gameServices.get(i).isPlayerTurn()){
                    Thread.sleep(2000);
                }
                displayBoard(game);
            }
        }

        sendMessageAllClients("###### Game Over. ######");
        for (int i = 0; i < (numPlayers+numBots); i++) {

            String playerName = "Player "+ (i+1);
            sendMessageAllClients(playerName + " scored: " );
        }

	}
}
