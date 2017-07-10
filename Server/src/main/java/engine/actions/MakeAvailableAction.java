package engine.actions;

import engine.actions.exceptions.UnauthorizedActionException;
import engine.core.Inventor;
import engine.core.Player;
import engine.core.exceptions.AlreadyAvailableException;

/**
 * Classe représentant l'action "Rendre disponible la totalité de ses inventeurs occupés".
 * @author Noé Mourton-Comte
 */
public class MakeAvailableAction extends Action
{
    /***
     * Constructeur de la classe MakeAvailableAction
     * @param owner Initiateur de l'action.
     */
    public MakeAvailableAction(Player owner)
    {
        super(owner);
    }

    @Override
    public void execute() throws AlreadyAvailableException, UnauthorizedActionException
    {
        if(super.owner.getBusyInventors().size() >= 1)
        {
            //Nous rendons disponible la totalité des inventeurs occupés
            for (Inventor inventor : super.owner.getBusyInventors())
            {
                inventor.makeAvailable();
            }
        }
        else
        {
            throw new UnauthorizedActionException("Impossible d'effectuer cette action si tous les inventeurs du joueur sont disponibles");
        }
    }

    @Override
    public String toString()
    {
        return "rendre disponible la totalité de ses inventeurs occupés.";
    }
}
