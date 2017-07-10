package engine.core;

import engine.core.rewards.RewardType;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

/**
 * Classe de test de la classe Invention
 * @author Louis Prud'homme
 */
public class InventionsTest
{
    private Knowledges mockedKnowledges;
    private Player mockedPlayer;

    private Invention inventionTest;

    /**
     * Initialize les variables nécessaires aux tests.
     */
    @Before
    public void initialize()
    {
        mockedKnowledges = Mockito.mock(Knowledges.class);
        mockedPlayer = Mockito.mock(Player.class);

        when(mockedKnowledges.getPhys()).thenReturn(1);
        when(mockedKnowledges.getChem()).thenReturn(2);
        when(mockedKnowledges.getMech()).thenReturn(3);
        when(mockedKnowledges.getMath()).thenReturn(4);

        inventionTest = new Invention("Invention", mockedKnowledges, 0);
    }

    /**
     * Permet de tester si la création d'une invention se fait de la bonne manière (ie, si tous les champs sont instanciés convenablement).
     */
    @Test
    public void createInvention()
    {
        assertEquals("Invention", inventionTest.getName());
        assertEquals(inventionTest.getRequiredKnowledges().getPhys(), 1);
        assertEquals(inventionTest.getRequiredKnowledges().getChem(), 2);
        assertEquals(inventionTest.getRequiredKnowledges().getMech(), 3);
        assertEquals(inventionTest.getRequiredKnowledges().getMath(), 4);
    }

    /**
     * Vérifie si l'on peut compléter une invention.
     */
    @Test
    public void inventionCompleted()
    {
        inventionTest.updateActualKnowledges(mockedPlayer, mockedKnowledges);

        assertTrue(inventionTest.isCompleted());
    }

    /**
     * Vérifie si une invention n'est pas complétée à sa création.
     */
    @Test
    public void inventionNotCompletedOnCreate()
    {
        assertFalse(inventionTest.isCompleted());
    }

    /**
     * Vérifie si une invention n'est pas complétée lorsqu'il ne faut pas qu'elle le soit.
     */
    @Test
    public void inventionNotCompleted()
    {
        inventionTest.updateActualKnowledges(Mockito.mock(Player.class), Mockito.mock(Knowledges.class));

        assertFalse(inventionTest.isCompleted());
    }

    /**
     * Teste si les knowledges ne dépassent pas la limite des requiredKnowledges.
     */
    @Test
    public void knowledgesDoNotOverflow()
    {
        inventionTest.updateActualKnowledges(mockedPlayer, mockedKnowledges);
        inventionTest.updateActualKnowledges(mockedPlayer, mockedKnowledges);

        assertEquals(inventionTest.getRequiredKnowledges().getChem(), inventionTest.getActualKnowledges().getChem());
        assertEquals(inventionTest.getRequiredKnowledges().getMath(), inventionTest.getActualKnowledges().getMath());
        assertEquals(inventionTest.getRequiredKnowledges().getMech(), inventionTest.getActualKnowledges().getMech());
        assertEquals(inventionTest.getRequiredKnowledges().getPhys(), inventionTest.getActualKnowledges().getPhys());
    }

    /**
     * Teste la contribution d'un joueur sur une invention
     */
    @Test
    public void playerContribution()
    {
        Player player1 = new Player("Joueur 1", new UUID(0, 1));
        Player player2 = new Player("Joueur 2", new UUID(1, 1));

        inventionTest.updateActualKnowledges(player1, new Knowledges(0, 1, 2, 3));
        inventionTest.updateActualKnowledges(player2, new Knowledges(3, 2, 1, 0));
        inventionTest.updateActualKnowledges(player1 , new Knowledges(4, 4, 4, 4));

        int contributionPlayer1 = inventionTest.getContributions().get(player1.getUUID());
        int contributionPlayer2 = inventionTest.getContributions().get(player2.getUUID());

        assertEquals(contributionPlayer1, 7);
        assertEquals(contributionPlayer2, 3);
    }
}
