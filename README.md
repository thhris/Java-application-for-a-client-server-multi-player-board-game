 # A Client-server multiplayer boardgame written in Java


## 1. Pre-production: 

Text based game played on a server. Players take turns in putting stones down with their assigned initial number. The winner is the player with the most stones on the board.
Once the specified number of players has connected, the game will start automatically by placing a number between 1 and 5 for each player on the board. 
Once a player gets blocked, he must wait until there are no more spaces on the board before the game can finish.
Upon completion, the game should calculate the scores for each player, determine the winner and send this information to all clients.


## 2. How to Run: 
 
To run the game first you need export the jar file to a desired location. Afterwards, navigate to the folder using command prompt and run “java -jar Server.jar”. 
After the server has started running, open a Putty session and connect to localhost at port 8888. Make sure the connection is set to “Raw”.  
You will be greeted by a message that prompts you to select the number of players that can join before the game starts and afterwards the number of bots. 
Once all players have connected, the game will start.


## 3. How to play: 
 
The game rules have been implemented according to the instructions provided in the assignment description. The following commands are available to the user:

Players: Takes no arguments and returns the number of players and bots connected to the server.

Move: Takes an influence card, an X coordinate and a Y coordinate as arguments and, if it passes the validation, places a stone on the specific position on the board. Help: Gives general information about how to make moves and what other commands are available. 

Logout: Takes no arguments and logs out the user from the game. StopServer: Takes no arguments and stops the server. Not mentioned in the help command as it is made for admin use only.


## 4. Program structure:

Game State: 
The game state is a class that contains the board and the list of players. It is instantiated in the in multiple places across the program since it is necessary to pass the board through the server to the clients. 
In the class I use Collections to store a list of all the players, while the board itself is stored as a twodimensional array of integers. Since the game is text-based the board is formatted and passed as a string through the output stream to each of the clients every time they make a move. 

GameServer: 
This is the main class that runs the server and exchanges information between all connected clients. To play the game, a client will have to connect to the server and keep a continuous communication channel open to it. It is responsible for distributing an updated version of the board to each of the connected players. It must accept user input only from the user whose current turn is to play. 
The main method of the class creates an instance of the GameState which allows for transferring the board between the server and all connected clients. 
The sendMessageAllClients method takes a string argument and iterates through each service being provided and calls the sendMessage function from the gameService instance, sending the argument to each service thread, corresponding to each player.  
All other methods are helper methods that are used for client connections.  
One of the issues with the game is how to get the game to start after enough clients have connected. 
The workaround I have implemented is to ask the first player that logs in to specify how many players should connect before the game starts.  The number entered can be between 2 and 5. 
Afterwards the socket methods for that many times before the game starts. 
If the player enters a number higher than 5 it will automatically default to 5 and if it is less than 2 the player will be tasked with playing against a bot.
Player and GameBot: 
The player class implements the Player Logic interface and all the functions within it, such as the GameBot class which also implements the same abstract class so that way I can create a list of type PlayerLogic which contains both Player and Bots under the same list. This helps with achieving abstraction. 


## 5.Socket Protocol and Concurency 

| Client Request  | Server Response |
| ------------- | ------------- |
| PLAYERS  | Return a String of the number of connected clients  |
| MOVE<Influence Card><X Coordinate><Y Coordinate>  | Update server board if successful and the server responds by sending the updated board to every user connected to the network.   |
| StopServer  | Sends a request to the server telling it to shut down   |
 
The way that I have tackled the way to choose a specific number of players and specific number of bots when the first player connects to the network. To avoid issues I have used thread-blocking methods like socket.accept(). 
I have achieved concurrency by only accepting input from one player at a time, which is ideal for a turn-based game. That way each method is accessed by one thread at a time.  


## 6. Bot Logic:

The bot logic is fairly simple, yet quite effective. The bot implements an updated version of the makeMove function from the PlayerLogic class. The first move is applying the double card as it gives an advantage to being used in the beginning, making the player harder to block. From there on the bot makes a random move until it gets blocked. Once blocked, it tries to use the freedom card and as a last resort, it applies the replacement card so it can get one additional slot before the game is lost for it.


## 7. Current issues needing addressing:

 * The game is still missing an "endgame" state
 * The bots need a bit more work when being used through
