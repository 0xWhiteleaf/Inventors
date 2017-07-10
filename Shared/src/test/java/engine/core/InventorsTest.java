package engine.core;

import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Classe destinée à tester la classe "Inventor".
 * @author Noé Mourton-Comte, Valentin Sappa
 */
public class InventorsTest
{
    @Test
    public void createBasicInventor()
    {
        String inventorName = "BasicInventor";
        Knowledges inventorKnowledges = new Knowledges(1, 2, 3, 4);
        Team inventorTeam = Team.Bleu;

        Inventor basicInventor = new Inventor(inventorName, inventorKnowledges, inventorTeam);

        assertEquals(basicInventor.getName(), inventorName);
        assertTrue(basicInventor.getKnowledges().getPhys() == 1);
        assertTrue(basicInventor.getKnowledges().getChem() == 2);
        assertTrue(basicInventor.getKnowledges().getMech() == 3);
        assertTrue(basicInventor.getKnowledges().getMath() == 4);
    }

    @Test
    public void inventorCanOrCantWork()
    {
        Invention invention = mock(Invention.class);
        when(invention.getRequiredKnowledges()).thenReturn(new Knowledges(1, 0, 3, 4));
        when(invention.getActualKnowledges()).thenReturn(new Knowledges(0, 0, 0, 2));

        Inventor inventor = mock(Inventor.class);
        when(inventor.getGrade(any())).thenCallRealMethod();
        when(inventor.canWorkOn(any())).thenCallRealMethod();

        when(inventor.getKnowledges())
                .thenReturn(new Knowledges(0, 0, 0, 0))
                .thenReturn(new Knowledges(1, 0, 0, 0))
                .thenReturn(new Knowledges(0, 1, 0, 0))
                .thenReturn(new Knowledges(0, 0, 0, 3));

        assertEquals(inventor.canWorkOn(invention), false);
        assertEquals(inventor.canWorkOn(invention), true);
        assertEquals(inventor.canWorkOn(invention), false);
        assertEquals(inventor.canWorkOn(invention), true);
    }
}
