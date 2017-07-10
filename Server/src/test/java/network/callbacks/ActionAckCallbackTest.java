package network.callbacks;

import engine.Game;
import engine.actions.Action;
import engine.actions.MakeAvailableAction;
import engine.actions.WorkAction;
import engine.core.*;
import engine.exceptions.RequestedInventionNotFoundException;
import engine.exceptions.RequestedInventorNotFoundException;
import network.ProtocolTypes;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Classe destinée à tester la classe "ActionAckCallback".
 * @author Valentin Sappa
 */
public class ActionAckCallbackTest
{
    private Game mockedGame;

    @Before
    public void init()
    {
        this.mockedGame = mock(Game.class);
    }

    @Test
    public void parseMakeAvailableAction() throws InterruptedException
    {
        ActionAckCallback actionAckCallback = new ActionAckCallback(new Object(), this.mockedGame, new Player());

        actionAckCallback.onSuccess(new String[] { ProtocolTypes.MakeAvailableAction.name() });
        Action result = actionAckCallback.getAction();

        assertTrue(result instanceof MakeAvailableAction);
    }

    @Test
    public void parseWorkAction() throws InterruptedException, RequestedInventorNotFoundException, RequestedInventionNotFoundException
    {
        when(this.mockedGame.getPlayerInventorByName(any(Player.class), anyString()))
                .thenReturn(new Inventor("Inventeur", new Knowledges(4, 3, 2, 1), Team.Bleu));
        when(this.mockedGame.getInventionByName(anyString()))
                .thenReturn(new Invention("Invention", new Knowledges(1, 2, 3, 4), 2));


        ActionAckCallback actionAckCallback = new ActionAckCallback(new Object(), this.mockedGame, new Player());

        actionAckCallback.onSuccess(new String[] { ProtocolTypes.WorkAction.name(), "Inventeur", "Invention" });
        Action result = actionAckCallback.getAction();

        verify(this.mockedGame).getPlayerInventorByName(any(Player.class), anyString());
        verify(this.mockedGame).getInventionByName(anyString());
        assertTrue(result instanceof WorkAction);
    }
}
