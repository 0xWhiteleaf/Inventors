package network;

import com.google.gson.Gson;
import engine.ai.AIPlayer;
import engine.ai.strategies.RandomStrategy;
import engine.core.Invention;
import engine.core.Inventor;
import engine.core.Knowledges;
import engine.core.Team;
import engine.core.rewards.Reward;
import engine.core.rewards.RewardType;
import io.socket.client.Ack;
import org.junit.*;
import org.mockito.ArgumentCaptor;

import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Classe destinée à tester les classes "AIClient" et "Client".
 * @author Noé Mourton-Comte
 */
public class ClientTest
{
    private AIClient legitAIClient;
    private AIClient mockedAIClient;

    private AIPlayer legitAIPlayer;

    private Gson gsonSerializer;

    @Before
    public void init() throws URISyntaxException
    {
        //Initialisation d'un AIClient...
        this.legitAIClient = new AIClient(RandomStrategy.class, false);

        //Initialisation d'un AIClient mocké....
        this.mockedAIClient = mock(AIClient.class);
        when(mockedAIClient.getUUID()).thenReturn(new UUID(0, 0).toString());

        //Initialisation d'un AIPlayer...
        this.legitAIPlayer = new AIPlayer();

        this.gsonSerializer = new Gson();
    }

    /**
     * Méthode permettant de tester le comportement de l'écouteur d'un GameStartedMessage
     */
    @Test
    public void gameStartedMessageBehavior() throws InvocationTargetException, NoSuchMethodException,
            InstantiationException, IllegalAccessException
    {
        when(this.mockedAIClient.getInventions()).thenCallRealMethod();
        when(this.mockedAIClient.getOthersPlayersInventors()).thenCallRealMethod();

        //Liste d'inventions présente sur "la table"
        ArrayList<Invention> oneInventionList = new ArrayList<>();
        oneInventionList.add(new Invention("Invention", new Knowledges(1, 2, 3, 4), 2));

        //Liste d'inventeurs du joueur
        ArrayList<Inventor> playerInventors = new ArrayList<>();
        Knowledges playerInventor1Knowledges = new Knowledges(1, 2, 3, 4);
        playerInventors.add(new Inventor("Inventeur1", playerInventor1Knowledges, Team.Bleu));

        //Liste d'inventeurs d'un autre joueur
        ArrayList<Inventor> otherPlayerInventors = new ArrayList<>();
        Knowledges otherPlayerInventor2Knowledges = new Knowledges(4, 3, 2, 1);
        otherPlayerInventors.add(new Inventor("Inventeur2", otherPlayerInventor2Knowledges, Team.Rouge));

        //Dictionnaire des inventeurs de tous les joueurs
        HashMap<UUID, ArrayList<Inventor>> mergedInventors = new HashMap<>();
        mergedInventors.put(UUID.fromString(this.mockedAIClient.getUUID()), playerInventors);

        UUID otherPlayerId = new UUID(1, 1);
        mergedInventors.put(otherPlayerId, otherPlayerInventors);

        //Réception d'un "GameStartedMessage"...
        this.legitAIClient.gameStartedMessageListener(this.mockedAIClient, this.legitAIPlayer).call(
                this.gsonSerializer.toJson(mergedInventors),
                this.gsonSerializer.toJson(oneInventionList));

        verify(this.mockedAIClient).init(this.mockedAIClient, this.legitAIPlayer);

        assertEquals(this.mockedAIClient.getInventions().size(), 1);

        assertEquals(this.legitAIPlayer.getInventors().size(), 1);
        assertEquals(this.legitAIPlayer.getInventors().get(0).getName(), "Inventeur1");
        assertTrue(this.legitAIPlayer.getInventors().get(0).getKnowledges().equals(playerInventor1Knowledges));

        assertEquals(this.mockedAIClient.getOthersPlayersInventors().get(otherPlayerId).size(), 1);
        assertEquals(this.mockedAIClient.getOthersPlayersInventors().get(otherPlayerId).get(0).getName(), "Inventeur2");
        assertTrue(this.mockedAIClient.getOthersPlayersInventors().get(otherPlayerId).get(0).getKnowledges().equals(otherPlayerInventor2Knowledges));
    }

    /**
     * Méthode permettant de tester le comportement de l'écouteur d'un TurnStartedMessage
     */
    @Test
    public void turnStartedMessageBehavior()
    {
        when(this.mockedAIClient.playTurn())
                .thenReturn(new String[] { ProtocolTypes.MakeAvailableAction.name() })
                .thenReturn(new String[] { ProtocolTypes.WorkAction.name(), "Inventeur", "Invention" });

        //Initilisation d'un ACK mocké...
        Ack mockedAck = mock(Ack.class);

        //Réception d'un "TurnStartedMessage"...
        this.legitAIClient.turnStartedMessageListener(this.mockedAIClient, this.legitAIPlayer, true).call(mockedAck);
        this.legitAIClient.turnStartedMessageListener(this.mockedAIClient, this.legitAIPlayer, true).call(mockedAck);

        verify(mockedAck, times(2)).call(any());
    }

