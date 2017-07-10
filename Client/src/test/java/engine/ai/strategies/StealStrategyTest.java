package engine.ai.strategies;

import engine.ai.AIPlayer;
import engine.core.Invention;
import engine.core.Inventor;
import engine.core.rewards.Reward;
import network.AIClient;
import network.ProtocolTypes;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

/**
 * Classe de test de StealStrategy.
 * @author Louis Prud'homme
 */
public class StealStrategyTest
{
    private StealStrategy testStealStrategy;

    private AIPlayer mockedAIPlayer;
    private AIClient mockedAIClient;

    private Inventor mockedInventor;
    private Invention mockedInvention;

    private ArrayList<Inventor> mockedInventorsList;
    private ArrayList<Invention> mockedInventionsList;

    HashMap<UUID, Integer> modelHashMap = new HashMap<>();

    /**
     * Initialise les variables nécessaires aux tests.
     */
    @Before
    public void initialize()
    {
        mockedAIClient = Mockito.mock(AIClient.class);
        mockedAIPlayer = Mockito.mock(AIPlayer.class);

        mockedInvention = Mockito.mock(Invention.class);
        mockedInventor = Mockito.mock(Inventor.class);

        mockedInventorsList = new ArrayList<>();
        mockedInventionsList = new ArrayList<>();

        mockedInventorsList.add(mockedInventor);
        mockedInventionsList.add(mockedInvention);

        Mockito.when(mockedAIClient.getUUID()).thenReturn(new UUID(0, 0).toString());
        Mockito.when(mockedInvention.getContributions()).thenReturn(new HashMap<>());

        testStealStrategy = new StealStrategy(mockedAIPlayer, mockedAIClient, mockedAIClient);
    }

    /**
     * Test si l'IA crée une makeAvalaibleAction lorsque tous les inventeurs sont occupés.
     */
    @Test
    public void doesForceMakeAvalaible()
    {
        ArrayList<Inventor> mockedInventorArrayList = new ArrayList<>();
        ArrayList<Invention> mockedInventionArrayList = new ArrayList<>();

        Mockito.when(mockedAIPlayer.getFreeInventors()).thenReturn(mockedInventorArrayList);
        Mockito.when(mockedAIClient.getInventions()).thenReturn(mockedInventionsList);

        Mockito.when(mockedInvention.getVictoryPoints()).thenReturn(1);

        mockedInventionArrayList.add(mockedInvention);

        assertEquals(ProtocolTypes.MakeAvailableAction.name(), testStealStrategy.determinateAction()[0]);
    }

    /**
     * Teste si l'IA crée une WorkAction lorsque tous les inventeurs sont disponibles.
     */
    @Test
    public void doesForceWork()
    {
        ArrayList<Inventor> mockedInventorArrayList = new ArrayList<>();
        ArrayList<Invention> mockedInventionArrayList = new ArrayList<>();

        Invention mockedInvention = Mockito.mock(Invention.class);
        Inventor mockedInventor = Mockito.mock(Inventor.class);

        Mockito.when(mockedInventor.canWorkOn(mockedInvention)).thenReturn(true);
        Mockito.when(mockedInvention.getVictoryPoints()).thenReturn(1);

        mockedInventionArrayList.add(mockedInvention);
        mockedInventorArrayList.add(mockedInventor);

        Mockito.when(mockedAIPlayer.getFreeInventors()).thenReturn(mockedInventorArrayList);
        Mockito.when(mockedAIClient.getInventions()).thenReturn(mockedInventionArrayList);

        assertEquals(ProtocolTypes.WorkAction.name(), testStealStrategy.determinateAction()[0]);
    }

    /**
     * Teste si l'IA pioche le meilleur inventeur pour une invention donnée.
     */
    @Test
    public void picksBestInventor()
    {
        ArrayList<Inventor> mockedInventorArrayList = new ArrayList<>();
        ArrayList<Invention> mockedInventionArrayList = new ArrayList<>();

        Invention mockedInvention = Mockito.mock(Invention.class);
        Inventor mockedInventor = Mockito.mock(Inventor.class);
        Inventor mockedInventorBad = Mockito.mock(Inventor.class);

        Mockito.when(mockedInventor.canWorkOn(mockedInvention)).thenReturn(true);
        Mockito.when(mockedInventorBad.canWorkOn(mockedInvention)).thenReturn(true);

        Mockito.when(mockedInventorBad.getGrade(mockedInvention)).thenReturn(1);
        Mockito.when(mockedInventor.getGrade(mockedInvention)).thenReturn(2);
        Mockito.when(mockedInvention.getVictoryPoints()).thenReturn(1);

        Mockito.when(mockedInventor.getName()).thenReturn("good");
        Mockito.when(mockedInventorBad.getName()).thenReturn("bad");

        mockedInventionArrayList.add(mockedInvention);
        mockedInventorArrayList.add(mockedInventor);

        Mockito.when(mockedAIPlayer.getFreeInventors()).thenReturn(mockedInventorArrayList);
        Mockito.when(mockedAIClient.getInventions()).thenReturn(mockedInventionArrayList);

        assertEquals(mockedInventor.getName(), testStealStrategy.determinateAction()[1]);
    }

    /**
     * Vérifie si l'IA choisit bien la meilleure récompense.
     */
    @Test
    public void picksBestReward()
    {
        ArrayList<Reward> mockedRewardsList = new ArrayList<>();

        Reward mockedRewardGood = Mockito.mock(Reward.class);
        Reward mockedRewardBad = Mockito.mock(Reward.class);

        Mockito.when(mockedRewardBad.getValue()).thenReturn(0);
        Mockito.when(mockedRewardGood.getValue()).thenReturn(1);

        mockedRewardsList.add(mockedRewardGood);
        mockedRewardsList.add(mockedRewardBad);

        assertEquals(mockedRewardsList.get(testStealStrategy.determinateReward(mockedRewardsList)), mockedRewardGood);
    }

