package engine.core;

import engine.core.exceptions.AlreadyAvailableException;
import engine.core.exceptions.AlreadyBusyException;

/**
 * Classe modélisant les inventeurs.
 * @author Louis Prud'homme & Alexandre Ciarafoni
 */
public class Inventor
{
    //MEMBERS
    /**
     * nom de l'inventeur.
     */
    private String name;
    /**
     * connaissances de l'inventeur.
     */
    private Knowledges knowledges;

    private boolean isBusy;

    //Équipe de l'inventeur.
    private Team team;

    //METHODS
    //constructeur

    /**
     * Constructeur renvoyant une instance
     * @param name nom de l'inventeur
     * @param knowledges structure décrivant les connaissances de base de l'inventeur.
     * @param team énumération : Bleu, Jaune, Vert ou Rouge.
     */
    public Inventor(String name, Knowledges knowledges, Team team)
    {
        this.name = name;
        this.knowledges = knowledges;
        this.team = team;
    }
    //get-set

    /**
     * Accesseur du nom de l'inventeur.
     * @return le nom de l'inventeur.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Accesseur des connaissances de l'inventeur.
     * @return structure Knowledges dont les champs représentent les différentes connaissances de l'inventeur.
     */
    public Knowledges getKnowledges()
    {
        return knowledges;
    }

    /**
     * Accesseur de l'équipe de l'inventeur.
     * @return énumération : Bleu, Jaune, Vert ou Rouge.
     */
    public Team getTeam()
    {
        return team;
    }

    /**
     * Fonction permettant de savoir si l'inventeur est occupé ou non
     * @return
     */
    public boolean isBusy()
    {
        return this.isBusy;
    }

    /**
     * Méthode permettant de rendre un inventeur occupé
     * @throws AlreadyBusyException
     */
    public void makeBusy() throws AlreadyBusyException
    {
        if(!this.isBusy)
            this.isBusy = true;
        else
            throw new AlreadyBusyException("Impossible de rendre un inventeur occupé lorsque celui-ci est déjà occupé.");
    }

    /**
     * Méthode permettant de rendre un inventeur disponible
     * @throws AlreadyAvailableException
     */
    public void makeAvailable() throws AlreadyAvailableException
    {
        if(this.isBusy)
            this.isBusy = false;
        else
            throw new AlreadyAvailableException("Impossible de rendre un inventeur disponible lorsque celui-ci est déjà disponible.");
    }

    /**
     * Permet de savoir si l'inventeur peut travailler sur une invention particulière.
     * @param invention invention sur laquelle on veut faire travailler l'inventeur.
     * @return un boolean indiquant si l'inventeur peut travailler sur l'invention.
     */
    public boolean canWorkOn(Invention invention)
    {
        return this.getGrade(invention) > 0;
    }

    /**
     * Permet de connaître la note de l'inventeur relativement à une invention.
     * @param invention invention sur laquelle on veut évaluer l'inventeur.
     * @return la note sous forme d'entier.
     */
    public int getGrade(Invention invention)
    {
        int rank = 0;

        Knowledges inventorKnowledges = this.getKnowledges();

        rank += inventorKnowledges.getPhys() * (invention.getRequiredKnowledges().getPhys() - invention.getActualKnowledges().getPhys());
        rank += inventorKnowledges.getChem() * (invention.getRequiredKnowledges().getChem() - invention.getActualKnowledges().getChem());
        rank += inventorKnowledges.getMech() * (invention.getRequiredKnowledges().getMech() - invention.getActualKnowledges().getMech());
        rank += inventorKnowledges.getMath() * (invention.getRequiredKnowledges().getMath() - invention.getActualKnowledges().getMath());

        return rank;
    }
}
