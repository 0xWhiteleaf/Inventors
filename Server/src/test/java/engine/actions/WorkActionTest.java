package engine.actions;

import engine.actions.exceptions.BusyInventorException;
import engine.actions.exceptions.IncompatibleInventorException;
import engine.core.*;
import engine.core.exceptions.AlreadyBusyException;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Classe de test d'une "WorkAction"
 * @author Noé Mourton-Comte
 */
public class WorkActionTest
{
    private Player samplePlayer;
    private Inventor sampleInventor;
    private Invention sampleInvention;

    @Before
    public void initialize()
    {
        samplePlayer = new Player();

        Knowledges knowledges = new Knowledges(1, 2, 3, 4);

        sampleInventor = new Inventor("SampleInventor", knowledges, Team.Rouge);
        sampleInvention = new Invention("SampleInvention", knowledges, 2);
    }

    @Test
    public void doWorkAction() throws AlreadyBusyException, BusyInventorException, IncompatibleInventorException
    {
        WorkAction workAction = new WorkAction(samplePlayer, sampleInventor, sampleInvention);
        workAction.execute();

        assertTrue(sampleInventor.isBusy());
    }

    @Test(expected = BusyInventorException.class)
    public void doWorkActionWithBusyInventor() throws AlreadyBusyException, BusyInventorException, IncompatibleInventorException
    {
        //Nous rendons l'inventeur occupé...
        sampleInventor.makeBusy();

        WorkAction workActionWithBusyInventor = new WorkAction(samplePlayer, sampleInventor, sampleInvention);
        workActionWithBusyInventor.execute();
    }

    @Test(expected = IncompatibleInventorException.class)
    public void doWorkActionWithIncompatibleInventor() throws AlreadyBusyException, BusyInventorException, IncompatibleInventorException
    {
        Inventor incompatibleInventor = new Inventor("SampleIncompatibleInventor", new Knowledges(0, 0, 0, 0), Team.Rouge);

        WorkAction workActionWithIncompatibleInventor = new WorkAction(samplePlayer, incompatibleInventor, sampleInvention);
        workActionWithIncompatibleInventor.execute();
    }
}
