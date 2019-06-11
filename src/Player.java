import java.util.HashMap;
import java.util.Map;

public class Player implements PlayerLogic{

    private Map<InfluenceCard,Boolean> influenceCards;
    final int playerId;
    public boolean isBlocked;

    public Player(int playerId){
        this.playerId = playerId;
        //create a Hashmap of all the player cards and whether they have been used
        addCards();
    }

    @Override
    public int getMyPlayerId() {
        return playerId;
    }

    @Override
    public boolean makeMove() {//not implemented since directly handled in GameService, but kept to implement bot
        return false;
    }

    @Override
    public void setBlocked() {
        isBlocked = true;
    }

    public Map<InfluenceCard,Boolean> getPlayerCards() {
        return influenceCards;
    }

    public void addCards(){
        influenceCards = new HashMap<>();
        influenceCards.put(InfluenceCard.NONE, true);
        influenceCards.put(InfluenceCard.DOUBLE, true);
        influenceCards.put(InfluenceCard.FREEDOM, true);
        influenceCards.put(InfluenceCard.REPLACEMENT, true);
    }

    public boolean isCardUsed(InfluenceCard card){
        return influenceCards.get(card);
    }
    public void removeCard(InfluenceCard card){
        influenceCards.put(card, false);
    }

}
