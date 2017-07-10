package engine;

import com.corundumstudio.socketio.SocketIOClient;
import engine.core.Invention;
import engine.core.Inventor;
import engine.core.Knowledges;
import engine.core.Player;
import engine.core.rewards.Reward;
import engine.core.rewards.RewardType;
import engine.exceptions.CantDeterminateWinnerException;
import engine.exceptions.CurrentPlayerNotFoundException;
import engine.exceptions.RequestedInventionNotFoundException;
import engine.exceptions.RequestedInventorNotFoundException;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.net.InetSocketAddress;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Classe destinée à tester la classe "Game".
 * @author Noé Mourton-Comte, Valentin Sappa
 */
@FixMethodOrder(MethodSorters.JVM)
public class GameTest
{
    private SocketIOClient mockedSocketClient;
    private Game mockedGame;

    @Before
    public void init() throws CantDeterminateWinnerException, RequestedInventorNotFoundException, RequestedInventionNotFoundException
    {
        this.mockedSocketClient = mock(SocketIOClient.class);
        when(this.mockedSocketClient.getRemoteAddress()).thenReturn(new InetSocketAddress("127.0.0.1", 666));
        when(this.mockedSocketClient.getSessionId()).thenReturn(new UUID(0, 0));

        this.mockedGame = mock(Game.class);

        when(this.mockedGame.determinateWinner()).thenCallRealMethod();

        when(this.mockedGame.getPlayerInventorByName(any(Player.class), anyString())).thenCallRealMethod();
        when(this.mockedGame.getInventionByName(anyString())).thenCallRealMethod();
        when(this.mockedGame.getPlayerVictoryPoint(any())).thenCallRealMethod();
    }

    //region Gestion de la partie

    @Test(expected = RequestedInventorNotFoundException.class)
    public void getNotOwnedPlayerInventor() throws RequestedInventorNotFoundException
    {
        this.mockedGame.getPlayerInventorByName(new Player(), "Feu");
    }

    @Test
    public void getOwnedPlayerInventor() throws RequestedInventorNotFoundException
    {
        Player player = new Player();

        Inventor mockedInventor = mock(Inventor.class);
        when(mockedInventor.getName()).thenReturn("Serge Miranda");
        player.addInventor(mockedInventor);

        Inventor result = this.mockedGame.getPlayerInventorByName(player, "Serge Miranda");
        assertEquals(result.getName(), "Serge Miranda");
    }

    @Test(expected = RequestedInventionNotFoundException.class)
    public void getNotExistingInvention() throws RequestedInventionNotFoundException
    {
        this.mockedGame.getInventionByName("Revolver");
    }

    @Test
    public void getExistingInvention() throws RequestedInventionNotFoundException
    {
        ArrayList<Invention> fakeInventionsList = new ArrayList<>();
        fakeInventionsList.add(new Invention("Feu", new Knowledges(1, 2, 3, 4), 2));

        when(this.mockedGame.getInventionsList()).thenReturn(fakeInventionsList);

        Invention result = this.mockedGame.getInventionByName("Feu");
        assertEquals(result.getName(), "Feu");
    }

    @Test
    public void getPlayerVictoryPoint()
    {
        List<Reward> rewardList = new ArrayList<>();

        rewardList.add(new Reward(RewardType.VICTORY, 5));
        rewardList.add(new Reward(RewardType.VICTORY, 2));
        rewardList.add(new Reward(RewardType.CARD, 1));

        Player mockedPlayer = mock(Player.class);

        when(mockedPlayer.getRewards()).thenReturn(rewardList);

        assertEquals(mockedGame.getPlayerVictoryPoint(mockedPlayer), 8);
    }

    //endregion

    //region Déroulement de la partie

    @Test(expected = CantDeterminateWinnerException.class)
    public void determinateWinnerWithoutPlayer() throws CantDeterminateWinnerException
    {
        this.mockedGame.determinateWinner();
    }

    @Test
    public void determinateWinnerWithOnePlayer() throws CantDeterminateWinnerException
    {
        Player fakePlayer = new Player();

        HashMap<SocketIOClient, Player> fakePlayersList = new HashMap<>();
        fakePlayersList.put(mock(SocketIOClient.class), fakePlayer);

        when(this.mockedGame.getPlayers()).thenReturn(fakePlayersList);

        assertEquals(this.mockedGame.determinateWinner().getValue(), fakePlayer);
    }

    @Test
    public void determinateWinnerWithTwoPlayers() throws CantDeterminateWinnerException
    {
        List<Reward> player1Rewards = new ArrayList<>();
        player1Rewards.add(new Reward(RewardType.CARD, 1));
        player1Rewards.add(new Reward(RewardType.VICTORY, 3));

        List<Reward> player2Rewards = new ArrayList<>();
        player2Rewards.add(new Reward(RewardType.CARD, 1));
        player2Rewards.add(new Reward(RewardType.VICTORY, 2));

        Player mockedPlayer1 = mock(Player.class);
        when(mockedPlayer1.getRewards()).thenReturn(player1Rewards);

        Player mockedPlayer2 = mock(Player.class);
        when(mockedPlayer2.getRewards()).thenReturn(player2Rewards);

        HashMap<SocketIOClient, Player> fakePlayersList = new HashMap<>();
        fakePlayersList.put(mock(SocketIOClient.class), mockedPlayer1);
        fakePlayersList.put(mock(SocketIOClient.class), mockedPlayer2);

        when(this.mockedGame.getPlayers()).thenReturn(fakePlayersList);

        assertEquals(mockedPlayer1, this.mockedGame.determinateWinner().getValue());
    }

    @Test(expected = CurrentPlayerNotFoundException.class)
    public void playRoundWithNotEnoughPlayer() throws CurrentPlayerNotFoundException, CantDeterminateWinnerException
    {
        doCallRealMethod().when(this.mockedGame).play();
        this.mockedGame.play();
    }

    //endregion
}
