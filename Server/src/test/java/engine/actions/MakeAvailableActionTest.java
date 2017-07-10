package engine.actions;

import engine.actions.exceptions.UnauthorizedActionException;
import engine.core.Inventor;
import engine.core.Knowledges;
import engine.core.Player;
import engine.core.Team;
import engine.core.exceptions.AlreadyAvailableException;
import engine.core.exceptions.AlreadyBusyException;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Classe de test d'une "MakeAvailableAction"
 * @author Noé Mourton-Comte
 */
public class MakeAvailableActionTest
{
    private Player samplePlayer;
    private Inventor sampleInventor;

    @Before
    public void initialize() throws AlreadyBusyException
    {
        Knowledges knowledges = new Knowledges(1, 2, 3, 4);

        samplePlayer = new Player();

        sampleInventor = new Inventor("SampleInventor", knowledges, Team.Bleu);
        samplePlayer.addInventor(sampleInventor);
    }

    @Test
    public void doMakeAvailableAction() throws UnauthorizedActionException, AlreadyAvailableException, AlreadyBusyException
    {
        //Nous rendons l'inventeur occupé...
        sampleInventor.makeBusy();

        MakeAvailableAction makeAvailableAction = new MakeAvailableAction(samplePlayer);
        makeAvailableAction.execute();

        assertFalse(sampleInventor.isBusy());
    }

    @Test(expected = UnauthorizedActionException.class)
    public void doMakeAvailableActionWhenAllInventorsAreAvailable() throws UnauthorizedActionException, AlreadyAvailableException
    {
        //Par défaut, un inventeur est disponible.
        MakeAvailableAction makeAvailableAction = new MakeAvailableAction(samplePlayer);
        makeAvailableAction.execute();
    }
}
