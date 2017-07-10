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

import static org.junit.Assert.assertEquals;

/**
 * Classe de test de RandomStrategy.
 * @author Louis Prud'homme
 */
public class VictoryPointsStrategyTest
{
    private VictoryPointsStrategy testVictoryPointsStrategy;

    private AIPlayer mockedAIPlayer;
    private AIClient mockedAIClient;

    private Inventor mockedInventor;
    private Invention mockedInvention;

    private ArrayList<Inventor> mockedInventorsList;
    private ArrayList<Invention> mockedInventionsList;

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

        testVictoryPointsStrategy = new VictoryPointsStrategy(mockedAIPlayer, mockedAIClient, mockedAIClient);
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

        assertEquals(ProtocolTypes.MakeAvailableAction.name(), testVictoryPointsStrategy.determinateAction()[0]);
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

        assertEquals(ProtocolTypes.WorkAction.name(), testVictoryPointsStrategy.determinateAction()[0]);
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

        assertEquals(mockedInventor.getName(), testVictoryPointsStrategy.determinateAction()[1]);
    }

    /**
     * Teste si l'IA pioche la meilleure invention disponible.
     */
    @Test
    public void picksBestInvention()
    {
        ArrayList<Inventor> mockedInventorArrayList = new ArrayList<>();
        ArrayList<Invention> mockedInventionArrayList = new ArrayList<>();

        Inventor mockedInventor = Mockito.mock(Inventor.class);
        Invention mockedInventionAvalaibleNWorst = Mockito.mock(Invention.class);

        Mockito.when(mockedInventor.canWorkOn(mockedInvention)).thenReturn(true);
        Mockito.when(mockedInventor.canWorkOn(mockedInventionAvalaibleNWorst)).thenReturn(true);

        Mockito.when(mockedInvention.getVictoryPoints()).thenReturn(1);
        Mockito.when(mockedInventionAvalaibleNWorst.getVictoryPoints()).thenReturn(0);

        Mockito.when(mockedInvention.getName()).thenReturn("good");
        Mockito.when(mockedInventionAvalaibleNWorst.getName()).thenReturn("bad");

        mockedInventorArrayList.add(mockedInventor);

        mockedInventionArrayList.add(mockedInventionAvalaibleNWorst);
        mockedInventionArrayList.add(mockedInvention);

        Mockito.when(mockedAIPlayer.getFreeInventors()).thenReturn(mockedInventorArrayList);
        Mockito.when(mockedAIClient.getInventions()).thenReturn(mockedInventionArrayList);

        assertEquals(mockedInvention.getName(), testVictoryPointsStrategy.determinateAction()[2]);
    }

    /**
     * Vérifie si l'IA tire la meilleure récompense.
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

        assertEquals(mockedRewardsList.get(testVictoryPointsStrategy.determinateReward(mockedRewardsList)), mockedRewardGood);
    }

    /**
     * Vérifie si l'IA ordonne le redressement des inventeurs aucun d'entre eux ne peut travailler sur l'invention en question.
     */
    @Test
    public void forcesMakeAvalaibleWhenNoOneCanWorkOnBestInvention()
    {
        ArrayList<Inventor> mockedInventorArrayList = new ArrayList<>();
        ArrayList<Invention> mockedInventionArrayList = new ArrayList<>();

        Inventor mockedInventor = Mockito.mock(Inventor.class);
        Invention mockedInventionWorst = Mockito.mock(Invention.class);

        Mockito.when(mockedInventor.canWorkOn(mockedInvention)).thenReturn(false);
        Mockito.when(mockedInventor.canWorkOn(mockedInventionWorst)).thenReturn(true);

        Mockito.when(mockedInvention.getVictoryPoints()).thenReturn(1);
        Mockito.when(mockedInventionWorst.getVictoryPoints()).thenReturn(0);

        mockedInventorArrayList.add(mockedInventor);

        mockedInventionArrayList.add(mockedInventionWorst);
        mockedInventionArrayList.add(mockedInvention);

        Mockito.when(mockedAIPlayer.getFreeInventors()).thenReturn(mockedInventorArrayList);
        Mockito.when(mockedAIClient.getInventions()).thenReturn(mockedInventionArrayList);

        assertEquals(ProtocolTypes.MakeAvailableAction.name(), testVictoryPointsStrategy.determinateAction()[0]);
    }
}