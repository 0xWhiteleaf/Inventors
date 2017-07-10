package engine.comparators;

import com.corundumstudio.socketio.SocketIOClient;
import engine.core.Invention;
import engine.core.Player;

import java.util.Comparator;
import java.util.Map;

/**
 * Classe permettant de comparer deux joueurs ayant travaillé sur une invention commune.
 * @author Noé Mourton-Comte
 */
public class ContributorComparator implements Comparator<Map.Entry<SocketIOClient, Player>>
{
    private Player playerWithTrophy;
    private Invention targetedInvention;

    public ContributorComparator(Player playerWithTrophy, Invention targetedInvention)
    {
        this.playerWithTrophy = playerWithTrophy;
        this.targetedInvention = targetedInvention;
    }

    @Override
    public int compare(Map.Entry<SocketIOClient, Player> o1, Map.Entry<SocketIOClient, Player> o2)
    {
        int result = 0;

        int o1Contributions = this.targetedInvention.getContributions().get(o1.getValue().getUUID());
        int o2Contributions = this.targetedInvention.getContributions().get(o2.getValue().getUUID());

        if(o1Contributions > o2Contributions)
        {
            result = -1;
        }
        else if(o2Contributions > o1Contributions)
        {
            result = 1;
        }
        else
        {
            if(playerWithTrophy == o1.getValue())
            {
                result = -1;
            }
            else if(playerWithTrophy == o2.getValue())
            {
                result = 1;
            }
        }

        return result;
    }
}
