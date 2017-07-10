package engine.comparators;

import com.corundumstudio.socketio.SocketIOClient;
import engine.core.Invention;
import engine.core.Knowledges;
import engine.core.Player;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Classe de test de la classe "ContributorComparator"
 * @author Valentin Sappa
 */
public class ContributorComparatorTest
{
    @Test
    public void compareContributors()
    {
        //Création du premier joueur et de son socket
        SocketIOClient mockedSocketClient1;
        mockedSocketClient1 = mock(SocketIOClient.class);
        when(mockedSocketClient1.getSessionId()).thenReturn(new UUID(1, 1));

        Player player1 = new Player("player1", mockedSocketClient1.getSessionId());
        player1.giveTrophy();

        //Création du 2eme joueur et de son socket
        SocketIOClient mockedSocketClient2;
        mockedSocketClient2 = mock(SocketIOClient.class);
        when(mockedSocketClient2.getSessionId()).thenReturn(new UUID(2, 2));

        Player player2 = new Player("player2", mockedSocketClient2.getSessionId());
        player2.removeTrophy();

        //Création de la HashMap de contribution qui sera utilisé pour tester la méthode compare
        HashMap<SocketIOClient, Player> mapContributors = new HashMap<>();
        mapContributors.put(mockedSocketClient1, player1);
        mapContributors.put(mockedSocketClient2, player2);

        //Création de la hashmap de contribution utilisé par l'invention
        HashMap<UUID, Integer> contributionContributorsInvention = new HashMap<>();

        //Si le joueur 2 a plus de contribution que le joueur 1
        contributionContributorsInvention.put(mockedSocketClient1.getSessionId(), 4);
        contributionContributorsInvention.put(mockedSocketClient2.getSessionId(), 5);

        Invention targetedInvention;
        targetedInvention = mock(Invention.class);
        when(targetedInvention.getContributions()).thenReturn(contributionContributorsInvention);

        ContributorComparator contributorComparator = new ContributorComparator(player1, targetedInvention);

        Map<SocketIOClient, Player> sortedMapContributors = mapContributors.entrySet()
                .stream()
                .sorted(contributorComparator)
                .collect(Collectors.toMap(Map.Entry::getKey,
                        Map.Entry::getValue,
                        (a, b) -> a, LinkedHashMap::new));

        assertEquals(2, sortedMapContributors.size());

        Iterator<Map.Entry<SocketIOClient, Player>> sortedMapContributorsIterator = sortedMapContributors.entrySet().iterator();

        assertTrue(sortedMapContributorsIterator.hasNext());
        assertEquals(new UUID(2, 2), sortedMapContributorsIterator.next().getKey().getSessionId());

        assertTrue(sortedMapContributorsIterator.hasNext());
        assertEquals(new UUID(1, 1), sortedMapContributorsIterator.next().getKey().getSessionId());


        //Si les joueurs ont la meme contribution et que le joueur 1 a LeonardoDaVinci
        contributionContributorsInvention.put(mockedSocketClient1.getSessionId(), 3);
        contributionContributorsInvention.put(mockedSocketClient2.getSessionId(), 3);

        contributorComparator = new ContributorComparator(player1, targetedInvention);

        sortedMapContributors = mapContributors.entrySet()
                .stream()
                .sorted(contributorComparator)
                .collect(Collectors.toMap(Map.Entry::getKey,
                        Map.Entry::getValue,
                        (a, b) -> a, LinkedHashMap::new));

        sortedMapContributorsIterator = sortedMapContributors.entrySet().iterator();

        assertTrue(sortedMapContributorsIterator.hasNext());
        assertEquals(new UUID(1, 1), sortedMapContributorsIterator.next().getKey().getSessionId());

        assertTrue(sortedMapContributorsIterator.hasNext());
        assertEquals(new UUID(2, 2), sortedMapContributorsIterator.next().getKey().getSessionId());
    }
}
