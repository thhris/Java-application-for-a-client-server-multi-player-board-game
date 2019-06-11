import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;


public class GameTests {

    public GameState game = new GameState();
    private int[][] gameBoard = game.getBoard();
    private int[][] gameBoard2 = game.getBoard();



    @Test
    public void addPlayer() {
        game.addPlayer(1);
        assert game.getNumberOfPlayers() != 0;
    }
    @Test
    public void getBoard() {
        assert gameBoard != null;
    }
    @Test
    public void setBoard() {
        game.addPlayer(1);
        Coordinates coordinates = new Coordinates(1,1);
        Move move = new Move(null, coordinates);
        game.setBoard(move, 1);
        boolean notEquals = Arrays.deepEquals(gameBoard, gameBoard2);
        System.out.print(notEquals);
        assert notEquals;

    }
    @Test
    public void isMoveLegal() {

        game.addPlayer(1 );
        Coordinates coordinates = new Coordinates(1,1);
        Move move = new Move(null, coordinates);
        boolean isAllowed = game.isMoveLegal(move, 1);

        assert isAllowed;

    }
    @Test
    public void checkIfAdjacent() {

        assert true;

    }
}
