package engine.managers;

import engine.core.Invention;
import engine.core.Inventor;
import engine.core.Knowledges;
import engine.core.exceptions.AlreadyAvailableException;
import engine.core.exceptions.AlreadyBusyException;
import engine.managers.exceptions.DuplicateInventorException;
import engine.reflection.Singleton;
import org.junit.Assert;
import org.junit.Test;

/**
 * Classe destinée à tester la classe "InventorManager".
 * @author Noé Mourton-Comte
 */
public class InventorManagerTest
{
    @Test
    public void loadInventors() throws DuplicateInventorException
    {
        //Chargement des inventeurs... (pour le moment, il n'y en a que deux)
        Singleton.getInstance(InventorManager.class).load();

        Assert.assertTrue(Singleton.getInstance(InventorManager.class).getAvailableInventorsCount()
                == InventorManager.IMPLEMENTED_INVENTORS_COUNT);
    }

    @Test
    public void pickAllImplementedInventors() throws DuplicateInventorException
    {
        //Rechargement des inventeurs...
        Singleton.getInstance(InventorManager.class).load();

        for (int i = 0; i < InventorManager.IMPLEMENTED_INVENTORS_COUNT; i++) {
            Inventor inventor = Singleton.getInstance(InventorManager.class).pickInventor();
            Assert.assertNotNull(inventor);
        }
    }

    @Test
    public void pickMoreThanImplementedInventors() throws DuplicateInventorException
    {
        //Rechargement des inventeurs...
        Singleton.getInstance(InventorManager.class).load();

        for(int i = 0; i < InventorManager.IMPLEMENTED_INVENTORS_COUNT + 1; i++)
        {
            Inventor inventor = Singleton.getInstance(InventorManager.class).pickInventor();
            if(i < InventorManager.IMPLEMENTED_INVENTORS_COUNT)
                Assert.assertNotNull(inventor);
            else
                Assert.assertNull(inventor);
        }
    }

    @Test(expected=AlreadyAvailableException.class)
    public void makeAvailable() throws DuplicateInventorException, AlreadyAvailableException
    {
        //Rechargement des inventeurs...
        Singleton.getInstance(InventorManager.class).load();
        Inventor inventor = Singleton.getInstance(InventorManager.class).pickInventor();

        //Test de la variable busy (faux de base)
        Assert.assertFalse(inventor.isBusy());

        //Test de l'exception makeAvailable
        inventor.makeAvailable();
    }

    @Test(expected=AlreadyBusyException.class)
    public void makeBusy() throws AlreadyBusyException, DuplicateInventorException
    {
        //Rechargement des inventeurs...
        Singleton.getInstance(InventorManager.class).load();
        Inventor inventor = Singleton.getInstance(InventorManager.class).pickInventor();

        //Test de la variable busy (faux de base)
        Assert.assertFalse(inventor.isBusy());

        //Test qui occupe l'inventeur
        inventor.makeBusy();
        Assert.assertTrue(inventor.isBusy());

        //Test de l'exception makeBusy
        inventor.makeBusy();
    }

    @Test
    public void canWorkOn() throws DuplicateInventorException
    {
        //Rechargement des inventeurs et tirage d'un inventeur
        Singleton.getInstance(InventorManager.class).load();
        Inventor inventor = Singleton.getInstance(InventorManager.class).pickInventor();

        //Création de deux inventions
        Invention invention1 = new Invention("invention 1", new Knowledges(1,1,1,1), 2);
        Invention invention2 = new Invention("invention 2", new Knowledges(0,0,0,0), 1);

        Assert.assertTrue(inventor.canWorkOn(invention1));
        Assert.assertFalse(inventor.canWorkOn(invention2));
    }
}
