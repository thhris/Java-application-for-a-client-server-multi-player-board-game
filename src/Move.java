// This class represents a single move of a player.
public final class Move
{
    private final InfluenceCard card;
    private final Coordinates firstMove;

    public Move(InfluenceCard card, Coordinates firstMove) {
            assert firstMove != null;
        assert card == InfluenceCard.DOUBLE;

        this.card = card;
        this.firstMove = firstMove;
    }
    /*Removed secondMove since the way double move is implemented
    * is for the player who's turn it is to go again.
    * */
    public Coordinates getFirstMove() { return firstMove; }
    public InfluenceCard getCard() { return card; }
}