    /**
     * Vérifie si l'AI choisit bien la meilleure invention à laquelle il n'a pas contribué.
     */
    @Test
    public void picksNonContributedInvention()
    {
        ArrayList<Inventor> mockedInventorArrayList = new ArrayList<>();
        ArrayList<Invention> mockedInventionArrayList = new ArrayList<>();

        Inventor mockedInventor = Mockito.mock(Inventor.class);
        Invention mockedInventionNotContributedNWorst = Mockito.mock(Invention.class);
        Invention mockedInventionContributedNBest = Mockito.mock(Invention.class);
        Invention mockedInventionContributedNWorst = Mockito.mock(Invention.class);

        Mockito.when(mockedInventor.canWorkOn(mockedInvention)).thenReturn(true);
        Mockito.when(mockedInventor.canWorkOn(mockedInventionNotContributedNWorst)).thenReturn(true);
        Mockito.when(mockedInventor.canWorkOn(mockedInventionContributedNBest)).thenReturn(true);
        Mockito.when(mockedInventor.canWorkOn(mockedInventionContributedNWorst)).thenReturn(true);

        Mockito.when(mockedInvention.getVictoryPoints()).thenReturn(1);
        Mockito.when(mockedInventionNotContributedNWorst.getVictoryPoints()).thenReturn(0);
        Mockito.when(mockedInventionContributedNBest.getVictoryPoints()).thenReturn(1);
        Mockito.when(mockedInventionContributedNWorst.getVictoryPoints()).thenReturn(0);

        Mockito.when(mockedInvention.getName()).thenReturn("good");
        Mockito.when(mockedInventionNotContributedNWorst.getName()).thenReturn("bad1");
        Mockito.when(mockedInventionContributedNBest.getName()).thenReturn("bad2");
        Mockito.when(mockedInventionContributedNWorst.getName()).thenReturn("bad3");

        HashMap<UUID, Integer> modelHashMap = new HashMap<>();
        HashMap<UUID, Integer> contributedHashMap = Mockito.mock(modelHashMap.getClass());
        HashMap<UUID, Integer> notContributedHashMap = Mockito.mock(modelHashMap.getClass());

        Mockito.when(contributedHashMap.containsKey(UUID.fromString(mockedAIClient.getUUID()))).thenReturn(true);
        Mockito.when(notContributedHashMap.containsKey(UUID.fromString(mockedAIClient.getUUID()))).thenReturn(false);

        Mockito.when(mockedInvention.getContributions()).thenReturn(notContributedHashMap);
        Mockito.when(mockedInventionNotContributedNWorst.getContributions()).thenReturn(notContributedHashMap);
        Mockito.when(mockedInventionContributedNBest.getContributions()).thenReturn(contributedHashMap);
        Mockito.when(mockedInventionContributedNWorst.getContributions()).thenReturn(contributedHashMap);

        mockedInventorArrayList.add(mockedInventor);

        mockedInventionArrayList.add(mockedInventionNotContributedNWorst);
        mockedInventionArrayList.add(mockedInventionContributedNBest);
        mockedInventionArrayList.add(mockedInventionContributedNWorst);
        mockedInventionArrayList.add(mockedInvention);

        Mockito.when(mockedAIPlayer.getFreeInventors()).thenReturn(mockedInventorArrayList);
        Mockito.when(mockedAIClient.getInventions()).thenReturn(mockedInventionArrayList);

        assertEquals(mockedInvention.getName(), testStealStrategy.determinateAction()[2]);
    }

    /**
     * Vérifie si l'AI choisit bien la meilleure invention dans le cas où il a contribué à toutes celles posées sur la table.
     */
    @Test
    public void picksBestInvention()
    {
        ArrayList<Inventor> mockedInventorArrayList = new ArrayList<>();
        ArrayList<Invention> mockedInventionArrayList = new ArrayList<>();

        Inventor mockedInventor = Mockito.mock(Inventor.class);
        Invention mockedInventionNotContributedNWorst = Mockito.mock(Invention.class);

        Mockito.when(mockedInventor.canWorkOn(mockedInvention)).thenReturn(true);
        Mockito.when(mockedInventor.canWorkOn(mockedInventionNotContributedNWorst)).thenReturn(true);

        Mockito.when(mockedInvention.getVictoryPoints()).thenReturn(1);
        Mockito.when(mockedInventionNotContributedNWorst.getVictoryPoints()).thenReturn(0);

        Mockito.when(mockedInvention.getName()).thenReturn("good");
        Mockito.when(mockedInventionNotContributedNWorst.getName()).thenReturn("bad");

        HashMap<UUID, Integer> contributedHashMap = Mockito.mock(modelHashMap.getClass());

        Mockito.when(contributedHashMap.containsKey(UUID.fromString(mockedAIClient.getUUID()))).thenReturn(true);

        Mockito.when(mockedInvention.getContributions()).thenReturn(contributedHashMap);
        Mockito.when(mockedInventionNotContributedNWorst.getContributions()).thenReturn(contributedHashMap);

        mockedInventorArrayList.add(mockedInventor);

        mockedInventionArrayList.add(mockedInventionNotContributedNWorst);
        mockedInventionArrayList.add(mockedInvention);

        Mockito.when(mockedAIPlayer.getFreeInventors()).thenReturn(mockedInventorArrayList);
        Mockito.when(mockedAIClient.getInventions()).thenReturn(mockedInventionArrayList);

        assertEquals(mockedInvention.getName(), testStealStrategy.determinateAction()[2]);
    }
}