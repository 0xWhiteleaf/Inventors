package engine.ai.strategies.providers;

import engine.core.Invention;

import java.util.ArrayList;

/**
 * Interface d'un "fournisseur" d'inventions.
 * @author Noé Mourton-Comte
 */
public interface InventionProvider
{
    ArrayList<Invention> getInventions();
}
