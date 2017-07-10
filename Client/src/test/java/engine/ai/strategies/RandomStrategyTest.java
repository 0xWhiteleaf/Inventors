package engine.ai.strategies;

import engine.ai.AIPlayer;
import engine.core.Invention;
import engine.core.Inventor;

import engine.core.rewards.Reward;
import network.AIClient;
import network.ProtocolTypes;

import org.mockito.Mockito;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Random;

import static org.junit.Assert.*;

/**
 * Classe de test de RandomStrategy.
 * @author Louis Prud'homme
 */
public class RandomStrategyTest
{
    private static ArrayList<Inventor> inventorList = new ArrayList<>();
    private RandomStrategy testRandomStrategy;

    private AIPlayer mockedAIPlayer;
    private Random mockedRandom;
    private Inventor mockedInventor;
    private Invention mockedInvention;

    private ArrayList<Inventor> mockedInventorsList;
    private ArrayList<Invention> mockedInventionsList;

    private AIClient mockedAIClient;
    
    /**
     * Permet d'initialiser les champs ci-dessus avant chaque test, afin qu'ils y soient utilisés.
     */
    @Before
    public void initialize()
    {
        mockedRandom = Mockito.mock(Random.class);
        mockedAIPlayer = Mockito.mock(AIPlayer.class);
        mockedAIClient = Mockito.mock(AIClient.class);
        mockedInvention = Mockito.mock(Invention.class);
        mockedInventor = Mockito.mock(Inventor.class);

        mockedInventorsList = new ArrayList<>();
        mockedInventionsList = new ArrayList<>();

        mockedInventorsList.add(mockedInventor);
        mockedInventionsList.add(mockedInvention);

        Mockito.when(mockedAIClient.getInventions()).thenReturn(mockedInventionsList);
        testRandomStrategy = new RandomStrategy(mockedAIPlayer, mockedAIClient, mockedAIClient);
    }

    /**
     * Permet de tester si l'IA renvoie une MakeAvalaibleAction lorsque tous ses inventeurs sont occupés.
     */
    @Test
    public void doesForceMakeAvalaible()
    {
        mockedInventorsList.add(mockedInventor);

        Mockito.when(mockedAIPlayer.getBusyInventors()).thenReturn(mockedInventorsList);
        Mockito.when(mockedAIPlayer.getInventors()).thenReturn(mockedInventorsList);

        assertEquals(ProtocolTypes.MakeAvailableAction.name(), testRandomStrategy.determinateAction()[0]);
    }

    /**
     * Permet de tester si l'IA renvoie une WorkAction lorsque tous ses inventeurs sont libres.
     */
    @Test
    public void doesForceWork()
    {
        Mockito.when(mockedAIPlayer.getInventors()).thenReturn(mockedInventorsList);
        Mockito.when(mockedAIPlayer.getFreeInventors()).thenReturn(mockedInventorsList);
        Mockito.when(mockedAIPlayer.getBusyInventors()).thenReturn(Mockito.mock(inventorList.getClass()));

        Mockito.when(mockedInventor.isBusy()).thenReturn(false);
        Mockito.when(mockedInventor.canWorkOn(mockedInvention)).thenReturn(true);

        assertEquals(ProtocolTypes.WorkAction.name(), testRandomStrategy.determinateAction()[0]);
    }

    /**
     * Permet de tester si l'IA est capable de tirer au hasard une MakeAvalaibleAciton.
     */
    @Test
    public void picksMakeAvalaibleAction()
    {
        Mockito.when(mockedAIPlayer.getBusyInventors()).thenReturn(Mockito.mock(inventorList.getClass()));
        Mockito.when(mockedAIPlayer.getInventors()).thenReturn(Mockito.mock(inventorList.getClass()));

        Mockito.when(mockedAIPlayer.getBusyInventors().size()).thenReturn(1);
        Mockito.when(mockedAIPlayer.getInventors().size()).thenReturn(2);
        Mockito.when(mockedRandom.nextInt(2)).thenReturn(0);

        testRandomStrategy.setRandomizer(mockedRandom);

        assertEquals(ProtocolTypes.MakeAvailableAction.name(), testRandomStrategy.determinateAction()[0]);
    }

    /**
     * Permet de tester si l'IA est capable de tirer au hasard une WorkAction.
     */
    @Test
    public void picksWorkAction()
    {
        mockedInventorsList.add(mockedInventor);

        Mockito.when(mockedAIPlayer.getBusyInventors()).thenReturn(Mockito.mock(inventorList.getClass()));
        Mockito.when(mockedAIPlayer.getFreeInventors()).thenReturn(mockedInventorsList);
        Mockito.when(mockedAIPlayer.getInventors()).thenReturn(mockedInventorsList);

        Mockito.when(mockedRandom.nextInt(2)).thenReturn(1);
        Mockito.when(mockedAIPlayer.getBusyInventors().size()).thenReturn(1);
        Mockito.when(mockedInventor.canWorkOn(mockedInvention)).thenReturn(true);

        testRandomStrategy.setRandomizer(mockedRandom);

        assertEquals(ProtocolTypes.WorkAction.name(), testRandomStrategy.determinateAction()[0]);
    }

