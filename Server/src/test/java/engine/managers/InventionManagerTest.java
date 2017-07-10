package engine.managers;

import engine.core.Invention;
import engine.managers.exceptions.DuplicateInventionException;
import engine.reflection.Singleton;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Classe de test de la classe InventionManager
 * @author Louis Prud'homme, Valentin Sappa
 */
public class InventionManagerTest
{
    /**
     * Permet de vérifier si l'on peut charger les inventions depuis InventionManager.
     * @throws Exception
     */
    @Test
    public void loadInventions() throws DuplicateInventionException
    {
        Singleton.getInstance(InventionManager.class).load();

        Assert.assertTrue(Singleton.getInstance(InventionManager.class).getAvalaibleInventionsCount()
                == InventionManager.IMPLEMENTED_INVENTIONS_COUNT);
    }

    /**
     * Permet de vérifier si l'on peut 'piocher' toutes les inventions.
     * @throws Exception
     */
    @Test
    public void pickAllInventions() throws DuplicateInventionException
    {
        Singleton.getInstance(InventionManager.class).load();

        for(int i = 0; i < InventionManager.IMPLEMENTED_INVENTIONS_COUNT; i++)
        {
            Invention test = Singleton.getInstance(InventionManager.class).pickInvention();
            assertNotNull(test);
        }
    }

    /**
     * Permet de vérifier si l'on ne peut pas piocher plus d'invention que l'on en a déclaré.
     * @throws Exception
     */
    @Test
    public void pickTooMuchInventions() throws DuplicateInventionException
    {
        Singleton.getInstance(InventionManager.class).load();

        for (int i = 0; i < InventionManager.IMPLEMENTED_INVENTIONS_COUNT + 1; i++) {
            Invention test = Singleton.getInstance(InventionManager.class).pickInvention();
            if (i < InventionManager.IMPLEMENTED_INVENTIONS_COUNT) {
                assertNotNull(test);
            } else {
                assertNull(test);
            }
        }
    }

    /**
     * Permet de vérifier si l'on peut tirer une invention d'une époque spécifique.
     * @throws DuplicateInventionException
     */
    @Test
    public void pickInventionsForSpecificEra() throws DuplicateInventionException
    {
        Singleton.getInstance(InventionManager.class).load();

        Invention inventionOfTheFirstEra = Singleton.getInstance(InventionManager.class).pickInvention(1);
        Invention inventionOfTheSecondEra = Singleton.getInstance(InventionManager.class).pickInvention(2);
        Invention inventionOfTheThirdEra = Singleton.getInstance(InventionManager.class).pickInvention(3);

        assertNotNull(inventionOfTheFirstEra);
        assertEquals(inventionOfTheFirstEra.getEra(), 1);

        assertNotNull(inventionOfTheSecondEra);
        assertEquals(inventionOfTheSecondEra.getEra(), 2);

        assertNotNull(inventionOfTheThirdEra);
        assertEquals(inventionOfTheThirdEra.getEra(), 3);
    }
}
