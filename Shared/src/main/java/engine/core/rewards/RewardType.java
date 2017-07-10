package engine.core.rewards;

/**
 * Énumération des différents types de pions
 * @author Noé Mourton-Comte
 */
public enum RewardType
{
    CARD("Carte"),
    VICTORY("Point(s) de victoire");

    private String friendlyName;

    RewardType(String friendlyName)
    {
        this.friendlyName = friendlyName;
    }

    @Override
    public String toString()
    {
        return this.friendlyName;
    }
}
