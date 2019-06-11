import java.util.HashMap;
import java.util.Map;

public class GameBot implements PlayerLogic{
    private int playerId;
    private Map<InfluenceCard,Boolean> influenceCards;
    private GameState game;
    private boolean isBlocked = false;

    public GameBot(GameState game, int playerId){
        this.playerId = playerId;
        this.game = game;
        //create a Hashmap of all the cards the bot has and whether they have been used
        addCards();
    }

    @Override
    public int getMyPlayerId() {
        return playerId;
    }

    @Override
    public boolean makeMove(){
        for (int x = 0; x < 6; x++) {
            for(int y = 0; y < 10; y++) {
                if (influenceCards.get(InfluenceCard.DOUBLE)) {
                    Move move = new Move(InfluenceCard.DOUBLE,
                            new Coordinates(x, y));
                    if (game.isMoveLegal(move, playerId)) {
                        game.setBoard(move, playerId);
                        return true;
                    }
                }
                else{
                    for (InfluenceCard card : influenceCards.keySet()) {
                        if (influenceCards.get(card)) {
                            Move move = new Move(card,
                                    new Coordinates(x, y));
                            if (game.isMoveLegal(move, playerId)) {
                                game.setBoard(move, playerId);
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    private void addCards(){
        influenceCards = new HashMap<>();
        influenceCards.put(InfluenceCard.DOUBLE, true);
        influenceCards.put(InfluenceCard.FREEDOM, true);
        influenceCards.put(InfluenceCard.REPLACEMENT, true);
    }

    public void removeCard(InfluenceCard card){
        influenceCards.put(card, false);
    }

    @Override
    public boolean isCardUsed(InfluenceCard card) {
        return influenceCards.get(card);
    }

    public void setBlocked() {
        isBlocked = true;
    }


}
