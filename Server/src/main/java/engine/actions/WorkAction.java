package engine.actions;

import engine.actions.exceptions.BusyInventorException;
import engine.actions.exceptions.IncompatibleInventorException;
import engine.core.Invention;
import engine.core.Inventor;
import engine.core.Player;
import engine.core.exceptions.AlreadyBusyException;

/**
 * Classe représentant l'action "Faire travailler un inventeur".
 * @author Noé Mourton-Comte
 */
public class WorkAction extends Action
{
    private Inventor workingInventor;
    private Invention targetedInvention;

    /**
     * Constructeur de la classe WorkAction
     * @param owner Initiateur de l'action.
     * @param inventor Inventeur à faire travailler
     * @param invention Invention sur laquelle l'inventeur doit travailler
     */
    public WorkAction(Player owner, Inventor inventor, Invention invention)
    {
        super(owner);
        this.workingInventor = inventor;
        this.targetedInvention = invention;
    }

    @Override
    public void execute() throws IncompatibleInventorException, BusyInventorException, AlreadyBusyException
    {
        if(!this.workingInventor.isBusy())
        {
            //Nous vérifions si l'inventeur peut travailler sur cette invention
            if(this.workingInventor.canWorkOn(this.targetedInvention))
            {
                //Ajout des compétences de l'inventeur sur l'invention
                this.targetedInvention.updateActualKnowledges(this.owner, this.workingInventor.getKnowledges());

                //Nous rendons l'inventeur occupé
                this.workingInventor.makeBusy();
            }
            else
            {
                throw new IncompatibleInventorException(String.format("Impossible de faire travailler l'inventeur: %s sur l'invention: %s",
                        this.workingInventor.getName(), this.targetedInvention.getName()));
            }
        }
        else
        {
            throw new BusyInventorException("Impossible de faire travailler un inventeur lorsqu'il est déjà occupé.");
        }
    }

    @Override
    public String toString()
    {
        return String.format("faire travailler l'inventeur '%s' (%d, %d, %d, %d) sur l'invention '%s' (physique: %d/%d, chimie: %d/%d, mécanique: %d/%d, mathématiques: %d/%d).",
                this.workingInventor.getName(),
                this.workingInventor.getKnowledges().getPhys(), this.workingInventor.getKnowledges().getChem(),
                this.workingInventor.getKnowledges().getMech(), this.workingInventor.getKnowledges().getMath(),
                this.targetedInvention.getName(),
                this.targetedInvention.getActualKnowledges().getPhys(), this.targetedInvention.getRequiredKnowledges().getPhys(),
                this.targetedInvention.getActualKnowledges().getChem(), this.targetedInvention.getRequiredKnowledges().getChem(),
                this.targetedInvention.getActualKnowledges().getMech(), this.targetedInvention.getRequiredKnowledges().getMech(),
                this.targetedInvention.getActualKnowledges().getMath(), this.targetedInvention.getRequiredKnowledges().getMath());
    }


    /**
     * Permet de retourner l'invention ciblée par l'action
     * @return l'invention ciblée
     */
    public Invention getTargetedInvention()
    {
        return this.targetedInvention;
    }
}
