package engine.core;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Classe de test "Player"
 * @author Valentin Sappa
 */
public class PlayersTest
{
    @Test
    public void createPlayer()
    {
        Player player = new Player();
        assertEquals(player.getInventors().size(), 0);
    }

    @Test
    public void addInventorToPlayer()
    {
        Player player = new Player();

        Inventor inventor1 = new Inventor("Inventeur1", new Knowledges(0, 0, 0, 0), Team.Bleu);
        Inventor inventor2 = new Inventor("Inventeur2", new Knowledges(1, 1, 1, 1), Team.Rouge);

        player.addInventor(inventor1);
        player.addInventor(inventor2);

        Assert.assertTrue(inventor1 == player.getInventors().get(0));
        Assert.assertTrue(inventor2 == player.getInventors().get(1));
    }

    @Test
    public void getPlayerInventors()
    {
        Player player = new Player();
        Inventor inventor = new Inventor("Inventeur", new Knowledges(0, 0, 0, 0), Team.Bleu);

        Assert.assertTrue(player.getInventors().size() == 0);
        player.addInventor(inventor);
        Assert.assertTrue(player.getInventors().size() == 1 && player.getInventors().get(0) == inventor) ;
    }
}