    /**
     * Méthode permettant de tester le comportement de l'écouteur d'un SynchronizeMessage
     */
    @Test
    @SuppressWarnings("unchecked")
    public void synchronizeMessageBehavior()
    {
        when(this.mockedAIClient.getInventions()).thenCallRealMethod();
        when(this.mockedAIClient.getOthersPlayersInventors()).thenCallRealMethod();

        Class<HashMap<UUID, List<Inventor>>> otherPlayersClazz =
                (Class<HashMap<UUID, List<Inventor>>>) new HashMap<UUID, List<Inventor>>().getClass();

        doCallRealMethod().when(this.mockedAIClient).sync(anyListOf(Invention.class),
                isA(otherPlayersClazz), any(AIClient.class), any(AIPlayer.class));

        //Liste d'inventions présente sur "la table"
        ArrayList<Invention> twoInventionsList = new ArrayList<>();
        twoInventionsList.add(new Invention("Invention1", new Knowledges(1, 2, 3, 4), 2));
        twoInventionsList.add(new Invention("Invention2", new Knowledges(4, 3, 2, 1), 3));

        //Liste d'inventeurs du joueur
        ArrayList<Inventor> playerInventors = new ArrayList<>();
        Knowledges playerInventorsKnowledges = new Knowledges(1, 2, 3, 4);
        playerInventors.add(new Inventor("Inventeur1", playerInventorsKnowledges, Team.Bleu));
        playerInventors.add(new Inventor("Inventeur2", playerInventorsKnowledges, Team.Bleu));

        //Liste d'inventeurs d'un autre joueur
        ArrayList<Inventor> otherPlayerInventors = new ArrayList<>();
        Knowledges otherPlayerInventorsKnowledges = new Knowledges(4, 3, 2, 1);
        otherPlayerInventors.add(new Inventor("Inventeur3", otherPlayerInventorsKnowledges, Team.Rouge));
        otherPlayerInventors.add(new Inventor("Inventeur4", otherPlayerInventorsKnowledges, Team.Rouge));

        //Dictionnaire des inventeurs de tous les joueurs
        HashMap<UUID, ArrayList<Inventor>> mergedInventors = new HashMap<>();
        mergedInventors.put(UUID.fromString(this.mockedAIClient.getUUID()), playerInventors);

        UUID otherPlayerId = new UUID(1, 1);
        mergedInventors.put(otherPlayerId, otherPlayerInventors);

        this.legitAIClient.synchronizeMessageListener(this.mockedAIClient, this.legitAIPlayer).call(
                this.gsonSerializer.toJson(mergedInventors),
                this.gsonSerializer.toJson(twoInventionsList));

        verify(this.mockedAIClient).sync(anyList(), anyMap(), any(AIClient.class), any(AIPlayer.class));

        assertEquals(this.mockedAIClient.getInventions().size(), 2);

        assertEquals(this.legitAIPlayer.getInventors().size(), 2);
        assertEquals(this.legitAIPlayer.getInventors().get(0).getName(), "Inventeur1");
        assertTrue(this.legitAIPlayer.getInventors().get(0).getKnowledges().equals(playerInventorsKnowledges));

        assertEquals(this.mockedAIClient.getOthersPlayersInventors().get(otherPlayerId).size(), 2);
        assertEquals(this.mockedAIClient.getOthersPlayersInventors().get(otherPlayerId).get(0).getName(), "Inventeur3");
        assertTrue(this.mockedAIClient.getOthersPlayersInventors().get(otherPlayerId).get(0).getKnowledges().equals(otherPlayerInventorsKnowledges));
    }

    /**
     * Méthode permettant de tester le comportement de l'écouteur d'un RewardMessage
     */
    @Test
    public void rewardMessageBehavior()
    {
        //Création d'un faux joueur
        AIPlayer mockedAIPlayer = mock(AIPlayer.class);
        //Sa méthode requestReward nous retournera 1 lorsque nous l'appellerons
        when(mockedAIPlayer.requestReward(any())).thenReturn(1);

        //Création d'un faux ACK
        Ack mockedAck = mock(Ack.class);

        //Initialisation d'un captor qui nous permettra d'enregistrer les arguments passés lors des appels sur le mock
        ArgumentCaptor<Integer> ackCaptor = ArgumentCaptor.forClass(Integer.class);

        //Création d'une liste de récompenses
        List<Reward> rewards = new ArrayList<>();
        rewards.add(new Reward(RewardType.CARD, 1));
        rewards.add(new Reward(RewardType.VICTORY));
        rewards.add(new Reward(RewardType.VICTORY));

        //Nous convertissons la liste de récompenses en JSON
        String rewardsJson = this.gsonSerializer.toJson(rewards);

        //Réception d'un "RewardMessage"...
        this.legitAIClient.rewardMessageListener(this.mockedAIClient, mockedAIPlayer).call(rewardsJson, mockedAck);

        //Nous vérifions que la méthode call de l'ACK a bien été appellé, avec le bon paramètre
        verify(mockedAck).call(ackCaptor.capture());

        assertEquals(ackCaptor.getValue().intValue(), 1);
    }
}