    /**
     * Permet de tester si l'IA peut choisir correctement une invention sur laquelle les inventeurs peuvent travailler.
     */
    @Test
    public void correctlyChoosesInventions()
    {
        Invention mockedInventionBad = Mockito.mock(Invention.class);

        Mockito.when(mockedInvention.getName()).thenReturn("good");
        Mockito.when(mockedInventionBad.getName()).thenReturn("bad");

        mockedInventionsList.add(mockedInventionBad);

        Mockito.when(mockedAIPlayer.getInventors()).thenReturn(mockedInventorsList);
        Mockito.when(mockedAIPlayer.getFreeInventors()).thenReturn(mockedInventorsList);
        Mockito.when(mockedAIPlayer.getBusyInventors()).thenReturn(Mockito.mock(inventorList.getClass()));
        testRandomStrategy.setRandomizer(mockedRandom);

        Mockito.when(mockedAIPlayer.getBusyInventors().size()).thenReturn(0);
        Mockito.when(mockedInventor.canWorkOn(mockedInvention)).thenReturn(true);
        Mockito.when(mockedInventor.canWorkOn(mockedInventionBad)).thenReturn(false);
        Mockito.when(mockedRandom.nextInt(mockedAIClient.getInventions().size())).thenReturn(1).thenReturn(0);

        assertEquals(mockedInvention.getName(), testRandomStrategy.determinateAction()[2]);
    }


    /**
     * Permet de tester si l'IA renvoie une MakeAvalaibleAction lorsqu'aucun inventeur ne peut travailler sur aucune invention.
     */
    @Test
    public void choosesMakeAvalaibleWhenNoOneCanWork()
    {
        Invention mockedInvention2 = Mockito.mock(Invention.class);

        Mockito.when(mockedInvention.getName()).thenReturn("good");
        Mockito.when(mockedInvention2.getName()).thenReturn("bad");

        mockedInventionsList.add(mockedInvention2);

        Mockito.when(mockedAIPlayer.getInventors()).thenReturn(mockedInventorsList);
        Mockito.when(mockedAIPlayer.getFreeInventors()).thenReturn(mockedInventorsList);
        Mockito.when(mockedAIPlayer.getBusyInventors()).thenReturn(Mockito.mock(inventorList.getClass()));
        testRandomStrategy.setRandomizer(mockedRandom);

        Mockito.when(mockedAIPlayer.getBusyInventors().size()).thenReturn(0);
        Mockito.when(mockedInventor.canWorkOn(mockedInvention)).thenReturn(false);
        Mockito.when(mockedInventor.canWorkOn(mockedInvention2)).thenReturn(false);
        Mockito.when(mockedRandom.nextInt(mockedAIClient.getInventions().size())).thenReturn(1).thenReturn(0);

        assertEquals(ProtocolTypes.MakeAvailableAction.name(), testRandomStrategy.determinateAction()[0]);
    }

    /**
     * Premet de tester si l'IA peut choisir correctement un inventeur, capable de travailler sur l'invention choisie.
     */
    @Test
    public void correctlyChoosesInventors()
    {
        Inventor mockedInventorBad = Mockito.mock(Inventor.class);

        mockedInventorsList.add(mockedInventorBad);

        Mockito.when(mockedInventor.getName()).thenReturn("good");
        Mockito.when(mockedInventorBad.getName()).thenReturn("bad");

        Mockito.when(mockedAIPlayer.getInventors()).thenReturn(mockedInventorsList);
        Mockito.when(mockedAIPlayer.getFreeInventors()).thenReturn(mockedInventorsList);
        Mockito.when(mockedAIPlayer.getBusyInventors()).thenReturn(Mockito.mock(inventorList.getClass()));
        testRandomStrategy.setRandomizer(mockedRandom);

        Mockito.when(mockedAIPlayer.getBusyInventors().size()).thenReturn(0);
        Mockito.when(mockedInventor.isBusy()).thenReturn(false);
        Mockito.when(mockedInventor.canWorkOn(mockedInvention)).thenReturn(true);
        Mockito.when(mockedInventorBad.canWorkOn(mockedInvention)).thenReturn(false);
        Mockito.when(mockedRandom.nextInt(mockedAIPlayer.getFreeInventors().size())).thenReturn(1).thenReturn(0);

        assertEquals(mockedInventor.getName(), testRandomStrategy.determinateAction()[1]);
    }

    /**
     * Teste si l'IA est capable de choisir une récompense au hasard.
     */
    @Test
    public void picksRandomReward()
    {
        ArrayList<Reward> mockedRewardsList = new ArrayList<>();

        Reward mockedReward = Mockito.mock(Reward.class);

        Mockito.when(mockedReward.getValue()).thenReturn(0);

        mockedRewardsList.add(mockedReward);

        assertEquals(mockedRewardsList.get(testRandomStrategy.determinateReward(mockedRewardsList)), mockedReward);
    }
}