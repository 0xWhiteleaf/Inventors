package engine.actions;

import engine.core.Player;

/**
 * Classe abstraite représentant une action.
 * @author Noé Mourton-Comte
 */
public abstract class Action
{
    protected Player owner;

    protected Action(Player owner)
    {
        this.owner = owner;
    }

    /**
     * Méthode abstraite représentant l'exécution de l'action
     */
    abstract public void execute() throws Exception;
}
