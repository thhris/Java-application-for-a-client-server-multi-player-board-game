import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

// This class (not yet fully implemented) will give access to the current state of the game.
public final class GameState{

    static final int ROWS = 6;
    static final int COLUMNS = 10;
    List<PlayerLogic> playersList;
    private static int[][] board;

    public GameState(){
        board = new int[ROWS][COLUMNS];
        playersList = new ArrayList<>();
    }

    public void addPlayer(int id){
        PlayerLogic player = new Player(id);
        playersList.add(player);
    }

    public void addBot(int id){
        PlayerLogic bot = new GameBot(this, id);
        playersList.add(bot);
    }

    public void addRandomPlayerPositions(){
        for(PlayerLogic player : playersList){
            while(true){
                int x = ThreadLocalRandom.current().nextInt(0, ROWS);
                int y = ThreadLocalRandom.current().nextInt(0, COLUMNS);
                Coordinates placement = new Coordinates(x, y);

                if (isEmpty(placement)) {
                    board[x][y] = player.getMyPlayerId();
                    break;
                }
            }
        }
    }
    private boolean isEmpty(Coordinates coords){
        int x = coords.getX();
        int y = coords.getY();

        return board[x][y] == 0;
    }

    public int getNumberOfPlayers() {
        return playersList.size();
        //return GameServer.playersConnected;
    }
    public List<PlayerLogic> getPlayerList() {
        return playersList;
        //return GameServer.playersConnected;
    }


    // Returns a rectangular matrix of board cells, with six rows and ten columns.
    // Zeros indicate empty cells.
    // Non-zero values indicate stones of the corresponding player.  E.g., 3 means a stone of the third player.
    public int[][] getBoard() {return board;}


    // Returns the set of influence cards available to the given player.
    //Changed the return type to map so it can also store the cards and whether they have been used in the form of a boolean
    public Map<InfluenceCard, Boolean> getAvailableInfluenceCards(int playerId) {
        Player player = new Player(playerId);

        return player.getPlayerCards();
    }

    /*Requires move and player id
     *Updates board with player id on top of the specified coordinates
     * */
    public void setBoard(Move move, int playerId){
        Coordinates firstMove = move.getFirstMove();
        board[firstMove.getX()][firstMove.getY()] = playerId;
        if(move.getCard() != InfluenceCard.NONE){
            playersList.get(playerId - 1).removeCard(move.getCard());
        }
    }

    /*Takes player id, X and Y coordinates for first move
    * Returns Boolean true if adjacent
    * */
    private boolean checkIfAdjacent(int playerId, int firstMoveX, int firstMoveY) {
        for (int xPrevious = firstMoveX - 1; xPrevious < firstMoveX + 2; xPrevious++) {
            for (int yPrevious = firstMoveY - 1; yPrevious < firstMoveY + 2; yPrevious++){
                try{
                    if(board[xPrevious][yPrevious] == playerId && !(xPrevious == firstMoveX && yPrevious == firstMoveY)){
                        return true;
                    }
                }catch(IndexOutOfBoundsException e){
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    /*Takes a move made and a player id
    * Performs different checks depending on influence card used(on none)
    * returns true if move is a legal one.
     * */
    public boolean isMoveLegal(Move move, int playerId) {
        InfluenceCard cardUsed = move.getCard();
        int firstMoveX = move.getFirstMove().getX();
        int firstMoveY = move.getFirstMove().getY();

        //check if input is within the board boundaries
        if(firstMoveX < 0 || firstMoveX > 6  || firstMoveY < 0 || firstMoveY > 10){
            return false;
        }

        switch (cardUsed){
            case NONE:
            case DOUBLE:
                if (isEmpty(move.getFirstMove()) && checkIfAdjacent(playerId, firstMoveX, firstMoveY)){
                    return true;
                } return false;
            case FREEDOM:
                if(isEmpty(move.getFirstMove())){
                    return true;
                } return false;
            case REPLACEMENT:
                if(checkIfAdjacent(playerId, firstMoveX, firstMoveY)){
                    return true;
                } return false;
        }try {
            throw new Exception("Card doesn't exist.");
        }catch (Exception e){e.printStackTrace();}

        return false;
    }

    public boolean isBlocked(int playerId){
        if(playersList.get(playerId-1).isCardUsed(InfluenceCard.REPLACEMENT)){
            return false;
        }
        else if(playersList.get(playerId-1).isCardUsed(InfluenceCard.FREEDOM)){
            for(int x = 0; x < ROWS; x++){
                for(int y = 0; y < COLUMNS; y++){
                    if (board[x][y] == 0){
                        return false;
                    }
                }
            }
        }else{
            for(int x = 0; x < ROWS; x++){
                for(int y = 0; y < COLUMNS; y++){
                    if (board[x][y] == 0){
                        if(checkIfAdjacent(playerId, x, y)){
                            return false;
                        }
                    }
                }
            }
        }
        playersList.get(playerId-1).setBlocked();
        return true;
    }

}

